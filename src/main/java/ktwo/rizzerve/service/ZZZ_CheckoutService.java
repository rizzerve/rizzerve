package ktwo.rizzerve.service;

import ktwo.rizzerve.model.*;
import ktwo.rizzerve.repository.ZZZ_CartRepository;
import ktwo.rizzerve.repository.OrderRepository;
import ktwo.rizzerve.repository.TableRepository;
import ktwo.rizzerve.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// Service class responsible for handling business logic related to cart operations and order checkout.
@Service
public class ZZZ_CheckoutService {
    // Dependency for accessing in-memory cart storage.
    private final ZZZ_CartRepository cartRepository;
    // Dependency for persisting finalized Order objects to the database.
    private final OrderRepository orderRepository;
    // Dependency for fetching MenuItem details (e.g., price, availability).
    private final MenuItemService menuItemService;
    // Dependency for fetching Table entity details.
    private final TableRepository tableRepository;

    /**
     * Constructs the ZZZ_CheckoutService with its required dependencies.
     * @param cartRepository Repository for ZZZ_Cart objects.
     * @param orderRepository Repository for Order entities.
     * @param menuItemService Service for MenuItem related operations.
     * @param tableRepository Repository for Table entities.
     */
    @Autowired
    public ZZZ_CheckoutService(ZZZ_CartRepository cartRepository,
                               OrderRepository orderRepository,
                               MenuItemService menuItemService,
                               TableRepository tableRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.menuItemService = menuItemService;
        this.tableRepository = tableRepository;
    }

    /**
     * Retrieves an existing ZZZ_Cart for a given customer and table, or creates a new one if not found.
     * The new cart is then saved to the cart repository.
     * @param customerId The ID of the customer.
     * @param tableId The ID of the table.
     * @param username The username of the customer, used when creating a new cart.
     * @return The existing or newly created ZZZ_Cart.
     */
    public ZZZ_Cart getOrCreateCart(Long customerId, Long tableId, String username) {
        // Try to find an existing cart.
        return cartRepository.findByCustomerIdAndTableId(customerId, tableId)
                // If not found, create a new one.
                .orElseGet(() -> {
                    ZZZ_Cart newCart = new ZZZ_Cart(username, customerId, tableId);
                    return cartRepository.save(newCart); // Save the new cart
                });
    }

    /**
     * Retrieves a ZZZ_Cart by its unique internal ID.
     * @param cartId The ID of the cart to retrieve.
     * @return The ZZZ_Cart object.
     * @throws IllegalArgumentException if no cart is found with the given ID.
     */
    private ZZZ_Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with ID: " + cartId));
    }

    /**
     * Resolves MenuItem IDs from a cart into actual MenuItem objects.
     * Fetches each MenuItem using the MenuItemService.
     * @param cart The ZZZ_Cart whose item IDs need to be resolved.
     * @return A Map where keys are MenuItem IDs (Long) and values are the corresponding MenuItem objects.
     *         Returns an empty map if the cart has no items.
     */
    private Map<Long, MenuItem> resolveMenuItems(ZZZ_Cart cart) {
        if (cart.getFoodItems().isEmpty()) {
            return new HashMap<>(); // Return empty map if cart is empty
        }
        // For each item ID in the cart, fetch the MenuItem object.
        return cart.getFoodItems().keySet().stream()
                .collect(Collectors.toMap(
                        itemId -> itemId, // Key of the map is the itemId itself
                        menuItemService::getById, // Value is the MenuItem fetched by its ID
                        (existing, replacement) -> existing, // Merge function for duplicate keys (not expected from keySet)
                        HashMap::new // Supplier for the new map
                ));
    }

    /**
     * Adds a specified quantity of a menu item to a given cart.
     * It first validates the menu item's existence and availability.
     * @param cartId The ID of the cart to which the item will be added.
     * @param menuItemId The ID of the menu item to add.
     * @param quantity The quantity of the item to add.
     * @return The updated ZZZ_Cart object.
     * @throws IllegalArgumentException if the cart is not found, or the menu item is invalid/unavailable.
     */
    public ZZZ_Cart addItemToCart(Long cartId, Long menuItemId, int quantity) {
        ZZZ_Cart cart = getCartById(cartId); // Get the cart
        MenuItem item = menuItemService.getById(menuItemId); // Fetch and validate the menu item
        if (item == null || !item.isAvailable()) {
            throw new IllegalArgumentException("MenuItem ID " + menuItemId + " is invalid or unavailable.");
        }
        cart.addItem(menuItemId, quantity); // Add item to cart logic (delegated to ZZZ_Cart)
        return cartRepository.save(cart); // Save the updated cart
    }

    /**
     * Updates the quantity of a specific menu item in a given cart.
     * If the item is not in the cart and quantity is positive, it might check item validity before adding.
     * If the new quantity is zero or less, the item is removed.
     * @param cartId The ID of the cart to update.
     * @param menuItemId The ID of the menu item whose quantity is to be updated.
     * @param newQuantity The new quantity for the item.
     * @return The updated ZZZ_Cart object.
     * @throws IllegalArgumentException if the cart is not found, or if adding a new item and it's invalid/unavailable.
     */
    public ZZZ_Cart updateItemQuantityInCart(Long cartId, Long menuItemId, int newQuantity) {
        ZZZ_Cart cart = getCartById(cartId); // Get the cart
        // If item is not in cart and we are trying to add it (positive quantity)
        if (!cart.getFoodItems().containsKey(menuItemId) && newQuantity > 0) {
            MenuItem item = menuItemService.getById(menuItemId); // Validate item before adding as new
            if (item == null || !item.isAvailable()) {
                throw new IllegalArgumentException("MenuItem ID " + menuItemId + " is invalid or unavailable to add.");
            }
        }
        cart.updateItemQuantity(menuItemId, newQuantity); // Update quantity (delegated to ZZZ_Cart)
        return cartRepository.save(cart); // Save the updated cart
    }

    /**
     * Removes a menu item completely from a given cart.
     * @param cartId The ID of the cart from which the item will be removed.
     * @param menuItemId The ID of the menu item to remove.
     * @return The updated ZZZ_Cart object.
     * @throws IllegalArgumentException if the cart is not found.
     */
    public ZZZ_Cart removeItemFromCart(Long cartId, Long menuItemId) {
        ZZZ_Cart cart = getCartById(cartId); // Get the cart
        cart.removeItem(menuItemId); // Remove item (delegated to ZZZ_Cart)
        return cartRepository.save(cart); // Save the updated cart
    }

    /**
     * Prepares details of a cart for display on a view (e.g., checkout page).
     * This includes the cart object itself, a map of resolved MenuItem objects, and the calculated total price.
     * @param cartId The ID of the cart whose details are needed.
     * @return A Map containing "cart" (ZZZ_Cart), "resolvedItemsMap" (Map<Long, MenuItem>), and "total" (BigDecimal).
     * @throws IllegalArgumentException if the cart is not found.
     */
    public Map<String, Object> getCartDetailsForView(Long cartId) {
        ZZZ_Cart cart = getCartById(cartId); // Get the cart
        Map<Long, MenuItem> resolvedMenuItems = resolveMenuItems(cart); // Get actual MenuItem objects
        BigDecimal total = cart.calculateTotal(resolvedMenuItems); // Calculate total price

        Map<String, Object> details = new HashMap<>();
        details.put("cart", cart); // The cart data model
        details.put("resolvedItemsMap", resolvedMenuItems); // Map of item IDs to MenuItem objects for easy display
        details.put("total", total); // The calculated total price
        return details;
    }

    /**
     * Finalizes the checkout process for a given cart.
     * Converts the ZZZ_Cart to an Order, saves the Order to the database,
     * and then deletes the ZZZ_Cart from the in-memory repository.
     * This operation is transactional.
     * @param cartId The ID of the cart to be checked out.
     * @return The saved Order object.
     * @throws IllegalArgumentException if cart or associated table is not found.
     * @throws IllegalStateException if the cart is empty.
     */
    @Transactional // Ensures all database operations (saving order) are atomic.
    public Order processCheckoutAndFinalizeOrder(Long cartId) {
        ZZZ_Cart cart = getCartById(cartId); // Get the cart
        if (cart.getFoodItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart."); // Prevent checkout of empty cart
        }

        // Fetch the Table entity associated with the cart.
        Table table = tableRepository.findById(cart.getTableId())
                .orElseThrow(() -> new IllegalArgumentException("Table not found for ID: " + cart.getTableId()));

        // Resolve MenuItem IDs to MenuItem objects for order creation.
        Map<Long, MenuItem> resolvedMenuItems = resolveMenuItems(cart);

        // Convert the ZZZ_Cart to a persistent Order entity.
        Order order = cart.convertToOrder(table, resolvedMenuItems);
        // Set the order status (e.g., AWAITING_PAYMENT, or use default from Order.java).
        order.setStatus(OrderStatus.AWAITING_PAYMENT);

        // Save the finalized order to the database.
        Order savedOrder = orderRepository.save(order);

        // Remove the cart from the in-memory store after successful order persistence.
        cartRepository.deleteById(cartId);

        return savedOrder; // Return the persisted order
    }
}