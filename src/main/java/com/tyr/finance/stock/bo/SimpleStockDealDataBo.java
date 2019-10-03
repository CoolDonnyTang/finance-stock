package com.tyr.finance.stock.bo;

import com.tyr.finance.stock.util.myenum.CalculateCycleEnum;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
public class SimpleStockDealDataBo implements Comparable<SimpleStockDealDataBo> {
    private String stockName;
    private String stockCode;
    private Date startDate;
    private Date endDate;
    private Float topen;
    private Float tclose;
    private Float maxPrice;
    private Float minPrice;
    private Float minMaxPriceAmplitude;

    @Override
    public int compareTo(SimpleStockDealDataBo o2) {
        if(this.endDate.getTime() - o2.getEndDate().getTime() > 0){
            return 1;
        } else if(this.endDate.getTime() - o2.getEndDate().getTime() == 0) {
            return 0;
        }
        return -1;
    }
}
