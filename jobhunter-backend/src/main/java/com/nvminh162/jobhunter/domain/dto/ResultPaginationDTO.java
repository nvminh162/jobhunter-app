package com.nvminh162.jobhunter.domain.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * return {
      meta: {
        current: page, //trang hiện tại
        pageSize: limit, //số lượng bản ghi đã lấy
        pages: totalPages,  //tổng số trang với điều kiện query
        total: totalItems // tổng số phần tử (số bản ghi)
      },
      result: array-data //kết quả query
    }
 */
@Getter
@Setter
public class ResultPaginationDTO {
    private Meta meta;
    private Object result; // chưa biết trước kiểu dữ liệu nên dùng OBJ
}
