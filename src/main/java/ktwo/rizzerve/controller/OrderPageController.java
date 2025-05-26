package ktwo.rizzerve.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/order")
public class OrderPageController {
    @GetMapping("/")
    public String showOrderListPage() {
        return "order_list";
    }

    @GetMapping("/new")
    public String showNewOrderPage() {
        return "new_order";
    }

    @GetMapping("/{id}")
    public String viewOrderPage(@PathVariable Long id) {
        return "view_order";
    }

    @GetMapping("/{id}/edit")
    public String showEditOrderPage(@PathVariable Long id) {
        return "edit_order";
    }
}
