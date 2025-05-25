package ktwo.rizzerve.repository;

import ktwo.rizzerve.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    List<Rating> findByMenuItem_Id(Long menuItemId);

    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.menuItem.id = :menuItemId")
    Double findAverageByMenuItemId(@Param("menuItemId") Long menuItemId);
}