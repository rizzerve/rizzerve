package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.ZZZ_Cart;
import ktwo.rizzerve.model.ZZZ_CartItem;
import ktwo.rizzerve.repository.ZZZ_CartItemRepository;
import ktwo.rizzerve.repository.ZZZ_CartRepository;
import ktwo.rizzerve.repository.ZZZ_ProductRepository;
import k2.rizzerve.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class ZZZ_CartApiController {

    private final ZZZ_CartRepository cartRepository;
    private final ZZZ_CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ZZZ_ProductRepository productRepository; // If product validation is needed

    // Temporary: For demo purposes, assume operations are for this user's cart
    // In a real app, this would come from security context
    private static final Long DEMO_USER_ID = 1L;

    @Autowired
    public ZZZ_CartApiController(ZZZ_CartRepository cartRepository,
                                 ZZZ_CartItemRepository cartItemRepository,
                                 UserRepository userRepository,
                                 ZZZ_ProductRepository productRepository) {
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

        Optional<ZZZ_CartItem> cartItemOptional = cartItemRepository.findById(itemId);

        if (!cartItemOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Cart item not found with ID: " + itemId));
        }

        ZZZ_CartItem item = cartItemOptional.get();

        // Optional: Verify this item belongs to the current user's cart
        Optional<ZZZ_Cart> userCartOptional = cartRepository.findByUserId(DEMO_USER_ID);
        if (!userCartOptional.isPresent() || item.getCart() == null || !item.getCart().getId().equals(userCartOptional.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Item does not belong to the current user's cart."));
        }

        item.setQuantity(request.getQuantity());
        ZZZ_CartItem updatedItem = cartItemRepository.save(item); // Save the updated item

        return ResponseEntity.ok(updatedItem);
    }
}