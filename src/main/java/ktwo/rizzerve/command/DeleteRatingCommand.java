package ktwo.rizzerve.command;

import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;

import java.util.UUID;

public class DeleteRatingCommand implements RatingCommand {
    private final RatingRepository repository;

    private final UUID id;

    public DeleteRatingCommand(String id, RatingRepository repository) {
        this.id = UUID.fromString(id);
        this.repository = repository;
    }

    @Override
    public Rating execute() {
        Rating rating = repository.findById(id).orElse(null);
        if (rating != null) {
            repository.deleteById(id);
        }
        return rating;
    }
}