package wang.seamas.scratch.dto;

import lombok.Data;

@Data
public class PaginationResponse<T> {
    private int total;
    private int pageSize;
    private int pageIndex;
    private T data;
}
