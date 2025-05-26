package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.Category;
import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.repository.RatingRepository;
import ktwo.rizzerve.service.CategoryService;
import ktwo.rizzerve.service.MenuItemService;
import ktwo.rizzerve.web.MenuItemWithRatingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;        // ← import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menuitems")
public class MenuItemRestController {
    private static final Logger log =
            LoggerFactory.getLogger(MenuItemRestController.class);  // ← logger

    private final MenuItemService svc;
    private final CategoryService catSvc;
    private final RatingRepository ratingRepo;

    @Autowired
    public MenuItemRestController(MenuItemService svc,
                                  CategoryService catSvc,
                                  RatingRepository ratingRepo) {
        this.svc        = svc;
        this.catSvc     = catSvc;
        this.ratingRepo = ratingRepo;
    }

    @GetMapping
    public List<MenuItem> listAll() {
        log.info("GET  /api/menuitems");
        List<MenuItem> items = svc.listAll();
        log.debug("  → {} items returned", items.size());
        return items;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getOne(@PathVariable Long id) {
        log.info("GET  /api/menuitems/{}", id);
        MenuItem item = svc.getById(id);
        if (item != null) {
            log.debug("  → found: {}", item);
            return ResponseEntity.ok(item);
        }
        log.warn("  → menuItem {} not found", id);
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<MenuItem> create(@RequestBody MenuItem menuItem) {
        log.info("POST /api/menuitems  payload: name={}", menuItem.getName());
        Category cat = catSvc.getById(menuItem.getCategory().getId());
        menuItem.setCategory(cat);
        MenuItem saved = svc.add(menuItem);
        log.info("  → created id={}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> update(@PathVariable Long id,
                                           @RequestBody MenuItem menuItem) {
        log.info("PUT  /api/menuitems/{}  payload: name={}", id, menuItem.getName());
        menuItem.setId(id);
        Category cat = catSvc.getById(menuItem.getCategory().getId());
        menuItem.setCategory(cat);
        MenuItem updated = svc.update(id, menuItem);
        if (updated != null) {
            log.info("  → updated id={}", id);
            return ResponseEntity.ok(updated);
        }
        log.warn("  → update failed, menuItem {} not found", id);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/menuitems/{}", id);
        svc.delete(id);
        log.info("  → deleted id={}", id);
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
