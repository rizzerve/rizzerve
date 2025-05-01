package k2.rizzerve.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a restaurant table.
 */
@Entity
@jakarta.persistence.Table(name = "restaurant_tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tableNumber;

    private boolean occupied = false;

    public Table(String tableNumber) {
        this.tableNumber = tableNumber;
    }
}
