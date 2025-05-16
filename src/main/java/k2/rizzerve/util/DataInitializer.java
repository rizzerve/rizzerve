package k2.rizzerve.util;

import k2.rizzerve.model.User;
import k2.rizzerve.model.Product;
import k2.rizzerve.model.Cart;
import k2.rizzerve.model.CartItem;
import k2.rizzerve.repository.UserRepository;
import k2.rizzerve.repository.ProductRepository;
import k2.rizzerve.repository.CartRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    // User IDs will be controlled here
    public static final Long USER_ID_ALICE = 1L;
    public static final Long USER_ID_BOB = 2L;
    public static final Long USER_ID_CHARLIE = 3L;


    @Autowired
    public DataInitializer(UserRepository userRepository,
                           ProductRepository productRepository,
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
        Product pizza = productRepository.save(new Product("Pepperoni Pizza", new BigDecimal("12.99")));
        Product cola = productRepository.save(new Product("Cola Drink", new BigDecimal("1.99")));
        Product fries = productRepository.save(new Product("French Fries", new BigDecimal("3.49")));
        Product salad = productRepository.save(new Product("Garden Salad", new BigDecimal("7.50")));
        Product water = productRepository.save(new Product("Bottled Water", new BigDecimal("1.00")));

        // --- User 1: Alice ---
        User alice = new User("alice", "alice@example.com", passwordEncoder.encode("passalice"));
        alice.setId(USER_ID_ALICE); // Set ID explicitly
        alice.addRole("ROLE_USER");
        userRepository.save(alice);

        Cart aliceCart = new Cart(alice);
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

        Cart bobCart = new Cart(bob);
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

        Cart charlieCart = new Cart(charlie);
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