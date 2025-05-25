package k2.rizzerve.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

public class Checkout_CartItemTest {

    @Test
    void cartItemShouldCalculateSubtotal() {
        // Arrange
        Checkout_Product product = new Checkout_Product("Latte", new BigDecimal("4.00"));
        product.setId(1L);
        Checkout_Cart cart = new Checkout_Cart(); // Assume Cart exists for linking, can mock later
        cart.setId(1L);
        int quantity = 3;

        // Act
        CartItem cartItem = new CartItem(cart, product, quantity);
        cartItem.setId(10L); // Simulate ID

        // Assert
        assertThat(cartItem.getId()).isEqualTo(10L);
        assertThat(cartItem.getCart()).isEqualTo(cart);
        assertThat(cartItem.getProduct()).isEqualTo(product);
        assertThat(cartItem.getQuantity()).isEqualTo(quantity);
        // Need a method to calculate subtotal
        assertThat(cartItem.getSubtotal()).isEqualTo(new BigDecimal("12.00")); // 4.00 * 3
    }

    @Test
    void cartItemSubtotalShouldBeZeroIfProductOrPriceIsNull() {
        // Arrange
        Checkout_Cart cart = new Checkout_Cart(); cart.setId(1L);
        Checkout_Product nullPriceProduct = new Checkout_Product("Mystery Drink", null); nullPriceProduct.setId(2L);
        Checkout_Product nullProduct = null; // Representing an error state

        // Act
        CartItem item1 = new CartItem(cart, nullPriceProduct, 2);
        CartItem item2 = new CartItem(cart, nullProduct, 1);


        // Assert
        assertThat(item1.getSubtotal()).isEqualTo(BigDecimal.ZERO);
        assertThat(item2.getSubtotal()).isEqualTo(BigDecimal.ZERO);
    }
}