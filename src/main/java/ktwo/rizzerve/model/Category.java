package ktwo.rizzerve.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categories")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    public Category(){}
    private Category(Builder b) {
        this.name = b.name;
    }

    public static class Builder {
        private String name;
        public Builder name(String n) { this.name = n; return this; }
        public Category build()     { return new Category(this); }
    }
}
