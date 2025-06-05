package ktwo.rizzerve.repository;

import ktwo.rizzerve.model.ZZZ_Cart;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Repository for managing ZZZ_Cart objects in memory.
// This is not a database-backed repository but an in-memory store.
@Repository
public class ZZZ_CartRepository {
    // Main store for carts, mapping internal cart ID to ZZZ_Cart object.
    private final Map<Long, ZZZ_Cart> carts = new ConcurrentHashMap<>();
    // Counter for generating unique internal IDs for carts.
    private final AtomicLong idCounter = new AtomicLong(1);

    // Secondary map for quick lookup of a cart ID using a composite key of "customerId-tableId".
    private final Map<String, Long> customerTableToCartIdMap = new ConcurrentHashMap<>();

    /**
     * Generates a unique string key for the customerTableToCartIdMap.
     * This key combines customerId and tableId.
     * @param customerId The ID of the customer.
     * @param tableId The ID of the table.
     * @return A string key (e.g., "123-45").
     * @throws IllegalArgumentException if customerId or tableId is null.
     */
    private String getCustomerTableKey(Long customerId, Long tableId) {
        if (customerId == null || tableId == null) {
            throw new IllegalArgumentException("CustomerId and TableId cannot be null for cache key generation.");
        }
        return customerId + "-" + tableId;
    }

    /**
     * Saves or updates a ZZZ_Cart in the in-memory store.
     * If the cart is new (ID is null), a new ID is generated and assigned.
     * The cart is stored in the main `carts` map and an entry is added/updated in `customerTableToCartIdMap`.
     * @param cart The ZZZ_Cart object to save.
     * @return The saved ZZZ_Cart object (possibly with a newly assigned ID).
     */
    public ZZZ_Cart save(ZZZ_Cart cart) {
        if (cart.getId() == null) {
            cart.setId(idCounter.getAndIncrement()); // Assign a new ID if cart is new
        }
        carts.put(cart.getId(), cart); // Store/update in the main map

        // Update the lookup map if customerId and tableId are present
        if (cart.getCustomerId() != null && cart.getTableId() != null) {
            customerTableToCartIdMap.put(getCustomerTableKey(cart.getCustomerId(), cart.getTableId()), cart.getId());
        }
        return cart;
    }

    /**
     * Finds a ZZZ_Cart by its internal ID.
     * @param id The internal ID of the cart to find.
     * @return An Optional containing the ZZZ_Cart if found, or an empty Optional otherwise.
     */
    public Optional<ZZZ_Cart> findById(Long id) {
        return Optional.ofNullable(carts.get(id)); // Retrieve from map, wrap in Optional
    }

    /**
     * Finds a ZZZ_Cart by customerId and tableId using the lookup map.
     * @param customerId The ID of the customer.
     * @param tableId The ID of the table.
     * @return An Optional containing the ZZZ_Cart if found, or an empty Optional otherwise.
     */
    public Optional<ZZZ_Cart> findByCustomerIdAndTableId(Long customerId, Long tableId) {
        String key = getCustomerTableKey(customerId, tableId); // Generate the lookup key
        Long cartId = customerTableToCartIdMap.get(key); // Get internal cart ID from lookup map
        if (cartId == null) {
            return Optional.empty(); // No cart ID found for this customer/table combination
        }
        return findById(cartId); // Find the cart using the retrieved internal ID
    }

    /**
     * Retrieves all ZZZ_Cart objects currently in the store.
     * @return A List of all ZZZ_Cart objects. Returns an empty list if no carts are stored.
     */
    public List<ZZZ_Cart> findAll() {
        return new ArrayList<>(carts.values()); // Return a new list containing all carts
    }

    /**
     * Deletes a ZZZ_Cart from the store by its internal ID.
     * Also removes the corresponding entry from the customerTableToCartIdMap.
     * @param id The internal ID of the cart to delete.
     */
    public void deleteById(Long id) {
        ZZZ_Cart removedCart = carts.remove(id); // Remove from main map
        // If cart was successfully removed and had customer/table info, remove from lookup map too
        if (removedCart != null && removedCart.getCustomerId() != null && removedCart.getTableId() != null) {
            customerTableToCartIdMap.remove(getCustomerTableKey(removedCart.getCustomerId(), removedCart.getTableId()));
        }
    }
}