package ktwo.rizzerve.controller;

import ktwo.rizzerve.dto.AdminDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminViewControllerTest {

    @InjectMocks
    private AdminViewController controller;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void showLoginForm_shouldReturnLoginView() {
        String view = controller.showLoginForm();
        assertThat(view).isEqualTo("admin/login");
    }

    @Test
    void showRegisterForm_shouldAddAdminDtoAndReturnRegisterView() {
        String view = controller.showRegisterForm(model);

        verify(model).addAttribute(eq("adminDto"), any(AdminDto.class));
        assertThat(view).isEqualTo("admin/register");
    }

    @Test
    void showProfile_shouldReturnProfileView() {
        String view = controller.showProfile();
        assertThat(view).isEqualTo("admin/profile");
    }

    @Test
    void showEditForm_shouldAddAdminDtoAndReturnEditProfileView() {
        String view = controller.showEditForm(model);

        verify(model).addAttribute(eq("adminDto"), any(AdminDto.class));
        assertThat(view).isEqualTo("admin/edit-profile");
    }
}
