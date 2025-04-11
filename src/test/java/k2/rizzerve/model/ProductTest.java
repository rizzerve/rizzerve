package k2.rizzerve.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductTest {

    @Test
    void productShouldHaveIdNameAndPrice() {
        // Arrange
        String name = "Espresso";
        BigDecimal price = new BigDecimal("3.50");

        // Act
        Product product = new Product(name, price);
        product.setId(1L); // Simulate ID assignment

        // Assert
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
    }
}