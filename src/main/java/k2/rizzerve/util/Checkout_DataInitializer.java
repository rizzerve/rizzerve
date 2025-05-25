package k2.rizzerve.util;

import k2.rizzerve.model.User;
import k2.rizzerve.model.Checkout_Product;
import k2.rizzerve.model.Checkout_Cart;
import k2.rizzerve.model.CartItem;
import k2.rizzerve.repository.UserRepository;
import k2.rizzerve.repository.Checkout_ProductRepository;
import k2.rizzerve.repository.CartRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Component
public class Checkout_DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final Checkout_ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    // User IDs will be controlled here
    public static final Long USER_ID_ALICE = 1L;
    public static final Long USER_ID_BOB = 2L;
    public static final Long USER_ID_CHARLIE = 3L;


    @Autowired
    public Checkout_DataInitializer(UserRepository userRepository,
                                    Checkout_ProductRepository productRepository,
                                    CartRepository cartRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Optional: Clear previous data from in-memory stores
        // userRepository.deleteAll(); // if exists
        // productRepository.deleteAll(); // if exists
        cartRepository.clearAll(); // Clears carts, cart items, and user-cart links

        // --- Products (shared by all users) ---
        Checkout_Product pizza = productRepository.save(new Checkout_Product("Pepperoni Pizza", new BigDecimal("12.99")));
        Checkout_Product cola = productRepository.save(new Checkout_Product("Cola Drink", new BigDecimal("1.99")));
        Checkout_Product fries = productRepository.save(new Checkout_Product("French Fries", new BigDecimal("3.49")));
        Checkout_Product salad = productRepository.save(new Checkout_Product("Garden Salad", new BigDecimal("7.50")));
        Checkout_Product water = productRepository.save(new Checkout_Product("Bottled Water", new BigDecimal("1.00")));

        // --- User 1: Alice ---
        User alice = new User("alice", "alice@example.com", passwordEncoder.encode("passalice"));
        alice.setId(USER_ID_ALICE); // Set ID explicitly
        alice.addRole("ROLE_USER");
        userRepository.save(alice);

        Checkout_Cart aliceCart = new Checkout_Cart(alice);
        Set<CartItem> aliceItems = new HashSet<>();
        aliceItems.add(new CartItem(null, pizza, 1));
        aliceItems.add(new CartItem(null, cola, 2));
        aliceCart.setItems(aliceItems);
        cartRepository.save(aliceCart);

        // --- User 2: Bob ---
        User bob = new User("bob", "bob@example.com", passwordEncoder.encode("passbob"));
        bob.setId(USER_ID_BOB); // Set ID explicitly
        bob.addRole("ROLE_USER");
        userRepository.save(bob);

        Checkout_Cart bobCart = new Checkout_Cart(bob);
        Set<CartItem> bobItems = new HashSet<>();
        bobItems.add(new CartItem(null, fries, 2));
        bobItems.add(new CartItem(null, salad, 1));
        bobItems.add(new CartItem(null, water, 3));
        bobCart.setItems(bobItems);
        cartRepository.save(bobCart);

        // --- User 3: Charlie (empty cart) ---
        User charlie = new User("charlie", "charlie@example.com", passwordEncoder.encode("passcharlie"));
        charlie.setId(USER_ID_CHARLIE); // Set ID explicitly
        charlie.addRole("ROLE_USER");
        userRepository.save(charlie);

        Checkout_Cart charlieCart = new Checkout_Cart(charlie);
        // Charlie's cart starts empty
        cartRepository.save(charlieCart);


        System.out.println("DataInitializer: Sample data created for multiple users.");
        userRepository.findAll().forEach(u -> {
            System.out.println("User: " + u.getUsername() + " (ID: " + u.getId() + ")");
            cartRepository.findByUserId(u.getId()).ifPresent(cart -> {
                System.out.println("  Cart ID: " + cart.getId() + " with " + cart.getItems().size() + " item types.");
                cart.getItems().forEach(item ->
                        System.out.println("    - " + item.getProduct().getName() + " x" + item.getQuantity())
                );
            });
        });
    }
}