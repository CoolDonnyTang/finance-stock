package com.tyr.finance.stock.service;

import com.tyr.finance.stock.entity.StockDailyDeal;
import com.tyr.finance.stock.entity.StockDailyDealSyncLog;
import com.tyr.finance.stock.entity.StockHeader;
import com.tyr.finance.stock.util.RemoteDataUtil;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;

public interface StockDataSyncService {
    String SUCCESS = "Success";
    String FAILED = "Failed";

    boolean saveData(StockHeader stockHeader, List<StockDailyDeal> stockDailyDeals) throws Exception;

    StockDailyDealSyncLog createOrUpdateData(String code, Date startDate, Date endDate, Date latestDealDay);
}
