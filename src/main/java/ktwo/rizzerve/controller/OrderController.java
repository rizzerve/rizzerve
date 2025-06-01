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
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
        try {
            OrderDTO orderDTO = orderService.findById(id);
            return new ResponseEntity<>(orderDTO, HttpStatus.OK);
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
            return new ResponseEntity<>("Order deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/update-items")
    public ResponseEntity<String> updateOrderItems(@PathVariable Long id, @RequestBody Map<Long, Integer> newItems) {
        try {
            orderService.updateOrderItems(id, newItems);
            return new ResponseEntity<>("Order items updated successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update order items", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
