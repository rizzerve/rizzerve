package ktwo.rizzerve.strategy;

import ktwo.rizzerve.model.Rating;
import org.springframework.stereotype.Component;

@Component
public class FiveStarRatingValidation implements RatingValidationStrategy {

    @Override
    public void validate(Rating rating) {
        int value = rating.getRatingValue();
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
    }
}