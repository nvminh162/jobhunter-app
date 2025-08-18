package com.nvminh162.jobhunter.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meta {
    private int page; // Client vừa gọi trang nào
    private int pageSize; // Tổng số trang là bao nhiêu/Tối đa bao nhiêu phần tử
    private int pages; // Tổng số trang chia trong dbs
    private long total; // Tổng phần tử có trong dbs
}
