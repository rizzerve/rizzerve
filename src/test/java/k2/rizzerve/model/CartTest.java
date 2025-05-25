package k2.rizzerve.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class CartTest {

    private Checkout_Cart cart;
    private User user;
    private Checkout_Product product1;
    private Checkout_Product product2;

    @BeforeEach
    void setUp() {
        user = new User("cartuser", "cart@test.com", "pass");
        user.setId(1L);

        cart = new Checkout_Cart(user); // Need constructor accepting User
        cart.setId(1L);

        product1 = new Checkout_Product("Tea", new BigDecimal("2.50"));
        product1.setId(10L);
        product2 = new Checkout_Product("Cake", new BigDecimal("5.00"));
        product2.setId(20L);
    }

    @Test
    void cartShouldBelongToUser() {
        assertThat(cart.getUser()).isEqualTo(user);
    }

    @Test
    void cartShouldStartEmpty() {
        assertThat(cart.getItems()).isNotNull().isEmpty();
        assertThat(cart.calculateTotal()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void cartShouldCalculateTotalWithItems() {
        // Arrange
        CartItem item1 = new CartItem(cart, product1, 2); // 2.50 * 2 = 5.00
        CartItem item2 = new CartItem(cart, product2, 1); // 5.00 * 1 = 5.00

        // Act
        // Need methods to add items to the cart's collection
        cart.addItem(item1);
        cart.addItem(item2);

        // Assert
        assertThat(cart.getItems()).hasSize(2).containsExactlyInAnyOrder(item1, item2);
        assertThat(cart.calculateTotal()).isEqualTo(new BigDecimal("10.00")); // 5.00 + 5.00
    }

    @Test
    void cartShouldAllowRemovingItems() {
        // Arrange
        CartItem item1 = new CartItem(cart, product1, 2);
        CartItem item2 = new CartItem(cart, product2, 1);
        cart.addItem(item1);
        cart.addItem(item2);
        assertThat(cart.calculateTotal()).isEqualTo(new BigDecimal("10.00")); // Pre-check

        // Act
        cart.removeItem(item1); // Need removeItem method

        // Assert
        assertThat(cart.getItems()).hasSize(1).containsExactly(item2);
        assertThat(cart.calculateTotal()).isEqualTo(new BigDecimal("5.00")); // Only item2 remains
    }
}