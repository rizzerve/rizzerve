package ktwo.rizzerve.controller;

import ktwo.rizzerve.repository.ZZZ_CartRepository;
import ktwo.rizzerve.service.ZZZ_CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@Controller
public class ZZZ_CheckoutController {
    private final ZZZ_CheckoutService checkoutService;
    @Autowired
    private final ZZZ_CartRepository cartRepository;

    @Autowired
    public ZZZ_CheckoutController(ZZZ_CheckoutService checkoutService, ZZZ_CartRepository cartRepository) {
        this.checkoutService = checkoutService;
        this.cartRepository = cartRepository;
    }

    @GetMapping("/checkout")
    public String checkoutPage(@RequestParam Long customerId,
                               @RequestParam Long tableId,
                               Model model) {
        try {
            Map<String, Object> cartDetails = checkoutService.getCartDetails(
                    cartRepository.findByCustomerIdAndTableId(customerId, tableId)
                            .orElseThrow().getId()
            );
            model.addAllAttributes(cartDetails);
        } catch (Exception e) {
            model.addAttribute("error", "Cart not found for customer");
        }
        return "checkout";
    }
}