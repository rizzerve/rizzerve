package ktwo.rizzerve.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRatingRequest {
    private String id;
    private String menuId;
    private String username;
    private int ratingValue;
}