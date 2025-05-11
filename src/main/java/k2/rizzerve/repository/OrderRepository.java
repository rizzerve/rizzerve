package k2.rizzerve.repository;

import k2.rizzerve.model.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepository {
    private final List<Order> orders = new ArrayList<Order>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Optional<Order> findById(Long id) {
        return orders.stream()
                .filter(order -> order.getOrderId().equals(id))
                .findFirst();
    }

    public void save(Order order) {
        if (order.getOrderId() == null) {
            order.setOrderId(idCounter.getAndIncrement());
            orders.add(order);
        } else {
            deleteById(order.getOrderId());
            orders.add(order);
        }
    }

    public void deleteById(Long id) {
        orders.removeIf(order -> order.getOrderId().equals(id));
    }
}
