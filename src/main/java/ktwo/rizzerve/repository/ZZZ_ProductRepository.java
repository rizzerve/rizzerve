package ktwo.rizzerve.repository;

import ktwo.rizzerve.model.ZZZ_Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ZZZ_ProductRepository {
    private final Map<Long, ZZZ_Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public ZZZ_Product save(ZZZ_Product product) {
        if (product.getId() == null) {
            product.setId(idCounter.getAndIncrement());
        }
        products.put(product.getId(), product);
        return product;
    }

    public Optional<ZZZ_Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public List<ZZZ_Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public void deleteById(Long id) {
        products.remove(id);
    }

    public boolean existsById(Long id) {
        return products.containsKey(id);
    }
}