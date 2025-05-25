package k2.rizzerve.controller;

import k2.rizzerve.model.Checkout_Cart;
import k2.rizzerve.model.CartItem;
import k2.rizzerve.repository.Checkout_CartItemRepository;
import k2.rizzerve.repository.CartRepository;
import k2.rizzerve.repository.Checkout_ProductRepository;
import k2.rizzerve.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class Checkout_CartApiController {

    private final CartRepository cartRepository;
    private final Checkout_CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final Checkout_ProductRepository productRepository; // If product validation is needed

    // Temporary: For demo purposes, assume operations are for this user's cart
    // In a real app, this would come from security context
    private static final Long DEMO_USER_ID = 1L;

    @Autowired
    public Checkout_CartApiController(CartRepository cartRepository,
                                      Checkout_CartItemRepository cartItemRepository,
                                      UserRepository userRepository,
                                      Checkout_ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // Inner class for request payload
    public static class QuantityUpdateRequest {
        private int quantity;
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    @PostMapping("/item/{itemId}/quantity")
    public ResponseEntity<?> updateCartItemQuantity(@PathVariable Long itemId,
                                                    @RequestBody QuantityUpdateRequest request) {
        if (request.getQuantity() < 1) { // Business rule: quantity cannot be less than 1
            return ResponseEntity.badRequest().body(Map.of("message", "Quantity must be at least 1. To remove an item, use the remove option."));
        }

        Optional<CartItem> cartItemOptional = cartItemRepository.findById(itemId);

        if (!cartItemOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Cart item not found with ID: " + itemId));
        }

        CartItem item = cartItemOptional.get();

        // Optional: Verify this item belongs to the current user's cart
        Optional<Checkout_Cart> userCartOptional = cartRepository.findByUserId(DEMO_USER_ID);
        if (!userCartOptional.isPresent() || item.getCart() == null || !item.getCart().getId().equals(userCartOptional.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Item does not belong to the current user's cart."));
        }

        item.setQuantity(request.getQuantity());
        CartItem updatedItem = cartItemRepository.save(item); // Save the updated item

        return ResponseEntity.ok(updatedItem);
    }
}