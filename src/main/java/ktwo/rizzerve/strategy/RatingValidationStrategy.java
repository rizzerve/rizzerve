package ktwo.rizzerve.strategy;

import ktwo.rizzerve.model.Rating;

public interface RatingValidationStrategy {
    void validate(Rating rating);
}