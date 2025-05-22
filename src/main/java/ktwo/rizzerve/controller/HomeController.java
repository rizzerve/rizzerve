package k2.rizzerve.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.security.Principal;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String homePage(Model model, Principal principal) {
        if (principal != null) {
            return "admin_home";
        } else {
            return "home";
        }
    }
}
