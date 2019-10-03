package com.tyr.finance.stock.service;

import java.util.Date;

public interface StockDailyDealService {
    Date getLatestDate(String code);
}
