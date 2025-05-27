package ktwo.rizzerve.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
public class ZZZ_Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private Long tableId;

    @ElementCollection
    @CollectionTable(name = "cart_items", joinColumns = @JoinColumn(name = "cart_id"))
    @MapKeyColumn(name = "menu_item_id")
    @Column(name = "quantity")
    private Map<Long, Integer> foodItems = new HashMap<>();

    public ZZZ_Cart(Long customerId, Long tableId) {
        this.customerId = customerId;
        this.tableId = tableId;
    }

    public void incrementQuantity(Long menuItemId) {
        foodItems.merge(menuItemId, 1, Integer::sum);
    }

    public void decrementQuantity(Long menuItemId) {
        foodItems.computeIfPresent(menuItemId, (k, v) -> v > 1 ? v - 1 : null);
    }

    public void setQuantity(Long menuItemId, int quantity) {
        if (quantity < 1) {
            foodItems.remove(menuItemId);
        } else {
            foodItems.put(menuItemId, quantity);
        }
    }

    public BigDecimal calculateTotal(Map<Long, MenuItem> menuItems) {
        return foodItems.entrySet().stream()
                .filter(entry -> menuItems.containsKey(entry.getKey()))
                .map(entry -> menuItems.get(entry.getKey()).getPrice()
                        .multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Order convertToOrder() {
        Order order = new Order(username, tableId);
        order.setFoodItems(new HashMap<>(foodItems));
        return order;
    }
}