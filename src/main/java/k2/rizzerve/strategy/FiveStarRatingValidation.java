package k2.rizzerve.strategy;

import org.springframework.stereotype.Component;

@Component
public class FiveStarRatingValidation implements RatingValidationStrategy {

    @Override
    public boolean isValid(int ratingValue) {
        return ratingValue >= 1 && ratingValue <= 5;
    }
}