package com.tyr.finance.stock.rangebreak.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RangeBreakResultBO {
    private float startPrice;
    private float endPrice;
    private float realPriceChange;
    private int startIndex;
    private int endIndex;
    private float coefficient;
    private float profit = 0;
    private float profitRatio = 0;
    private List<Float> profitDetail = new ArrayList<>();
    private List<Float> profitRatioDetail = new ArrayList<>();
}
