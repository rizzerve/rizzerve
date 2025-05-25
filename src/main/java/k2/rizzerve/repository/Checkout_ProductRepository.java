package k2.rizzerve.repository;

import k2.rizzerve.model.Checkout_Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class Checkout_ProductRepository {
    private final Map<Long, Checkout_Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Checkout_Product save(Checkout_Product product) {
        if (product.getId() == null) {
            product.setId(idCounter.getAndIncrement());
        }
        products.put(product.getId(), product);
        return product;
    }

    public Optional<Checkout_Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public List<Checkout_Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public void deleteById(Long id) {
        products.remove(id);
    }

    public boolean existsById(Long id) {
        return products.containsKey(id);
    }
}