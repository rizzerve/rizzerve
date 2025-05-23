package ktwo.rizzerve.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import ktwo.rizzerve.strategy.RatingValidationStrategy;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MenuItem menuItem;

    private String username;
    private int ratingValue;

    public Rating(MenuItem menuItem, String username, int ratingValue, RatingValidationStrategy validation) {
        this.menuItem = menuItem;
        this.username = username;
        this.ratingValue = ratingValue;
        validation.validate(this);
    }

    public Rating(UUID id, MenuItem menuItem, String username, int ratingValue, RatingValidationStrategy validation) {
        this.id = id;
        this.menuItem = menuItem;
        this.username = username;
        this.ratingValue = ratingValue;
        validation.validate(this);
    }

}
