package ktwo.rizzerve.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

public class ZZZ_CartItemTest {

    @Test
    void cartItemShouldCalculateSubtotal() {
        // Arrange
        ZZZ_Product product = new ZZZ_Product("Latte", new BigDecimal("4.00"));
        product.setId(1L);
        ZZZ_Cart cart = new ZZZ_Cart(); // Assume Cart exists for linking, can mock later
        cart.setId(1L);
        int quantity = 3;

        // Act
        ZZZ_CartItem cartItem = new ZZZ_CartItem(cart, product, quantity);
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
        ZZZ_Cart cart = new ZZZ_Cart(); cart.setId(1L);
        ZZZ_Product nullPriceProduct = new ZZZ_Product("Mystery Drink", null); nullPriceProduct.setId(2L);
        ZZZ_Product nullProduct = null; // Representing an error state

        // Act
        ZZZ_CartItem item1 = new ZZZ_CartItem(cart, nullPriceProduct, 2);
        ZZZ_CartItem item2 = new ZZZ_CartItem(cart, nullProduct, 1);


        // Assert
        assertThat(item1.getSubtotal()).isEqualTo(BigDecimal.ZERO);
        assertThat(item2.getSubtotal()).isEqualTo(BigDecimal.ZERO);
    }
}