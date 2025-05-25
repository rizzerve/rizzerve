package ktwo.rizzerve.web;

import lombok.Data;

@Data
public class MenuItemWithRatingDTO {
    private Long id;
    private String name;
    private String description;
    private int price;
    private boolean available;
    private double averageRating;
    private CategoryDTO category;

    @Data
    public static class CategoryDTO {
        private Long id;
        private String name;
    }
}