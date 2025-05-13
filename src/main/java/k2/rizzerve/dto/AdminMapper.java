package k2.rizzerve.dto;

import k2.rizzerve.model.Admin;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AdminMapper {
    public static AdminDto toDto(Admin a) {
        AdminDto d = new AdminDto();
        d.id = a.getId();
        d.name = a.getName();
        d.username = a.getUsername();
        return d;
    }

    public static void updateEntity(Admin a, AdminDto d,
                                    BCryptPasswordEncoder enc) {
        if (d.name     != null) a.setName(d.name);
        if (d.username != null) a.setUsername(d.username);
    }
}