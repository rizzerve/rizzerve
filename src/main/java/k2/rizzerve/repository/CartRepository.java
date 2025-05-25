package k2.rizzerve.repository;

import k2.rizzerve.model.Checkout_Cart;
import k2.rizzerve.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CartRepository {
    private final Map<Long, Checkout_Cart> carts = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    private final Checkout_CartItemRepository cartItemRepository;
    private final Checkout_UserCartLinkRepository userCartLinkRepository;

    @Autowired
    public CartRepository(Checkout_CartItemRepository cartItemRepository, Checkout_UserCartLinkRepository userCartLinkRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userCartLinkRepository = userCartLinkRepository;
    }

    public Checkout_Cart save(Checkout_Cart cart) {
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
            for (CartItem item : cart.getItems()) {
                item.setCart(cart); // Ensure parent cart is set
                cartItemRepository.save(item); // This will assign an ID if new, and store it
            }
        }

        carts.put(cart.getId(), cart);
        userCartLinkRepository.addLink(cart.getUser().getId(), cart.getId());

        return cart;
    }

    public Optional<Checkout_Cart> findById(Long id) {
        return Optional.ofNullable(carts.get(id));
    }

    public Optional<Checkout_Cart> findByUserId(Long userId) {
        return userCartLinkRepository.getCartIdByUserId(userId)
                .flatMap(this::findById);
    }

    public List<Checkout_Cart> findAll() {
        return new ArrayList<>(carts.values());
    }

    public void deleteById(Long cartId) {
        Checkout_Cart removedCart = carts.remove(cartId);
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