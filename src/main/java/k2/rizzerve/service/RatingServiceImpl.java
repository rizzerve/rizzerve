package k2.rizzerve.service;

import k2.rizzerve.command.RatingCommand;
import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;
import org.springframework.stereotype.Service;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository repository;

    public RatingServiceImpl(RatingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Rating executeCommand(RatingCommand command) {
        Rating result = command.execute();
        repository.save(result);
        return result;
    }
}
