package k2.rizzerve.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "products") // Define table name
@Data // Lombok for getters, setters, toString, equals, hashCode
@NoArgsConstructor // JPA requires a no-arg constructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    @Column(nullable = false)
    private String name;

    // Use BigDecimal for currency precision
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Constructor for creating new products easily
    public Product(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }
}