package k2.rizzerve.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class Checkout_UserCartLinkRepository {
    // In a real scenario with multiple carts per user (e.g., saved carts, active cart),
    // this would be more complex. For now, assuming one active cart per user.
    private final Map<Long, Long> userIdToCartIdMap = new ConcurrentHashMap<>(); // userId -> cartId
    private final Map<Long, Long> cartIdToUserIdMap = new ConcurrentHashMap<>(); // cartId -> userId

    public void addLink(Long userId, Long cartId) {
        // If a user already has a cart, this will override it.
        // Handle prior links if necessary (e.g., remove old cartId from cartIdToUserIdMap)
        Long oldCartId = userIdToCartIdMap.put(userId, cartId);
        if (oldCartId != null) {
            cartIdToUserIdMap.remove(oldCartId);
        }
        cartIdToUserIdMap.put(cartId, userId);
    }

    public Optional<Long> getCartIdByUserId(Long userId) {
        return Optional.ofNullable(userIdToCartIdMap.get(userId));
    }

    public Optional<Long> getUserIdByCartId(Long cartId) {
        return Optional.ofNullable(cartIdToUserIdMap.get(cartId));
    }

    public void removeLinkByUserId(Long userId) {
        Long cartId = userIdToCartIdMap.remove(userId);
        if (cartId != null) {
            cartIdToUserIdMap.remove(cartId);
        }
    }

    public void removeLinkByCartId(Long cartId) {
        Long userId = cartIdToUserIdMap.remove(cartId);
        if (userId != null) {
            userIdToCartIdMap.remove(userId);
        }
    }

    public void clearAllLinks() {
        userIdToCartIdMap.clear();
        cartIdToUserIdMap.clear();
    }
}