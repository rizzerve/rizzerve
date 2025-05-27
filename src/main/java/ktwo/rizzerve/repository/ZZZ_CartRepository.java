package ktwo.rizzerve.repository;

import ktwo.rizzerve.model.ZZZ_Cart;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ZZZ_CartRepository {
    private final Map<Long, ZZZ_Cart> carts = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    private final Map<String, Long> customerCartMap = new ConcurrentHashMap<>();

    public ZZZ_Cart save(ZZZ_Cart cart) {
        if (cart.getId() == null) {
            cart.setId(idCounter.getAndIncrement());
        }
        carts.put(cart.getId(), cart);
        customerCartMap.put(cart.getCustomerId() + "-" + cart.getTableId(), cart.getId());
        return cart;
    }

    public Optional<ZZZ_Cart> findById(Long id) {
        return Optional.ofNullable(carts.get(id));
    }

    public Optional<ZZZ_Cart> findByCustomerIdAndTableId(Long customerId, Long tableId) {
        return Optional.ofNullable(customerCartMap.get(customerId + "-" + tableId))
                .flatMap(this::findById);
    }

    public List<ZZZ_Cart> findAll() {
        return new ArrayList<>(carts.values());
    }

    public void deleteById(Long id) {
        ZZZ_Cart removed = carts.remove(id);
        if (removed != null) {
            customerCartMap.values().removeIf(v -> v.equals(id));
        }
    }
}