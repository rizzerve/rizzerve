package k2.rizzerve.dto;

public class MenuItemDTO {

    private Long id;
    private String name;
    private double price;
    private String description;
    private boolean available;

    public MenuItemDTO() {}

    public MenuItemDTO(Long id, String name, double price, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.available = available;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
