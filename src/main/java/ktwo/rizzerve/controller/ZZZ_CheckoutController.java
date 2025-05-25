package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.ZZZ_Cart;
import k2.rizzerve.model.User;
import ktwo.rizzerve.repository.ZZZ_CartRepository;
import k2.rizzerve.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class ZZZ_CheckoutController {

    private final ZZZ_CartRepository cartRepository;
    private final UserRepository userRepository;

    // Default user to show if no userId is specified in the request
    private static final Long DEFAULT_USER_ID = ZZZ_DataInitializer.USER_ID_ALICE;

    @Autowired
    public ZZZ_CheckoutController(ZZZ_CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/checkout")
    public String checkoutPage(@RequestParam(name = "userId", required = false) Long selectedUserId, Model model) {

        Long userIdToLoad = (selectedUserId != null) ? selectedUserId : DEFAULT_USER_ID;

        List<User> allUsers = userRepository.findAll(); // Fetch all users for the dropdown
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("selectedUserId", userIdToLoad);

        Optional<User> userOptional = userRepository.findById(userIdToLoad);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("username", user.getUsername());

            Optional<ZZZ_Cart> cartOptional = cartRepository.findByUserId(user.getId());
            if (cartOptional.isPresent()) {
                model.addAttribute("cart", cartOptional.get());
                // Make sure items are eagerly fetched or explicitly loaded if lazy
                // With EAGER fetch on Cart.items, this should be fine.
                // If lazy, you might need to explicitly initialize cartOptional.get().getItems()
                model.addAttribute("cartItems", cartOptional.get().getItems());
            } else {
                model.addAttribute("cart", null);
                model.addAttribute("cartItems", Collections.emptyList());
                model.addAttribute("message_page_level", "Cart is empty or could not be found for " + user.getUsername() + ".");
            }
        } else {
            model.addAttribute("username", "User not found");
            model.addAttribute("cart", null);
            model.addAttribute("cartItems", Collections.emptyList());
            model.addAttribute("message_page_level", "User with ID " + userIdToLoad + " not found. Cannot display cart.");
        }

        return "checkout";
    }
}