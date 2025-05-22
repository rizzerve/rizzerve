package ktwo.rizzerve.web;

import lombok.Data;

@Data
public class CreateRatingRequest {
    private String id;
    private String menuId;
    private String username;
    private int ratingValue;
}