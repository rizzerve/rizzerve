package k2.rizzerve.repository;

import k2.rizzerve.model.MenuComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuComponentRepository extends JpaRepository<MenuComponent, Long> {
}
