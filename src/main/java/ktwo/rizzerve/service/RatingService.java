package ktwo.rizzerve.service;

import ktwo.rizzerve.command.RatingCommand;
import ktwo.rizzerve.model.Rating;

import java.util.List;

public interface RatingService {
    Rating executeCommand(RatingCommand cmd);
    Rating getById(String id);
    List<Rating> getAll();
    List<Rating> getByMenu(String menuId);
}
