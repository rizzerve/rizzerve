package ktwo.rizzerve.model; // Original package

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

// This class represents a user's active shopping cart.
// It's a temporary, in-memory representation before an order is finalized.
@Data // Lombok: Adds getters, setters, toString, equals/hashCode, etc.
@NoArgsConstructor // Lombok: Adds a no-argument constructor.
public class ZZZ_Cart {
    // Unique identifier for this cart instance within the ZZZ_CartRepository (in-memory).
    private Long id;

    // Identifier for the customer associated with this cart.
    private Long customerId;

    // Identifier for the table associated with this cart.
    private Long tableId;

    // Username of the customer.
    private String username;

    // Stores the items in the cart.
    // Key: MenuItem ID (Long), Value: Quantity (Integer).
    private Map<Long, Integer> foodItems = new HashMap<>();

    /**
     * Constructs a new ZZZ_Cart.
     * @param username The username of the customer.
     * @param customerId The ID of the customer.
     * @param tableId The ID of the table.
     */
    public ZZZ_Cart(String username, Long customerId, Long tableId) {
        this.username = username;
        this.customerId = customerId;
        this.tableId = tableId;
    }

    /**
     * Adds a specified quantity of a menu item to the cart.
     * If the item already exists in the cart, its quantity is increased by the given amount.
     * Does nothing if the quantity to add is zero or negative.
     * @param menuItemId The ID of the menu item to add.
     * @param quantity The quantity of the menu item to add. Must be positive.
     */
    public void addItem(Long menuItemId, int quantity) {
        if (quantity <= 0) return; // Only add positive quantities
        // Merge adds the quantity if key exists, or puts the new item if not.
        this.foodItems.merge(menuItemId, quantity, Integer::sum);
    }

    /**
     * Updates the quantity of a specific menu item in the cart.
     * If the new quantity is zero or less, the item is removed from the cart.
     * Otherwise, the item's quantity is set to the new value.
     * @param menuItemId The ID of the menu item to update.
     * @param newQuantity The new quantity for the menu item.
     */
    public void updateItemQuantity(Long menuItemId, int newQuantity) {
        if (newQuantity <= 0) {
            this.foodItems.remove(menuItemId); // Remove if quantity is non-positive
        } else {
            this.foodItems.put(menuItemId, newQuantity); // Set new quantity
        }
    }

    /**
     * Removes a menu item completely from the cart, regardless of its quantity.
     * @param menuItemId The ID of the menu item to remove.
     */
    public void removeItem(Long menuItemId) {
        this.foodItems.remove(menuItemId); // Remove the item
    }

    /**
     * Calculates the total price of all items currently in the cart.
     * This method requires a map of resolved MenuItem objects (ID to MenuItem) to get their prices.
     * @param resolvedMenuItems A map where keys are MenuItem IDs (Long) and values are the corresponding MenuItem objects.
     *                          This map provides access to each item's price.
     * @return The total price as a BigDecimal. Returns BigDecimal.ZERO if the cart is empty or items have no prices.
     */
    public BigDecimal calculateTotal(Map<Long, MenuItem> resolvedMenuItems) {
        if (resolvedMenuItems == null || resolvedMenuItems.isEmpty()) {
            return BigDecimal.ZERO; // No items to calculate price for
        }
        return foodItems.entrySet().stream()
                // Ensure the item ID from the cart exists in the resolvedMenuItems map and the MenuItem object itself is not null.
                .filter(entry -> resolvedMenuItems.containsKey(entry.getKey()) && resolvedMenuItems.get(entry.getKey()) != null)
                .map(entry -> {
                    MenuItem item = resolvedMenuItems.get(entry.getKey()); // Get the MenuItem object
                    // Use item's price; if price is null, treat as zero to avoid NullPointerException.
                    BigDecimal price = item.getPrice() == null ? BigDecimal.ZERO : item.getPrice();
                    // Calculate subtotal for this item entry (price * quantity).
                    return price.multiply(BigDecimal.valueOf(entry.getValue()));
                })
                // Sum all subtotals to get the grand total.
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Converts this ZZZ_Cart object into a persistent Order object.
     * The Order object will contain the items from this cart.
     * @param table The Table entity associated with this order.
     * @param resolvedMenuItems A map where keys are MenuItem IDs (Long) and values are the corresponding MenuItem objects.
     *                          This is used to populate the Order with actual MenuItem instances.
     * @return A new Order object populated with data from this cart.
     */
    public Order convertToOrder(Table table, Map<Long, MenuItem> resolvedMenuItems) {
        // Create a new Order using the non-modifiable Order class constructor.
        Order order = new Order(this.username, table);

        if (resolvedMenuItems != null) {
            // Iterate through items in this cart (foodItems map).
            for (Map.Entry<Long, Integer> cartEntry : this.foodItems.entrySet()) {
                Long menuItemId = cartEntry.getKey();
                Integer quantity = cartEntry.getValue();
                MenuItem menuItem = resolvedMenuItems.get(menuItemId); // Get the full MenuItem object

                // Add item to the Order only if it's valid and quantity is positive.
                if (menuItem != null && quantity != null && quantity > 0) {
                    // Use the addItem method from the Order class.
                    order.addItem(menuItem, quantity);
                }
            }
        }
        // The Order's status defaults to AWAITING_PAYMENT as per Order.java.
        return order;
    }

    /**
     * Clears all items from the cart, making it empty.
     */
    public void clearItems() {
        this.foodItems.clear(); // Empties the map of items
    }
}