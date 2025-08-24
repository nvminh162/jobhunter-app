package com.nvminh162.jobhunter.dto;

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
public class ResResultPaginationDTO {
  private Meta meta;
  private Object result; // chưa biết trước kiểu dữ liệu nên dùng OBJ

  @Getter
  @Setter
  public static class Meta {
    private int page; // Client vừa gọi trang nào
    private int pageSize; // Tổng số trang là bao nhiêu/Tối đa bao nhiêu phần tử
    private int pages; // Tổng số trang chia trong dbs
    private long total; // Tổng phần tử có trong dbs
  }
}
