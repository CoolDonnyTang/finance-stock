package com.tyr.finance.stock.vo.rangebreak;

import lombok.Data;

import java.util.Date;

@Data
public class CalculateResultVo {
    private String stockName;
    private String startDate;
    private String endDate;
    private Float useCoefficient;
    private String coefficientStarDate;
    private String coefficientEndDate;
    private Float profit;
    private Float realPriceChange;
}
