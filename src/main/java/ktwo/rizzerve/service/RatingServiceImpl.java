package ktwo.rizzerve.service;

import ktwo.rizzerve.command.RatingCommand;
import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RatingServiceImpl implements RatingService {
    private final RatingRepository repository;

    public RatingServiceImpl(RatingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Rating executeCommand(RatingCommand command) {
        return command.execute();
    }

    @Override
    public List<Rating> getAll() {
        return repository.findAll();
    }

    @Override
    public Rating getById(String id) {
        return repository.findById(UUID.fromString(id)).orElse(null);
    }

    @Override
    public List<Rating> getByMenu(String menuId) {
        return repository.findByMenuItem_Id(Long.valueOf(menuId));
    }
}