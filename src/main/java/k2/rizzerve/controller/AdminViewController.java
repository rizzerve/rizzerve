package k2.rizzerve.controller;

import k2.rizzerve.dto.AdminDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {
    @GetMapping("/login")
    public String showLoginForm() {
        return "admin/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("adminDto", new AdminDto());
        return "admin/register";
    }

    @GetMapping("/profile/view")
    public String showProfile() {
        return "admin/profile";
    }

    @GetMapping("/profile/editform")
    public String showEditForm(Model model) {
        model.addAttribute("adminDto", new AdminDto());
        return "admin/edit-profile";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            @AuthenticationPrincipal UserDetails user
    ) {
        model.addAttribute("username", user.getUsername());
        return "admin/dashboard";
    }
}

