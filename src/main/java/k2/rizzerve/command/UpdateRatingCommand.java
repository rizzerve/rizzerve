package k2.rizzerve.command;

import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;

import java.util.NoSuchElementException;

public class UpdateRatingCommand implements RatingCommand {
    private final String id;
    private final Rating updated;
    private final RatingRepository repo;

    public UpdateRatingCommand(String id, Rating updated, RatingRepository repo) {
        this.id = id;
        this.updated = updated;
        this.repo = repo;
    }

    @Override
    public Rating execute() {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Rating "+id+" not found");
        }
        repo.update(id, updated);
        return updated;
    }
}
