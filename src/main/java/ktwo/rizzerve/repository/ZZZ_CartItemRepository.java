package ktwo.rizzerve.repository;

import ktwo.rizzerve.model.ZZZ_CartItem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ZZZ_CartItemRepository {
    private final Map<Long, ZZZ_CartItem> cartItems = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public ZZZ_CartItem save(ZZZ_CartItem item) {
        if (item.getId() == null) {
            item.setId(idCounter.getAndIncrement());
        }
        cartItems.put(item.getId(), item);
        return item;
    }

    public Optional<ZZZ_CartItem> findById(Long id) {
        return Optional.ofNullable(cartItems.get(id));
    }

    public List<ZZZ_CartItem> findByCartId(Long cartId) {
        return cartItems.values().stream()
                .filter(item -> item.getCart() != null && item.getCart().getId().equals(cartId))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        cartItems.remove(id);
    }

    public void deleteByCartId(Long cartId) {
        List<Long> itemIdsToRemove = cartItems.values().stream()
                .filter(item -> item.getCart() != null && item.getCart().getId().equals(cartId))
                .map(ZZZ_CartItem::getId)
                .collect(Collectors.toList());
        itemIdsToRemove.forEach(cartItems::remove);
    }

    public List<ZZZ_CartItem> findAll() {
        return new ArrayList<>(cartItems.values());
    }

    public void clearAll() {
        cartItems.clear();
        // Consider resetting idCounter if needed for tests
    }
}