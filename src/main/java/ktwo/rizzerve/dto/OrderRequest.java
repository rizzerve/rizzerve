package ktwo.rizzerve.dto;

import lombok.Data;

import java.util.Map;

@Data
public class OrderRequest {
    private String username;
    private Long tableId;
    private Map<Long, Integer> items;
}
