package ktwo.rizzerve.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import java.security.Principal;

@Controller
public class HomeController {

    // Unauthenticated homepage (e.g. landing page)
    @GetMapping("/home")
    public String publicHomePage(Principal principal) {
        if (principal != null) {
            return "redirect:admin/home";
        }
        return "home"; 
    }

    // Authenticated homepage
    @GetMapping("admin/home")
    public String authenticatedHomePage() {
        return "admin_home"; 
    }
}
