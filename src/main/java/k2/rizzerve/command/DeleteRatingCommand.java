package k2.rizzerve.command;

import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;

import java.util.NoSuchElementException;

public class DeleteRatingCommand implements RatingCommand {
    private final String id;
    private final RatingRepository repo;

    public DeleteRatingCommand(String id, RatingRepository repo) {
        this.id = id;
        this.repo = repo;
    }

    @Override
    public Rating execute() {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Rating "+id+" not found");
        }
        return repo.deleteById(id);
    }
}
