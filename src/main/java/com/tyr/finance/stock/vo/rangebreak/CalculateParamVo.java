package com.tyr.finance.stock.vo.rangebreak;

import lombok.Data;

@Data
public class CalculateParamVo {
    private String code;
    private String cycle;
    private Integer weightingNum;
    private Integer startYear;
    private Integer endYear;
}
