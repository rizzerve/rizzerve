package k2.rizzerve.command;

import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;

public class UpdateRatingCommand implements RatingCommand {

    private final String id;
    private final Rating updatedRating;
    private final RatingRepository repository;

    public UpdateRatingCommand(String id, Rating updatedRating, RatingRepository repository) {
        this.id = id;
        this.updatedRating = updatedRating;
        this.repository = repository;
    }

    @Override
    public Rating execute() {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Rating with id " + id + " does not exist.");
        }
        repository.update(id, updatedRating);
        return updatedRating;
    }
}
