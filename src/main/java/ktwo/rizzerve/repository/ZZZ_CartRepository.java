package ktwo.rizzerve.repository;

import ktwo.rizzerve.model.ZZZ_Cart;
import ktwo.rizzerve.model.ZZZ_CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ZZZ_CartRepository {
    private final Map<Long, ZZZ_Cart> carts = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    private final ZZZ_CartItemRepository cartItemRepository;
    private final ZZZ_UserCartLinkRepository userCartLinkRepository;

    @Autowired
    public ZZZ_CartRepository(ZZZ_CartItemRepository cartItemRepository, ZZZ_UserCartLinkRepository userCartLinkRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userCartLinkRepository = userCartLinkRepository;
    }

    public ZZZ_Cart save(ZZZ_Cart cart) {
        if (cart.getUser() == null || cart.getUser().getId() == null) {
            throw new IllegalArgumentException("Cart must have an associated user with an ID to be saved.");
        }

        boolean isNewCart = cart.getId() == null;
        if (isNewCart) {
            cart.setId(idCounter.getAndIncrement());
        }

        // Ensure all cart items have IDs and are saved in CartItemRepository
        // Also, ensure bidirectional link between cart and cart item is set.
        if (cart.getItems() != null) {
            for (ZZZ_CartItem item : cart.getItems()) {
                item.setCart(cart); // Ensure parent cart is set
                cartItemRepository.save(item); // This will assign an ID if new, and store it
            }
        }

        carts.put(cart.getId(), cart);
        userCartLinkRepository.addLink(cart.getUser().getId(), cart.getId());

        return cart;
    }

    public Optional<ZZZ_Cart> findById(Long id) {
        return Optional.ofNullable(carts.get(id));
    }

    public Optional<ZZZ_Cart> findByUserId(Long userId) {
        return userCartLinkRepository.getCartIdByUserId(userId)
                .flatMap(this::findById);
    }

    public List<ZZZ_Cart> findAll() {
        return new ArrayList<>(carts.values());
    }

    public void deleteById(Long cartId) {
        ZZZ_Cart removedCart = carts.remove(cartId);
        if (removedCart != null) {
            // Remove associated links and items
            if (removedCart.getUser() != null) {
                userCartLinkRepository.removeLinkByCartId(cartId); // Or removeLinkByUserId if that's the primary key
            }
            cartItemRepository.deleteByCartId(cartId);
        }
    }

    public void clearAll() {
        carts.clear();
        cartItemRepository.clearAll(); // Assuming CartItemRepository should also be cleared
        userCartLinkRepository.clearAllLinks(); // Clear links
        // Consider resetting idCounter if needed for tests
    }
}