package ktwo.rizzerve.controller;

import ktwo.rizzerve.dto.OrderDTO;
import ktwo.rizzerve.dto.OrderRequest;
import ktwo.rizzerve.model.Order;
import ktwo.rizzerve.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id) {
        try {
            Order order = orderService.findById(id);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-by-username/{username}")
    public ResponseEntity<List<OrderDTO>> findByUsername(@PathVariable String username) {
        try {
            List<OrderDTO> orders = orderService.findDTOsByUsername(username);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveOrder(@RequestBody OrderRequest request) {
        try {
            orderService.createOrderFromRequest(request);
            return new ResponseEntity<>("Order created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating order", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteById(id);
            return new ResponseEntity<>("Order deleted successfully", HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        }
    }
}
