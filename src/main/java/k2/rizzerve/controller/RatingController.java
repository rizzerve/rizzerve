package k2.rizzerve.controller;

import k2.rizzerve.command.CreateRatingCommand;
import k2.rizzerve.command.DeleteRatingCommand;
import k2.rizzerve.command.UpdateRatingCommand;
import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;
import k2.rizzerve.service.RatingService;
import k2.rizzerve.strategy.RatingValidationStrategy;
import k2.rizzerve.web.CreateRatingRequest;
import k2.rizzerve.web.UpdateRatingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {
    private final RatingService service;
    private final RatingRepository repo;
    private final RatingValidationStrategy validation;

    public RatingController(RatingService service,
                            RatingRepository repo,
                            RatingValidationStrategy validation)
    {
        this.service = service;
        this.repo = repo;
        this.validation = validation;
    }

    @GetMapping
    public List<Rating> all(@RequestParam(required=false) String menuId) {
        return (menuId != null)
                ? service.getByMenu(menuId)
                : service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rating> byId(@PathVariable String id) {
        Rating r = service.getById(id);
        return (r == null)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(r);
    }

    @PostMapping
    public ResponseEntity<Rating> create(@RequestBody CreateRatingRequest rq) {
        CreateRatingCommand cmd = new CreateRatingCommand(
                rq.getId(),
                rq.getMenuId(),
                rq.getUsername(),
                rq.getRatingValue(),
                validation,
                repo
        );
        Rating created = service.executeCommand(cmd);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rating> update(@PathVariable String id,
                                         @RequestBody UpdateRatingRequest rq)
    {
        Rating updated = new Rating(id, rq.menuId, rq.username, rq.ratingValue, validation);
        UpdateRatingCommand cmd = new UpdateRatingCommand(id, updated, repo);
        Rating result = service.executeCommand(cmd);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        DeleteRatingCommand cmd = new DeleteRatingCommand(id, repo);
        service.executeCommand(cmd);
        return ResponseEntity.noContent().build();
    }
}
