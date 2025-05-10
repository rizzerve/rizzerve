package k2.rizzerve.dto;

public class AdminDto {
    public Long id;
    public String name;
    public String username;
    public String password;

    public AdminDto() {}

    public AdminDto(Long id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }
}