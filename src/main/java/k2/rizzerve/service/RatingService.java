package k2.rizzerve.service;

import k2.rizzerve.command.RatingCommand;
import k2.rizzerve.model.Rating;

public interface RatingService {
    Rating executeCommand(RatingCommand command);
}
