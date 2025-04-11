package k2.rizzerve.service;

import k2.rizzerve.command.RatingCommand;

public interface RatingService {
    void executeCommand(RatingCommand command);
}
