package ktwo.rizzerve.controller;

import java.util.List;
import java.util.UUID;

import ktwo.rizzerve.command.CreateRatingCommand;
import ktwo.rizzerve.command.DeleteRatingCommand;
import ktwo.rizzerve.command.UpdateRatingCommand;
import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import ktwo.rizzerve.service.MenuItemService;
import ktwo.rizzerve.service.RatingService;
import ktwo.rizzerve.strategy.RatingValidationStrategy;
import ktwo.rizzerve.web.CreateRatingRequest;
import ktwo.rizzerve.web.UpdateRatingRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private static final Logger log = LoggerFactory.getLogger(RatingController.class);

    private final RatingService service;
    private final RatingRepository repo;
    private final RatingValidationStrategy validation;
    private final MenuItemService menuService;

    public RatingController(RatingService service,
                            RatingRepository repo,
                            RatingValidationStrategy validation,
                            MenuItemService menuService) {
        this.service = service;
        this.repo = repo;
        this.validation = validation;
        this.menuService = menuService;
    }

    @GetMapping
    public List<Rating> all(@RequestParam(required = false) String menuId) {
        log.info("Fetching ratings with menuId={}", menuId);
        return (menuId != null) ? service.getByMenu(menuId) : service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rating> byId(@PathVariable String id) {
        log.info("Fetching rating by id={}", id);
        Rating r = service.getById(id);
        return (r == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(r);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createApi(@RequestBody CreateRatingRequest rq) {
        log.info("Creating rating: {}", rq);
        try {
            Long menuId = Long.valueOf(rq.getMenuId());
            MenuItem menuItem = menuService.getById(menuId);

            if (menuItem == null) {
                log.warn("Menu not found with ID: {}", menuId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Menu item not found with ID: " + menuId);
            }

            List<Rating> existing = repo.findByMenuItem_Id(menuId);
            boolean alreadyRated = existing.stream()
                    .anyMatch(r -> r.getUsername().equalsIgnoreCase(rq.getUsername()));
            if (alreadyRated) {
                log.warn("User {} already rated menu {}", rq.getUsername(), menuId);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("You have already rated this menu.");
            }

            CreateRatingCommand cmd = new CreateRatingCommand(menuItem, rq.getUsername(), rq.getRatingValue(), validation, repo);
            Rating created = service.executeCommand(cmd);
            log.info("Rating created: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (NumberFormatException e) {
            log.error("Invalid menu ID format: {}", rq.getMenuId(), e);
            return ResponseEntity.badRequest().body("Invalid menu ID format");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id,
                                    @RequestBody UpdateRatingRequest rq) {
        log.info("Updating rating id={}", id);
        try {
            Long menuId = Long.valueOf(rq.getMenuId());
            MenuItem menuItem = menuService.getById(menuId);

            if (menuItem == null) {
                log.warn("Menu not found with ID: {}", menuId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Menu item not found with ID: " + menuId);
            }

            Rating updated = new Rating(UUID.fromString(id), menuItem, rq.getUsername(), rq.getRatingValue(), validation);
            UpdateRatingCommand cmd = new UpdateRatingCommand(id, updated, repo);
            Rating result = service.executeCommand(cmd);
            log.info("Rating updated successfully: {}", result.getId());
            return ResponseEntity.ok(result);
        } catch (NumberFormatException e) {
            log.error("Invalid menu ID format: {}", rq.getMenuId(), e);
            return ResponseEntity.badRequest().body("Invalid menu ID format");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("Deleting rating with id={}", id);
        DeleteRatingCommand cmd = new DeleteRatingCommand(id, repo);
        service.executeCommand(cmd);
        return ResponseEntity.noContent().build();
    }
}