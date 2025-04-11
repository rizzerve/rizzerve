package k2.rizzerve.service;

import k2.rizzerve.command.RatingCommand;

public class RatingServiceImpl implements RatingService {

    @Override
    public void executeCommand(RatingCommand command) {
        command.execute();
    }
}
