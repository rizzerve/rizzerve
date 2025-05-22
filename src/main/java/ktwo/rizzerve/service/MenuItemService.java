package ktwo.rizzerve.service;

import ktwo.rizzerve.model.MenuItem;
import java.util.List;

public interface MenuItemService {
    MenuItem add(MenuItem item);
    List<MenuItem> listAll();
    MenuItem getById(Long id);
    MenuItem update(Long id, MenuItem data);
    void delete(Long id);
}