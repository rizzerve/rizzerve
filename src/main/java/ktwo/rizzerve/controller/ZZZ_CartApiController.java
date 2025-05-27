package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.Order;
import ktwo.rizzerve.model.ZZZ_Cart;
import ktwo.rizzerve.repository.ZZZ_CartRepository;
import ktwo.rizzerve.service.ZZZ_CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class ZZZ_CartApiController {
    private final ZZZ_CartRepository cartRepository;
    private final ZZZ_CheckoutService checkoutService;

    @Autowired
    public ZZZ_CartApiController(ZZZ_CartRepository cartRepository,
                                 ZZZ_CheckoutService checkoutService) {
        this.cartRepository = cartRepository;
        this.checkoutService = checkoutService;
    }

    @PostMapping("/{cartId}/items/{itemId}/increment")
    public ResponseEntity<?> incrementItem(@PathVariable Long cartId,
                                           @PathVariable Long itemId) {
        return updateQuantity(cartId, itemId, 1);
    }

    @PostMapping("/{cartId}/items/{itemId}/decrement")
    public ResponseEntity<?> decrementItem(@PathVariable Long cartId,
                                           @PathVariable Long itemId) {
        return updateQuantity(cartId, itemId, -1);
    }

    @PostMapping("/{cartId}/checkout")
    public ResponseEntity<?> checkout(@PathVariable Long cartId) {
        try {
            Order order = checkoutService.processCheckout(cartId);
            Map<String, Object> response = checkoutService.getCartDetails(cartId);
            response.put("orderId", order.getOrderId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private ResponseEntity<?> updateQuantity(Long cartId, Long itemId, int delta) {
        ZZZ_Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        int newQuantity = cart.getFoodItems().getOrDefault(itemId, 0) + delta;
        cart.setQuantity(itemId, newQuantity);
        cartRepository.save(cart);

        return ResponseEntity.ok(checkoutService.getCartDetails(cartId));
    }
}