package k2.rizzerve.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many CartItems can belong to one Cart
    @ManyToOne(fetch = FetchType.LAZY) // LAZY fetching is generally preferred
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Many CartItems can refer to the same Product (indirectly via different carts)
    // Or Many CartItems (across all carts) reference one Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    // Method to calculate subtotal for this item
    public BigDecimal getSubtotal() {
        if (product == null || product.getPrice() == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}