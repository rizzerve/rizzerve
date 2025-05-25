package k2.rizzerve.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore // Prevents serializing the 'cart' field back to client
    private Checkout_Cart cart;

    // If product details (like name) are needed in the response, EAGER can be simpler here, otherwise use a DTO.
    @ManyToOne(fetch = FetchType.EAGER) // << CHANGED TO EAGER (was LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Checkout_Product product;

    @Column(nullable = false)
    private int quantity;

    public CartItem(Checkout_Cart cart, Checkout_Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        if (product == null || product.getPrice() == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}