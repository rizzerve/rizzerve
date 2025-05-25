package ktwo.rizzerve.repository;

import ktwo.rizzerve.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    List<Rating> findByMenuItem_Id(Long menuItemId);
}