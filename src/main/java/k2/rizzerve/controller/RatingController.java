package k2.rizzerve.controller;

import k2.rizzerve.command.CreateRatingCommand;
import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;
import k2.rizzerve.service.RatingService;
import k2.rizzerve.strategy.RatingValidationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final RatingRepository ratingRepository;
    private final RatingValidationStrategy strategy;

    @Autowired
    public RatingController(RatingService ratingService, RatingRepository ratingRepository, RatingValidationStrategy strategy) {
        this.ratingService = ratingService;
        this.ratingRepository = ratingRepository;
        this.strategy = strategy;
    }

    @PostMapping
    public ResponseEntity<Rating> createRating(
            @RequestParam String id,
            @RequestParam String menuId,
            @RequestParam String username,
            @RequestParam int ratingValue
    ) {
        CreateRatingCommand command = new CreateRatingCommand(id, menuId, username, ratingValue, strategy, ratingRepository);
        Rating result = ratingService.executeCommand(command);
        return ResponseEntity.ok(result);
    }
}
