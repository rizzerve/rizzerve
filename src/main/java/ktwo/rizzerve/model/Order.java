package ktwo.rizzerve.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import ktwo.rizzerve.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Entity
@jakarta.persistence.Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    private Table table;

    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyJoinColumn(name = "menu_item_id")
    @Column(name = "quantity")
    private Map<MenuItem, Integer> items = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.AWAITING_PAYMENT;

    public Order(String username, Table table) {
        this.username = username;
        this.table = table;
    }

    public void addItem(MenuItem item, int quantity) {
        items.merge(item, quantity, Integer::sum);
    }

    public void updateItemQuantity(MenuItem item, int quantity) {
        if (quantity <= 0) {
            items.remove(item);
        } else {
            items.put(item, quantity);
        }
    }

    public void removeItem(MenuItem item) {
        items.remove(item);
    }
}
