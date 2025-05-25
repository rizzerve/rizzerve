package ktwo.rizzerve.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RatingPageController {

    private static final Logger log = LoggerFactory.getLogger(RatingPageController.class);

    @GetMapping("/rate")
    public String showRatePage() {
        log.info("GET /rate — Rendering global rating page");
        return "rating";
    }

    @GetMapping("/edit")
    public String showEditPage() {
        log.info("GET /edit — Rendering global edit rating page");
        return "edit";
    }

    @GetMapping("/rate/menu")
    public String showMenuRatingPage() {
        log.info("GET /rate/menu — Rendering per-menu rating page");
        return "rating_menu";
    }

    @GetMapping("/edit-menu")
    public String showEditMenuPage() {
        log.info("GET /edit-menu — Rendering per-menu edit rating page");
        return "edit_menu";
    }
}
