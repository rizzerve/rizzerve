package k2.rizzerve.command;

import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;

public class DeleteRatingCommand implements RatingCommand {

    private final String id;
    private final RatingRepository repository;

    public DeleteRatingCommand(String id, RatingRepository repository) {
        this.id = id;
        this.repository = repository;
    }

    @Override
    public Rating execute() {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Rating with id " + id + " does not exist.");
        }
        return repository.deleteById(id);
    }
}
