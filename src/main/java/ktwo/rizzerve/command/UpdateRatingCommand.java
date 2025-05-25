package ktwo.rizzerve.command;

import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;

import java.util.UUID;

public class UpdateRatingCommand implements RatingCommand {
    private final Rating updated;
    private final RatingRepository repository;
    private final UUID id;

    public UpdateRatingCommand(String id, Rating updated, RatingRepository repository) {
        this.id = UUID.fromString(id);
        this.updated = updated;
        this.repository = repository;
    }

    @Override
    public Rating execute() {
        if (!repository.existsById(id)) return null;
        return repository.save(updated);
    }
}