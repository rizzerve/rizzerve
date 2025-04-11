package k2.rizzerve.model;

import jakarta.persistence.*;
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

    public Order(Long customerId, Long tableId) {
        this.customerId = customerId;
        this.tableId = tableId;
    }
}
