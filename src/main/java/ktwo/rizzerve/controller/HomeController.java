package ktwo.rizzerve.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @GetMapping("/")
    public String landing() {
        return "index";
    }

    @GetMapping("/home")
    public String userHome() {
        return "home";
    }

    @GetMapping("/user/input")
    public String userInput() {
        return "user_input";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "admin_home";
    }
}