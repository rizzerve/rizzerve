package ktwo.rizzerve.service;

import ktwo.rizzerve.model.*;
import ktwo.rizzerve.repository.ZZZ_CartRepository;
import ktwo.rizzerve.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZZZ_CheckoutService {
    private final ZZZ_CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final MenuItemService menuItemService;

    public ZZZ_CheckoutService(ZZZ_CartRepository cartRepository,
                               OrderRepository orderRepository,
                               MenuItemService menuItemService) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.menuItemService = menuItemService;
    }

    public Map<String, Object> getCartDetails(Long cartId) {
        ZZZ_Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        Map<Long, MenuItem> items = new HashMap<>();
        cart.getFoodItems().keySet().forEach(itemId ->
                items.put(itemId, menuItemService.getById(itemId))
        );

        return Map.of(
                "cart", cart,
                "items", items,
                "total", cart.calculateTotal(items)
        );
    }

    public Order processCheckout(Long cartId) {
        ZZZ_Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        Order order = cart.convertToOrder();
        orderRepository.save(order);
        cart.getFoodItems().clear();
        cartRepository.save(cart);

        return order;
    }
}