package ktwo.rizzerve.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RatingPageController {

    @GetMapping("/rate")
    public String showRatePage() {
        return "rating";
    }

    @GetMapping("/edit")
    public String showEditPage() {
        return "edit";
    }

    @GetMapping("/rate/menu")
    public String menuRatingPage() {
        return "rating_menu";
    }

    @GetMapping("/edit-menu")
    public String showEditMenuPage() {
        return "edit_menu";
    }
}