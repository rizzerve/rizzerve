package ktwo.rizzerve.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashSet; // Using HashSet
import java.util.Set;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
public class ZZZ_Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each Cart belongs to one User
    @OneToOne(fetch = FetchType.LAZY) // One User has one Cart
    @JoinColumn(name = "user_id", nullable = false, unique = true) // Ensure one cart per user
    private User user;

    // One Cart has many CartItems
    // CascadeType.ALL: If Cart is saved/deleted, associated CartItems are too.
    // orphanRemoval=true: If a CartItem is removed from this set, it's deleted from DB.
    // mappedBy="cart": Indicates CartItem.cart field owns the relationship.
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) // EAGER might be ok for Cart
    @EqualsAndHashCode.Exclude // Avoid recursion in Lombok methods
    @ToString.Exclude // Avoid recursion in Lombok methods
    private Set<ZZZ_CartItem> items = new HashSet<>();

    public ZZZ_Cart(User user) {
        this.user = user;
    }

    // Method to calculate the total price of all items in the cart
    public BigDecimal calculateTotal() {
        return items.stream()
                .map(ZZZ_CartItem::getSubtotal) // Use the subtotal method from CartItem
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum up all subtotals
    }

    // Method to add an item - NOTE: This just adds the pre-created CartItem object
    // A better version would take Product and quantity, and manage CartItem creation/update internally
    public void addItem(ZZZ_CartItem item) {
        item.setCart(this); // Ensure relationship is bidirectional
        this.items.add(item);
    }

    // Method to remove an item
    public void removeItem(ZZZ_CartItem item) {
        this.items.remove(item);
        item.setCart(null); // Break relationship
    }
}