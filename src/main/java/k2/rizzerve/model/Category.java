package k2.rizzerve.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Category(){}
    private Category(Builder b) {
        this.name = b.name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public static class Builder {
        private String name;
        public Builder name(String n) { this.name = n; return this; }
        public Category build()     { return new Category(this); }
    }
}
