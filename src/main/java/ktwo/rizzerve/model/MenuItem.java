package ktwo.rizzerve.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import jakarta.persistence.Table;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "menu_items")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;

    @Column(length = 1024)
    private String description;

    private boolean available  = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public MenuItem() { }

    public static Builder builder() {
        return new Builder();
    }

    private MenuItem(Builder b) {
        this.name        = b.name;
        this.price       = b.price;
        this.description = b.description;
        this.available   = b.available;
        this.category    = b.category;
    }

    public static class Builder {
        private String name;
        private BigDecimal price;
        private String description;
        private boolean available = true;
        private Category category;

        public Builder name(String name)        { this.name = name; return this; }
        public Builder price(BigDecimal p)     { this.price = p;   return this; }
        public Builder description(String d)   { this.description = d; return this; }
        public Builder available(boolean f)    { this.available = f;   return this; }
        public Builder category(Category c)    { this.category = c;    return this; }
        public MenuItem build()               { return new MenuItem(this); }
    }
}
