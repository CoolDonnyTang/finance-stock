package com.tyr.finance.stock.vo;

import lombok.Data;

@Data
public class ResultVo<T> {
    private Integer status;
    private String message;
    private T data;
}
