package wang.seamas.scratch.dto;

import lombok.Data;

@Data
public class PaginationRequest {
    private int pageIndex;
    private int pageSize;
    private String sort;
    private String order;
}
