package ktwo.rizzerve.model;

import ktwo.rizzerve.enums.OrderStatus; // If needed for Order comparison
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

class ZZZ_CartTest {

    private ZZZ_Cart cart;
    private final Long item1Id = 1L;
    private final Long item2Id = 2L;
    private MenuItem menuItem1;
    private MenuItem menuItem2;
    private Map<Long, MenuItem> resolvedMenuItems;
    private Table testTable;

    @BeforeEach
    void setUp() {
        cart = new ZZZ_Cart("testUser", 100L, 200L);
        testTable = new Table(200L, "Table A"); // Assuming Table constructor

        Category dummyCategory = new Category(); // Non-modifiable, so we use it
        dummyCategory.setId(1L);
        dummyCategory.setName("Drinks");

        menuItem1 = MenuItem.builder().name("Coke").price(new BigDecimal("2.50")).category(dummyCategory).build();
        menuItem1.setId(item1Id); // Manually set ID

        menuItem2 = MenuItem.builder().name("Pizza Slice").price(new BigDecimal("5.00")).category(dummyCategory).build();
        menuItem2.setId(item2Id);

        resolvedMenuItems = new HashMap<>();
        resolvedMenuItems.put(item1Id, menuItem1);
        resolvedMenuItems.put(item2Id, menuItem2);
    }

    // --- Test addItem ---
    @Test
    void addItem_newItem_shouldAddWithCorrectQuantity() {
        cart.addItem(item1Id, 2);
        assertEquals(2, cart.getFoodItems().get(item1Id));
    }

    @Test
    void addItem_existingItem_shouldMergeQuantity() {
        cart.addItem(item1Id, 1);
        cart.addItem(item1Id, 2);
        assertEquals(3, cart.getFoodItems().get(item1Id));
    }

    @Test
    void addItem_zeroOrNegativeQuantity_shouldNotAddItem() {
        cart.addItem(item1Id, 0);
        assertFalse(cart.getFoodItems().containsKey(item1Id));

        cart.addItem(item1Id, -1);
        assertFalse(cart.getFoodItems().containsKey(item1Id));
    }

    // --- Test updateItemQuantity ---
    @Test
    void updateItemQuantity_positiveQuantity_shouldUpdateQuantity() {
        cart.addItem(item1Id, 1);
        cart.updateItemQuantity(item1Id, 3);
        assertEquals(3, cart.getFoodItems().get(item1Id));
    }

    @Test
    void updateItemQuantity_zeroQuantity_shouldRemoveItem() {
        cart.addItem(item1Id, 2);
        cart.updateItemQuantity(item1Id, 0);
        assertFalse(cart.getFoodItems().containsKey(item1Id));
    }

    @Test
    void updateItemQuantity_negativeQuantity_shouldRemoveItem() {
        cart.addItem(item1Id, 2);
        cart.updateItemQuantity(item1Id, -1);
        assertFalse(cart.getFoodItems().containsKey(item1Id));
    }

    // --- Test removeItem ---
    @Test
    void removeItem_existingItem_shouldRemove() {
        cart.addItem(item1Id, 2);
        cart.removeItem(item1Id);
        assertFalse(cart.getFoodItems().containsKey(item1Id));
    }

    @Test
    void removeItem_nonExistingItem_shouldDoNothing() {
        cart.removeItem(item1Id); // Item not in cart
        assertTrue(cart.getFoodItems().isEmpty());
    }

    // --- Test calculateTotal ---
    @Test
    void calculateTotal_emptyCart_shouldReturnZero() {
        assertEquals(BigDecimal.ZERO, cart.calculateTotal(resolvedMenuItems));
    }

    @Test
    void calculateTotal_singleItem_shouldReturnCorrectTotal() {
        cart.addItem(item1Id, 2); // 2 Cokes @ 2.50 each
        assertEquals(new BigDecimal("5.00"), cart.calculateTotal(resolvedMenuItems));
    }

    @Test
    void calculateTotal_multipleItems_shouldReturnCorrectTotal() {
        cart.addItem(item1Id, 1); // 1 Coke @ 2.50
        cart.addItem(item2Id, 2); // 2 Pizza Slices @ 5.00 each
        // Total = 2.50 + 10.00 = 12.50
        assertEquals(new BigDecimal("12.50"), cart.calculateTotal(resolvedMenuItems));
    }

    @Test
    void calculateTotal_withItemNotResolved_shouldIgnoreUnresolvedItem() {
        cart.addItem(item1Id, 1);
        cart.addItem(3L, 1); // Item 3L is not in resolvedMenuItems
        assertEquals(new BigDecimal("2.50"), cart.calculateTotal(resolvedMenuItems));
    }

    @Test
    void calculateTotal_withItemHavingNullPrice_shouldTreatPriceAsZero() {
        MenuItem itemWithNullPrice = MenuItem.builder().name("Free Water").price(null).build();
        itemWithNullPrice.setId(3L);
        resolvedMenuItems.put(3L, itemWithNullPrice);

        cart.addItem(item1Id, 1); // 2.50
        cart.addItem(3L, 1);    // 0.00

        assertEquals(new BigDecimal("2.50"), cart.calculateTotal(resolvedMenuItems));
    }

    // --- Test convertToOrder ---
    @Test
    void convertToOrder_shouldCreateOrderWithCorrectDetails() {
        cart.addItem(item1Id, 2); // 2 Cokes
        cart.addItem(item2Id, 1); // 1 Pizza Slice

        Order order = cart.convertToOrder(testTable, resolvedMenuItems);

        assertNotNull(order);
        assertEquals("testUser", order.getUsername());
        assertEquals(testTable, order.getTable());
        assertEquals(OrderStatus.AWAITING_PAYMENT, order.getStatus()); // Default status

        Map<MenuItem, Integer> orderItems = order.getItems();
        assertNotNull(orderItems);
        assertEquals(2, orderItems.size());
        assertEquals(2, orderItems.get(menuItem1).intValue());
        assertEquals(1, orderItems.get(menuItem2).intValue());
    }

    @Test
    void convertToOrder_emptyCart_shouldCreateOrderWithNoItems() {
        Order order = cart.convertToOrder(testTable, resolvedMenuItems);

        assertNotNull(order);
        assertTrue(order.getItems().isEmpty());
    }

    // --- Test clearItems ---
    @Test
    void clearItems_shouldRemoveAllItemsFromCart() {
        cart.addItem(item1Id, 1);
        cart.addItem(item2Id, 1);
        assertFalse(cart.getFoodItems().isEmpty());

        cart.clearItems();
        assertTrue(cart.getFoodItems().isEmpty());
    }
}