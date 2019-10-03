package com.tyr.finance.stock.service.impl;

import com.tyr.finance.stock.repository.StockDailyDealRepository;
import com.tyr.finance.stock.repository.StockHeaderRepository;
import com.tyr.finance.stock.service.StockDailyDealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StockDailyDealServiceImpl implements StockDailyDealService {

    @Autowired
    private StockHeaderRepository stockHeaderRepository;
    @Autowired
    private StockDailyDealRepository stockDailyDealRepository;

    @Override
    public Date getLatestDate(String code) {
        return stockDailyDealRepository.getLatestDateByCode(code);
    }
}
