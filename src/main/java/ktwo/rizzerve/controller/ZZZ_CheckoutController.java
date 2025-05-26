package ktwo.rizzerve.controller;

import ktwo.rizzerve.service.ZZZ_CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class ZZZ_CheckoutController {
    private final ZZZ_CheckoutService checkoutService;

    @Autowired
    public ZZZ_CheckoutController(ZZZ_CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
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