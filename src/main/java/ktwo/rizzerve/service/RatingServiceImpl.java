package ktwo.rizzerve.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import ktwo.rizzerve.command.RatingCommand;
import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final MeterRegistry meterRegistry;

    private Counter ratingCreatedCounter;
    private Counter ratingUpdatedCounter;

    public RatingServiceImpl(RatingRepository ratingRepository, MeterRegistry meterRegistry) {
        this.ratingRepository = ratingRepository;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void initMetrics() {
        this.ratingCreatedCounter = Counter.builder("ratings")
                .description("Total number of ratings created")
                .register(meterRegistry);

        this.ratingUpdatedCounter = Counter.builder("ratings_updated")
                .description("Total number of ratings updated")
                .register(meterRegistry);
    }


    @Override
    public Rating executeCommand(RatingCommand command) {
        Rating result = command.execute();

        String commandType = command.getClass().getSimpleName().toLowerCase();
        if (commandType.contains("create")) {
            ratingCreatedCounter.increment();
        } else if (commandType.contains("update")) {
            ratingUpdatedCounter.increment();
        }

        return result;
    }

    @Override
    public List<Rating> getAll() {
        return ratingRepository.findAll();
    }

    @Override
    public Rating getById(String id) {
        try {
            return ratingRepository.findById(UUID.fromString(id)).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public List<Rating> getByMenu(String menuId) {
        try {
            return ratingRepository.findByMenuItem_Id(Long.parseLong(menuId));
        } catch (NumberFormatException e) {
            return List.of();
        }
    }
}
