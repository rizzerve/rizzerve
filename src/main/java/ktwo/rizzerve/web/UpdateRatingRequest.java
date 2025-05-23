package ktwo.rizzerve.web;

public class UpdateRatingRequest {
    public String menuId;
    public String username;
    public int ratingValue;

    public String getMenuId() {
        return menuId;
    }

    public String getUsername() {
        return username;
    }

    public int getRatingValue() {
        return ratingValue;
    }
}