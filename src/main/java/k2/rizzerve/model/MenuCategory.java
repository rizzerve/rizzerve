package k2.rizzerve.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CATEGORY")
public class MenuCategory extends MenuComponent {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private List<MenuComponent> children = new ArrayList<>();

    public MenuCategory() {
        super();
    }

    public MenuCategory(String name) {
        super(name);
    }

    public List<MenuComponent> getChildren() {
        return children;
    }

    public void add(MenuComponent component) {
        children.add(component);
    }

    public void remove(MenuComponent component) {
        children.remove(component);
    }

    @Override
    public void display() {
        System.out.println("MenuCategory: " + getName());
        for (MenuComponent child : children) {
            child.display();
        }
    }
}
