package k2.rizzerve.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

public class Checkout_ProductTest {

    @Test
    void productShouldHaveIdNameAndPrice() {
        // Arrange
        String name = "Espresso";
        BigDecimal price = new BigDecimal("3.50");

        // Act
        Checkout_Product product = new Checkout_Product(name, price);
        product.setId(1L); // Simulate ID assignment

        // Assert
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
    }
}