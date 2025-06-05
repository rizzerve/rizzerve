package ktwo.rizzerve.service;

import ktwo.rizzerve.model.*;
import ktwo.rizzerve.repository.ZZZ_CartRepository;
import ktwo.rizzerve.repository.OrderRepository;
import ktwo.rizzerve.repository.TableRepository;
import ktwo.rizzerve.enums.OrderStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ZZZ_CheckoutServiceTest {

    @Mock
    private ZZZ_CartRepository cartRepository;
    @Mock
    private OrderRepository orderRepository; // JPA repo for Order
    @Mock
    private MenuItemService menuItemService; // Service to get MenuItem by ID
    @Mock
    private TableRepository tableRepository;   // JPA repo for Table

    @InjectMocks
    private ZZZ_CheckoutService checkoutService;

    private Long customerId;
    private Long tableId;
    private String username;
    private Long cartId;
    private ZZZ_Cart testCart;
    private MenuItem item1;
    private Long item1Id;
    private Table testTable;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        tableId = 10L;
        username = "testUser";
        cartId = 99L;

        testCart = new ZZZ_Cart(username, customerId, tableId);
        testCart.setId(cartId);

        Category dummyCategory = new Category();
        dummyCategory.setId(1L);

        item1Id = 101L;
        item1 = MenuItem.builder().name("Coke").price(new BigDecimal("2.00")).available(true).category(dummyCategory).build();
        item1.setId(item1Id);

        testTable = new Table(tableId, "Table A");
    }

    // --- Test getOrCreateCart ---
    @Test
    void getOrCreateCart_whenCartExists_shouldReturnExisting() {
        when(cartRepository.findByCustomerIdAndTableId(customerId, tableId)).thenReturn(Optional.of(testCart));
        ZZZ_Cart result = checkoutService.getOrCreateCart(customerId, tableId, username);
        assertSame(testCart, result);
        verify(cartRepository, never()).save(any(ZZZ_Cart.class));
    }

    @Test
    void getOrCreateCart_whenCartNotExists_shouldCreateAndSaveNew() {
        when(cartRepository.findByCustomerIdAndTableId(customerId, tableId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(ZZZ_Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ZZZ_Cart result = checkoutService.getOrCreateCart(customerId, tableId, username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(tableId, result.getTableId());
        verify(cartRepository).save(result);
    }

    // --- Test addItemToCart ---
    @Test
    void addItemToCart_validItem_shouldAddItemAndSave() {
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(menuItemService.getById(item1Id)).thenReturn(item1);
        when(cartRepository.save(testCart)).thenReturn(testCart);

        ZZZ_Cart result = checkoutService.addItemToCart(cartId, item1Id, 2);

        assertTrue(result.getFoodItems().containsKey(item1Id));
        assertEquals(2, result.getFoodItems().get(item1Id));
        verify(cartRepository).save(testCart);
    }

    @Test
    void addItemToCart_itemNotAvailable_shouldThrowException() {
        item1.setAvailable(false); // Make item unavailable
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(menuItemService.getById(item1Id)).thenReturn(item1);

        assertThrows(IllegalArgumentException.class, () -> {
            checkoutService.addItemToCart(cartId, item1Id, 1);
        });
        verify(cartRepository, never()).save(any(ZZZ_Cart.class));
    }

    @Test
    void addItemToCart_cartNotFound_shouldThrowException() {
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            checkoutService.addItemToCart(cartId, item1Id, 1);
        });
    }


    // --- Test updateItemQuantityInCart ---
    @Test
    void updateItemQuantityInCart_validUpdate_shouldUpdateAndSave() {
        testCart.addItem(item1Id, 1); // Pre-add item
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        // No need to mock menuItemService if item already in cart or quantity > 0
        when(cartRepository.save(testCart)).thenReturn(testCart);

        ZZZ_Cart result = checkoutService.updateItemQuantityInCart(cartId, item1Id, 3);

        assertEquals(3, result.getFoodItems().get(item1Id));
        verify(cartRepository).save(testCart);
    }

    @Test
    void updateItemQuantityInCart_addNewItemWithPositiveQuantity_shouldCheckAvailability() {
        // Cart does not contain item1Id yet.
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(menuItemService.getById(item1Id)).thenReturn(item1); // Item is valid and available
        when(cartRepository.save(testCart)).thenReturn(testCart);

        ZZZ_Cart result = checkoutService.updateItemQuantityInCart(cartId, item1Id, 1);
        assertTrue(result.getFoodItems().containsKey(item1Id));
        verify(menuItemService).getById(item1Id); // Ensure item was checked
    }

    @Test
    void updateItemQuantityInCart_addNewItemButUnavailable_shouldThrowException() {
        item1.setAvailable(false);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(menuItemService.getById(item1Id)).thenReturn(item1);

        assertThrows(IllegalArgumentException.class, () -> {
            checkoutService.updateItemQuantityInCart(cartId, item1Id, 1);
        });
    }

    // --- Test removeItemFromCart ---
    @Test
    void removeItemFromCart_shouldRemoveAndSave() {
        testCart.addItem(item1Id, 1);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        ZZZ_Cart result = checkoutService.removeItemFromCart(cartId, item1Id);

        assertFalse(result.getFoodItems().containsKey(item1Id));
        verify(cartRepository).save(testCart);
    }

    // --- Test getCartDetailsForView ---
    @Test
    void getCartDetailsForView_shouldReturnCorrectDetails() {
        testCart.addItem(item1Id, 2); // 2 Cokes @ 2.00 each = 4.00
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(menuItemService.getById(item1Id)).thenReturn(item1);

        Map<String, Object> details = checkoutService.getCartDetailsForView(cartId);

        assertEquals(testCart, details.get("cart"));
        assertEquals(new BigDecimal("4.00"), details.get("total"));
        assertTrue(((Map<Long, MenuItem>) details.get("resolvedItemsMap")).containsKey(item1Id));
    }

    @Test
    void getCartDetailsForView_emptyCart_shouldReturnZeroTotal() {
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart)); // testCart is empty
        // menuItemService.getById should not be called for an empty cart's foodItems.keySet()

        Map<String, Object> details = checkoutService.getCartDetailsForView(cartId);
        assertEquals(BigDecimal.ZERO, details.get("total"));
        assertTrue(((Map<?,?>)details.get("resolvedItemsMap")).isEmpty());
    }


    // --- Test processCheckoutAndFinalizeOrder ---
    @Test
    void processCheckoutAndFinalizeOrder_validCart_shouldSaveOrderAndDeleteCart() {
        testCart.addItem(item1Id, 1);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(tableRepository.findById(tableId)).thenReturn(Optional.of(testTable));
        when(menuItemService.getById(item1Id)).thenReturn(item1);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setOrderId(500L); // Simulate DB generating ID
            return o;
        });

        Order finalizedOrder = checkoutService.processCheckoutAndFinalizeOrder(cartId);

        assertNotNull(finalizedOrder);
        assertEquals(500L, finalizedOrder.getOrderId());
        assertEquals(OrderStatus.AWAITING_PAYMENT, finalizedOrder.getStatus());
        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).deleteById(cartId);
    }

    @Test
    void processCheckoutAndFinalizeOrder_emptyCart_shouldThrowException() {
        // testCart is empty by default for this test
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));

        assertThrows(IllegalStateException.class, () -> {
            checkoutService.processCheckoutAndFinalizeOrder(cartId);
        });
    }

    @Test
    void processCheckoutAndFinalizeOrder_tableNotFound_shouldThrowException() {
        testCart.addItem(item1Id, 1);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(tableRepository.findById(tableId)).thenReturn(Optional.empty()); // Table not found

        assertThrows(IllegalArgumentException.class, () -> {
            checkoutService.processCheckoutAndFinalizeOrder(cartId);
        });
    }
}