package k2.rizzerve.service;

import k2.rizzerve.command.RatingCommand;
import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return repository.findById(id);
    }

    @Override
    public List<Rating> getByMenu(String menuId) {
        return repository.findByMenuId(menuId);
    }
}