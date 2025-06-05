package ktwo.rizzerve.repository;

import ktwo.rizzerve.model.ZZZ_Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

class ZZZ_CartRepositoryTest {

    private ZZZ_CartRepository cartRepository;
    private ZZZ_Cart cart1;
    private ZZZ_Cart cart2;

    @BeforeEach
    void setUp() {
        cartRepository = new ZZZ_CartRepository();
        cart1 = new ZZZ_Cart("user1", 1L, 10L);
        cart2 = new ZZZ_Cart("user2", 2L, 20L);
    }

    // --- Test save ---
    @Test
    void save_newCart_shouldAssignIdAndStore() {
        assertNull(cart1.getId()); // ID is null before save
        ZZZ_Cart savedCart = cartRepository.save(cart1);

        assertNotNull(savedCart.getId());
        assertEquals(cart1, savedCart);
        assertTrue(cartRepository.findById(savedCart.getId()).isPresent());
    }

    @Test
    void save_existingCart_shouldUpdate() {
        ZZZ_Cart savedCart = cartRepository.save(cart1);
        Long originalId = savedCart.getId();
        savedCart.addItem(101L, 5); // Modify the cart

        ZZZ_Cart updatedCart = cartRepository.save(savedCart); // Save again
        assertEquals(originalId, updatedCart.getId()); // ID should not change
        assertEquals(5, updatedCart.getFoodItems().get(101L));
    }

    // --- Test findById ---
    @Test
    void findById_existingCart_shouldReturnCart() {
        ZZZ_Cart savedCart = cartRepository.save(cart1);
        Optional<ZZZ_Cart> found = cartRepository.findById(savedCart.getId());
        assertTrue(found.isPresent());
        assertEquals(savedCart, found.get());
    }

    @Test
    void findById_nonExistingCart_shouldReturnEmpty() {
        Optional<ZZZ_Cart> found = cartRepository.findById(999L);
        assertFalse(found.isPresent());
    }

    // --- Test findByCustomerIdAndTableId ---
    @Test
    void findByCustomerIdAndTableId_existingCart_shouldReturnCart() {
        cartRepository.save(cart1); // CustomerId 1, TableId 10
        Optional<ZZZ_Cart> found = cartRepository.findByCustomerIdAndTableId(1L, 10L);
        assertTrue(found.isPresent());
        assertEquals(cart1.getUsername(), found.get().getUsername());
    }

    @Test
    void findByCustomerIdAndTableId_nonExistingCombination_shouldReturnEmpty() {
        cartRepository.save(cart1);
        Optional<ZZZ_Cart> found = cartRepository.findByCustomerIdAndTableId(1L, 99L); // Wrong table
        assertFalse(found.isPresent());
    }

    @Test
    void findByCustomerIdAndTableId_nullParameters_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            cartRepository.findByCustomerIdAndTableId(null, 10L);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            cartRepository.findByCustomerIdAndTableId(1L, null);
        });
    }


    // --- Test findAll ---
    @Test
    void findAll_noCarts_shouldReturnEmptyList() {
        assertTrue(cartRepository.findAll().isEmpty());
    }

    @Test
    void findAll_withCarts_shouldReturnAllCarts() {
        cartRepository.save(cart1);
        cartRepository.save(cart2);
        List<ZZZ_Cart> allCarts = cartRepository.findAll();
        assertEquals(2, allCarts.size());
        assertTrue(allCarts.contains(cart1));
        assertTrue(allCarts.contains(cart2));
    }

    // --- Test deleteById ---
    @Test
    void deleteById_existingCart_shouldRemoveCartAndLookupEntry() {
        ZZZ_Cart savedCart = cartRepository.save(cart1); // CustomerId 1, TableId 10
        Long cartId = savedCart.getId();

        cartRepository.deleteById(cartId);

        assertFalse(cartRepository.findById(cartId).isPresent());
        // Also check if the customerTableToCartIdMap entry is gone
        assertFalse(cartRepository.findByCustomerIdAndTableId(1L, 10L).isPresent());
    }

    @Test
    void deleteById_nonExistingCart_shouldDoNothing() {
        cartRepository.save(cart1);
        cartRepository.deleteById(999L); // Non-existing ID
        assertEquals(1, cartRepository.findAll().size()); // cart1 should still be there
    }
}