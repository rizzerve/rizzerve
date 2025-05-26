package ktwo.rizzerve.controller;

import ktwo.rizzerve.service.TableService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tables")
public class TablePageController {

    private final TableService tableService;

    public TablePageController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public String showTables(Model model, @RequestParam(value = "editId", required = false) Long editId) {
        model.addAttribute("tables", tableService.getAllTables());
        model.addAttribute("editId", editId);
        return "tables";
    }

    @PostMapping("/edit")
    public String editTable(@RequestParam("id") Long id) {
        return "redirect:/tables?editId=" + id;
    }
}
