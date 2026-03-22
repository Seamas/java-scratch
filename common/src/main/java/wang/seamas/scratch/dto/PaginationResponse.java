package wang.seamas.scratch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse<T> {

    private List<T> data;
    private int total;
    private int pageIndex;
    private int pageSize;
}
