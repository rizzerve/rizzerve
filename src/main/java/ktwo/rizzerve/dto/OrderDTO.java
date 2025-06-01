package ktwo.rizzerve.dto;

import ktwo.rizzerve.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String username;
    private Long tableId;
    private Map<Long, Integer> items;
    private OrderStatus status;
}
