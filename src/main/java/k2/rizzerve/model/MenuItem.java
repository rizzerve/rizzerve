package k2.rizzerve.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ITEM")
public class MenuItem extends MenuComponent {

    private double price;
    private String description;
    private boolean available;

    public MenuItem() {
        super();
    }

    public MenuItem(String name, double price, String description, boolean available) {
        super(name);
        this.price = price;
        this.description = description;
        this.available = available;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public void display() {
        System.out.println("MenuItem: " + getName() + " - Price: " + price);
    }
}
