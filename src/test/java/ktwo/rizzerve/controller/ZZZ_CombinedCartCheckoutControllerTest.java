package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.model.Order;
import ktwo.rizzerve.model.Table;
import ktwo.rizzerve.model.ZZZ_Cart;
import ktwo.rizzerve.repository.ZZZ_CartRepository;
import ktwo.rizzerve.service.ZZZ_CheckoutService;
import ktwo.rizzerve.enums.OrderStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser; // For more complex security scenarios if needed
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration; // To exclude
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // For CSRF token
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

// Exclude Spring Security's auto-configuration for these focused controller tests
@WebMvcTest(controllers = ZZZ_CombinedCartCheckoutController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class ZZZ_CombinedCartCheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ZZZ_CheckoutService checkoutService;

    @MockBean
    private ZZZ_CartRepository cartRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long customerId;
    private Long tableId;
    private String username;
    private Long cartId;
    private ZZZ_Cart sampleCart;
    private Table sampleTableObject; // Renamed for clarity
    private MenuItem sampleItem;
    private Long sampleItemId;
    private Map<String, Object> sampleCartDetails;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        tableId = 10L;
        username = "testUser";
        cartId = 99L;
        sampleItemId = 101L;

        sampleTableObject = new Table(tableId, "Table A"); // Assuming Table has (Long id, String name) constructor
        sampleCart = new ZZZ_Cart(username, customerId, tableId);
        sampleCart.setId(cartId);

        sampleItem = MenuItem.builder().name("Test Item").price(BigDecimal.TEN).build();
        sampleItem.setId(sampleItemId);

        sampleCartDetails = new HashMap<>();
        sampleCartDetails.put("cart", sampleCart);
        sampleCartDetails.put("resolvedItemsMap", Map.of(sampleItemId, sampleItem));
        sampleCartDetails.put("total", BigDecimal.ZERO);
    }

    @Test
    void viewCheckoutPage_success_shouldReturnViewWithCartDetails() throws Exception {
        when(checkoutService.getOrCreateCart(customerId, tableId, username)).thenReturn(sampleCart);
        when(checkoutService.getCartDetailsForView(cartId)).thenReturn(sampleCartDetails);

        mockMvc.perform(get("/zzz/cart/checkout-page")
                        .param("customerId", String.valueOf(customerId))
                        .param("tableId", String.valueOf(tableId))
                        .param("username", username))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(view().name("zzz_checkout_user"))
                .andExpect(model().attribute("cartId", cartId))
                .andExpect(model().attribute("cart", sampleCart))
                .andExpect(model().attribute("total", BigDecimal.ZERO));
    }

    @Test
    void viewCheckoutPage_serviceError_shouldReturnViewWithErrorAttribute() throws Exception {
        when(checkoutService.getOrCreateCart(customerId, tableId, username))
                .thenThrow(new RuntimeException("Service unavailable"));

        mockMvc.perform(get("/zzz/cart/checkout-page")
                        .param("customerId", String.valueOf(customerId))
                        .param("tableId", String.valueOf(tableId))
                        .param("username", username))
                .andExpect(status().isOk()) // Still expect 200 as controller handles error
                .andExpect(view().name("zzz_checkout_user"))
                .andExpect(model().attribute("error", "Error loading cart: Service unavailable"));
    }

    @Test
    void addItemToCart_api_success_shouldReturnUpdatedCartDetails() throws Exception {
        sampleCart.addItem(sampleItemId, 1);
        Map<String, Object> updatedDetails = new HashMap<>(sampleCartDetails);
        updatedDetails.put("total", BigDecimal.TEN);

        when(checkoutService.addItemToCart(cartId, sampleItemId, 1)).thenReturn(sampleCart);
        when(checkoutService.getCartDetailsForView(cartId)).thenReturn(updatedDetails);

        mockMvc.perform(post("/zzz/cart/{cartId}/items/add", cartId)
                        .param("menuItemId", String.valueOf(sampleItemId))
                        .param("quantity", "1")
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cart.id", is(cartId.intValue())))
                .andExpect(jsonPath("$.total", is(10.0)));

        verify(checkoutService).addItemToCart(cartId, sampleItemId, 1);
        verify(checkoutService, times(1)).getCartDetailsForView(cartId);
    }

    @Test
    void addItemToCart_api_serviceError_shouldReturnBadRequest() throws Exception {
        when(checkoutService.addItemToCart(cartId, sampleItemId, 1))
                .thenThrow(new IllegalArgumentException("Item invalid"));

        mockMvc.perform(post("/zzz/cart/{cartId}/items/add", cartId)
                        .param("menuItemId", String.valueOf(sampleItemId))
                        .param("quantity", "1")
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isBadRequest()) // Expect 400 Bad Request
                .andExpect(jsonPath("$.error", is("Item invalid")));
    }

    @Test
    void updateItemQuantity_api_success() throws Exception {
        sampleCart.updateItemQuantity(sampleItemId, 2);
        Map<String, Object> updatedDetails = new HashMap<>(sampleCartDetails);
        updatedDetails.put("total", new BigDecimal("20.0"));
        when(checkoutService.updateItemQuantityInCart(cartId, sampleItemId, 2)).thenReturn(sampleCart);
        when(checkoutService.getCartDetailsForView(cartId)).thenReturn(updatedDetails);

        mockMvc.perform(post("/zzz/cart/{cartId}/items/update", cartId)
                        .param("menuItemId", String.valueOf(sampleItemId))
                        .param("quantity", "2")
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(jsonPath("$.total", is(20.0)));
        verify(checkoutService).updateItemQuantityInCart(cartId, sampleItemId, 2);
    }

    @Test
    void proceedToCheckout_success_shouldRedirectToConfirmation() throws Exception {
        Order finalizedOrder = new Order(username, sampleTableObject); // Use the initialized sampleTableObject
        finalizedOrder.setOrderId(500L);
        finalizedOrder.setStatus(OrderStatus.AWAITING_PAYMENT);

        when(checkoutService.processCheckoutAndFinalizeOrder(cartId)).thenReturn(finalizedOrder);

        mockMvc.perform(post("/zzz/cart/{cartId}/proceed-to-checkout", cartId)
                        .with(csrf())) // Add CSRF token
                .andExpect(status().is3xxRedirection()) // Expect Redirection
                .andExpect(redirectedUrl("/zzz/cart/order-confirmation"))
                .andExpect(flash().attribute("order", finalizedOrder));
    }

    @Test
    void proceedToCheckout_serviceError_shouldRedirectBackToCheckoutPage() throws Exception {
        when(checkoutService.processCheckoutAndFinalizeOrder(cartId))
                .thenThrow(new IllegalStateException("Cart empty"));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(sampleCart));

        mockMvc.perform(post("/zzz/cart/{cartId}/proceed-to-checkout", cartId)
                        .with(csrf())) // Add CSRF token
                .andExpect(status().is3xxRedirection()) // Expect Redirection
                .andExpect(redirectedUrl(String.format("/zzz/cart/checkout-page?customerId=%d&tableId=%d&username=%s", customerId, tableId, username)))
                .andExpect(flash().attribute("error", "Checkout failed: Cart empty"));
    }

    @Test
    void proceedToCheckout_serviceErrorCartNotFoundForRedirect_shouldRedirectToRoot() throws Exception {
        when(checkoutService.processCheckoutAndFinalizeOrder(cartId))
                .thenThrow(new IllegalStateException("Cart empty"));
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/zzz/cart/{cartId}/proceed-to-checkout", cartId)
                        .with(csrf())) // Add CSRF token
                .andExpect(status().is3xxRedirection()) // Expect Redirection
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Checkout failed: Cart empty"));
    }

    @Test
    void orderConfirmationPage_withOrderInFlash_shouldDisplayConfirmation() throws Exception {
        Order order = new Order(username, sampleTableObject); // Use the initialized sampleTableObject
        order.setOrderId(500L);
        order.setStatus(OrderStatus.AWAITING_PAYMENT);
        MenuItem item = MenuItem.builder().name("Coke").price(new BigDecimal("2.50")).build();
        item.setId(1L);
        order.addItem(item, 2);

        mockMvc.perform(get("/zzz/cart/order-confirmation").flashAttr("order", order))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(view().name("zzz_order_confirmation"))
                .andExpect(model().attribute("order", order))
                .andExpect(model().attribute("totalPrice", new BigDecimal("5.00")));
    }

    @Test
    void orderConfirmationPage_withoutOrderInFlash_shouldShowError() throws Exception {
        mockMvc.perform(get("/zzz/cart/order-confirmation"))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(view().name("zzz_order_confirmation"))
                .andExpect(model().attribute("confirmation_error", "No order details found. Your order might still be processing or an error occurred."))
                .andExpect(model().attributeDoesNotExist("order"));
    }
}