package k2.rizzerve.strategy;

public class FiveStarRatingValidation implements RatingValidationStrategy {
    @Override
    public boolean isValid(int value) {
        return value >= 0 && value <= 5;
    }
}
