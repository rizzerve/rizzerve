package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.Category;
import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.repository.RatingRepository;
import ktwo.rizzerve.service.CategoryService;
import ktwo.rizzerve.service.MenuItemService;
import ktwo.rizzerve.web.MenuItemWithRatingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menuitems")
public class MenuItemRestController {

    private final MenuItemService svc;
    private final CategoryService catSvc;
    private final RatingRepository ratingRepo;

    @Autowired
    public MenuItemRestController(MenuItemService svc, CategoryService catSvc, RatingRepository ratingRepo) {
        this.svc = svc;
        this.catSvc = catSvc;
        this.ratingRepo = ratingRepo;
    }

    @GetMapping
    public List<MenuItem> listAll() {
        return svc.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getOne(@PathVariable Long id) {
        MenuItem item = svc.getById(id);
        return (item != null) ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<MenuItem> create(@RequestBody MenuItem menuItem) {
        Category cat = catSvc.getById(menuItem.getCategory().getId());
        menuItem.setCategory(cat);
        MenuItem saved = svc.add(menuItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> update(@PathVariable Long id, @RequestBody MenuItem menuItem) {
        menuItem.setId(id);
        Category cat = catSvc.getById(menuItem.getCategory().getId());
        menuItem.setCategory(cat);
        MenuItem updated = svc.update(id, menuItem);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/with-ratings")
    public List<MenuItemWithRatingDTO> listAllWithRating() {
        List<MenuItem> items = svc.listAll();
        return items.stream().map(item -> {
            MenuItemWithRatingDTO dto = new MenuItemWithRatingDTO();
            dto.setId(item.getId());
            dto.setName(item.getName());
            dto.setDescription(item.getDescription());
            dto.setPrice(item.getPrice().intValue());
            dto.setAvailable(item.isAvailable());

            Double avg = ratingRepo.findAverageByMenuItemId(item.getId());
            dto.setAverageRating(avg != null ? avg : -1);

            MenuItemWithRatingDTO.CategoryDTO catDto = new MenuItemWithRatingDTO.CategoryDTO();
            catDto.setId(item.getCategory().getId());
            catDto.setName(item.getCategory().getName());
            dto.setCategory(catDto);

            return dto;
        }).toList();
    }
}