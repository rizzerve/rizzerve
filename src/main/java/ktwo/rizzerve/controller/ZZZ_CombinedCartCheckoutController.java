package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.model.Order;
import ktwo.rizzerve.model.ZZZ_Cart;
import ktwo.rizzerve.repository.ZZZ_CartRepository; // Imported for fallback in proceedToCheckout error handling
import ktwo.rizzerve.service.ZZZ_CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Map;

// Controller handling both web page requests for cart/checkout and API endpoints for cart manipulations.
@Controller
@RequestMapping("/zzz/cart") // Base path for all cart-related requests under this controller.
public class ZZZ_CombinedCartCheckoutController {

    // Service layer dependency for handling cart and checkout business logic.
    private final ZZZ_CheckoutService checkoutService;
    // Repository for cart data, used in error fallback path.
    private final ZZZ_CartRepository cartRepository;


    /**
     * Constructs the controller with necessary service dependencies.
     * @param checkoutService The service for cart and checkout operations.
     * @param cartRepository The repository for cart data, for error handling fallback.
     */
    @Autowired
    public ZZZ_CombinedCartCheckoutController(ZZZ_CheckoutService checkoutService, ZZZ_CartRepository cartRepository) {
        this.checkoutService = checkoutService;
        this.cartRepository = cartRepository;
    }

    /**
     * Displays the main checkout page for a user at a specific table.
     * It retrieves or creates a cart for the user and table, then populates the model
     * with cart details for rendering by the view.
     * @param customerId The ID of the customer.
     * @param tableId The ID of the table.
     * @param username The username of the customer.
     * @param model The Spring Model object to pass data to the view.
     * @return The name of the Thymeleaf template to render (e.g., "zzz_checkout_user").
     */
    @GetMapping("/checkout-page")
    public String viewCheckoutPage(@RequestParam Long customerId,
                                   @RequestParam Long tableId,
                                   @RequestParam String username,
                                   Model model) {
        try {
            // Get or create a cart for this customer/table.
            ZZZ_Cart cart = checkoutService.getOrCreateCart(customerId, tableId, username);
            // Fetch all details needed for the view (cart items, total price, etc.).
            Map<String, Object> cartDetails = checkoutService.getCartDetailsForView(cart.getId());
            model.addAllAttributes(cartDetails); // Add all fetched details to the model
            // Pass identifiers to the view, useful for JavaScript making further AJAX calls.
            model.addAttribute("cartId", cart.getId());
            model.addAttribute("customerId", customerId);
            model.addAttribute("tableId", tableId);
            model.addAttribute("username", username);
        } catch (Exception e) {
            model.addAttribute("error", "Error loading cart: " + e.getMessage());
            // Consider redirecting to a dedicated error page or a more user-friendly display.
        }
        return "zzz_checkout_user"; // Thymeleaf template name
    }

    /**
     * API endpoint to add an item to a specific cart.
     * Expects cartId in path, and menuItemId/quantity as request parameters.
     * @param cartId The ID of the cart to modify.
     * @param menuItemId The ID of the menu item to add.
     * @param quantity The quantity of the item to add.
     * @return A ResponseEntity containing the updated cart details (for UI refresh) on success,
     *         or an error message on failure.
     */
    @PostMapping("/{cartId}/items/add")
    @ResponseBody // Return data directly in response body (e.g., as JSON).
    public ResponseEntity<?> addItemToCart(@PathVariable Long cartId,
                                           @RequestParam Long menuItemId,
                                           @RequestParam int quantity) {
        try {
            checkoutService.addItemToCart(cartId, menuItemId, quantity); // Call service
            // Fetch and return updated cart details for the client to re-render.
            Map<String, Object> updatedCartDetails = checkoutService.getCartDetailsForView(cartId);
            return ResponseEntity.ok(updatedCartDetails); // HTTP 200 OK with data
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); // HTTP 400 Bad Request
        } catch (Exception e) {
            // Log server-side error here
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to add item. Please try again.")); // HTTP 500
        }
    }

    /**
     * API endpoint to update the quantity of an item in a specific cart.
     * If quantity is <= 0, the item is typically removed.
     * @param cartId The ID of the cart to modify.
     * @param menuItemId The ID of the menu item to update.
     * @param quantity The new quantity for the item.
     * @return A ResponseEntity with updated cart details on success, or an error message on failure.
     */
    @PostMapping("/{cartId}/items/update")
    @ResponseBody
    public ResponseEntity<?> updateItemQuantity(@PathVariable Long cartId,
                                                @RequestParam Long menuItemId,
                                                @RequestParam int quantity) {
        try {
            checkoutService.updateItemQuantityInCart(cartId, menuItemId, quantity); // Call service
            Map<String, Object> updatedCartDetails = checkoutService.getCartDetailsForView(cartId);
            return ResponseEntity.ok(updatedCartDetails); // HTTP 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); // HTTP 400
        } catch (Exception e) {
            // Log server-side error here
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to update quantity. Please try again.")); // HTTP 500
        }
    }

    /**
     * API endpoint to remove an item completely from a specific cart.
     * @param cartId The ID of the cart to modify.
     * @param menuItemId The ID of the menu item to remove.
     * @return A ResponseEntity with updated cart details on success, or an error message on failure.
     */
    @PostMapping("/{cartId}/items/remove")
    @ResponseBody
    public ResponseEntity<?> removeItemFromCart(@PathVariable Long cartId,
                                                @RequestParam Long menuItemId) {
        try {
            checkoutService.removeItemFromCart(cartId, menuItemId); // Call service
            Map<String, Object> updatedCartDetails = checkoutService.getCartDetailsForView(cartId);
            return ResponseEntity.ok(updatedCartDetails); // HTTP 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); // HTTP 400
        } catch (Exception e) {
            // Log server-side error here
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to remove item. Please try again.")); // HTTP 500
        }
    }

    /**
     * Handles the action of proceeding to checkout.
     * It finalizes the order for the given cartId and redirects the user to an order confirmation page.
     * @param cartId The ID of the cart to checkout.
     * @param redirectAttributes Used to pass attributes (like the finalized order or error messages)
     *                           across a redirect.
     * @return A redirect string to the order confirmation page on success, or back to the
     *         checkout page (or a generic error page) on failure.
     */
    @PostMapping("/{cartId}/proceed-to-checkout")
    public String proceedToCheckout(@PathVariable Long cartId,
                                    RedirectAttributes redirectAttributes) {
        ZZZ_Cart cartForRedirectOnError = null; // To store cart info for redirect URL if needed
        try {
            // Attempt to get cart info for potential error redirect BEFORE deleting it.
            cartForRedirectOnError = cartRepository.findById(cartId).orElse(null);

            Order finalizedOrder = checkoutService.processCheckoutAndFinalizeOrder(cartId); // Process checkout
            redirectAttributes.addFlashAttribute("order", finalizedOrder); // Pass order to confirmation page
            return "redirect:/zzz/cart/order-confirmation"; // Redirect to confirmation
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Checkout failed: " + e.getMessage());
            // Try to redirect back to the specific cart's checkout page
            if (cartForRedirectOnError != null) {
                return "redirect:/zzz/cart/checkout-page?customerId=" + cartForRedirectOnError.getCustomerId()
                        + "&tableId=" + cartForRedirectOnError.getTableId()
                        + "&username=" + cartForRedirectOnError.getUsername();
            }
            return "redirect:/"; // Fallback: redirect to home or a generic error page
        } catch (Exception e) {
            // Log server-side error here
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during checkout. Please contact support.");
            return "redirect:/"; // Fallback
        }
    }

    /**
     * Displays the order confirmation (thank you) page.
     * Expects the finalized 'order' object to be passed via flash attributes from the checkout process.
     * @param model The Spring Model object.
     * @return The name of the Thymeleaf template for order confirmation (e.g., "zzz_order_confirmation").
     */
    @GetMapping("/order-confirmation")
    public String orderConfirmationPage(Model model) {
        // Check if the 'order' object was passed (usually via flash attributes after redirect).
        if (!model.containsAttribute("order")) {
            model.addAttribute("confirmation_error", "No order details found. Your order might still be processing or an error occurred.");
        } else {
            Order order = (Order) model.getAttribute("order");
            // Calculate total price for display, as Order model doesn't store it directly.
            // (Order.java is non-modifiable).
            BigDecimal total = order.getItems().entrySet().stream()
                    .map(entry -> {
                        // Ensure key and price are not null before calculation
                        MenuItem item = entry.getKey();
                        BigDecimal price = (item != null && item.getPrice() != null) ? item.getPrice() : BigDecimal.ZERO;
                        return price.multiply(BigDecimal.valueOf(entry.getValue()));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("totalPrice", total);
        }
        return "zzz_order_confirmation"; // Thymeleaf template name
    }
}