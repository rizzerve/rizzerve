package ktwo.rizzerve.service;

import ktwo.rizzerve.dto.OrderRequest;
import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.model.Order;
import ktwo.rizzerve.model.Table;
import ktwo.rizzerve.repository.MenuItemRepository;
import ktwo.rizzerve.repository.OrderRepository;
import ktwo.rizzerve.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private TableRepository tableRepository;

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> findAllByUsername(String username) {
        return orderRepository.findAllByUsername(username);
    }

    public void createOrderFromRequest(OrderRequest request) {
        Table table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

        Order order = new Order(request.getUsername(), table);

        for (Map.Entry<Long, Integer> entry : request.getItems().entrySet()) {
            MenuItem item = menuItemRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));
            order.addItem(item, entry.getValue());
        }

        orderRepository.save(order);
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}
