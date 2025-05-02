package k2.rizzerve.controller;

import k2.rizzerve.command.CreateRatingCommand;
import k2.rizzerve.command.DeleteRatingCommand;
import k2.rizzerve.command.UpdateRatingCommand;
import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;
import k2.rizzerve.service.RatingService;
import k2.rizzerve.strategy.FiveStarRatingValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final RatingRepository ratingRepository;

    @Autowired
    public RatingController(RatingService ratingService, RatingRepository ratingRepository) {

}}
