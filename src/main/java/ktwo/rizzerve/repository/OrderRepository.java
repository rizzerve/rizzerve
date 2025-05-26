package ktwo.rizzerve.repository;

import ktwo.rizzerve.model.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class OrderRepository {
    private final List<Order> orders = new ArrayList<Order>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Optional<Order> findById(Long id) {
        return orders.stream()
                .filter(order -> order.getOrderId().equals(id))
                .findFirst();
    }

    public List<Order> findAllByUsername(String username) {
        return orders.stream()
                .filter(order -> order.getUsername().equals(username))
                .collect(Collectors.toList());
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
