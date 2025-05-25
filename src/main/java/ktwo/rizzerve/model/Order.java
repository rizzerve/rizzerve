package ktwo.rizzerve.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import ktwo.rizzerve.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private Long tableId;

    @ElementCollection
    @CollectionTable(name = "order_foods", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "food_id")
    @Column(name = "amount")
    private Map<Long, Integer> foodItems;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.AWAITING_PAYMENT;

    public Order(Long customerId, Long tableId) {
        this.customerId = customerId;
        this.tableId = tableId;
    }

    public void addFoodItem(Long foodId, Integer amount) {
        foodItems.put(foodId, foodItems.getOrDefault(foodId, 0) + amount);
    }

    public void setFoodItem(Long foodId, Integer amount) {
        foodItems.put(foodId, amount);
    }

    public void deleteFoodItem(Long foodId) {
        foodItems.remove(foodId);
    }
}
