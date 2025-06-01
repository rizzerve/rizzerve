package ktwo.rizzerve.controller;

import ktwo.rizzerve.dto.OrderDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;
import ktwo.rizzerve.service.OrderService;

import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderPageController {
    @Autowired
    private OrderService orderService;

    @GetMapping("")
    public String showOrderListPage() {
        return "order_list";
    }

    @GetMapping("/new")
    public String showNewOrderPage() {
        return "new_order";
    }

    @GetMapping("/{id}")
    public String viewOrderPage(@PathVariable Long id, Model model) {
        OrderDTO order = orderService.findById(id);
        model.addAttribute("order", order);
        return "view_order";
    }

    @GetMapping("/{id}/edit")
    public String showEditOrderPage(@PathVariable Long id) {
        return "edit_order";
    }
}
