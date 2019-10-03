package com.tyr.finance.stock.stock;

import com.tyr.finance.stock.bo.SimpleStockDealDataBo;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface Stock {
    List<SimpleStockDealDataBo> getDeals();
    Date getCycleEndDate(Date currentDate);
}
