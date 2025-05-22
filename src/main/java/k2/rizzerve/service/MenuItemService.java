package k2.rizzerve.service;

import k2.rizzerve.model.MenuItem;
import java.util.List;

public interface MenuItemService {
    MenuItem add(MenuItem item);
    List<MenuItem> listAll();
    MenuItem getById(Long id);
    MenuItem update(Long id, MenuItem data);
    void delete(Long id);
}