package k2.rizzerve.service;

import k2.rizzerve.command.RatingCommand;
import k2.rizzerve.model.Rating;

import java.util.List;

public interface RatingService {
    Rating executeCommand(RatingCommand cmd);
    Rating getById(String id);
    List<Rating> getAll();
    List<Rating> getByMenu(String menuId);
}
