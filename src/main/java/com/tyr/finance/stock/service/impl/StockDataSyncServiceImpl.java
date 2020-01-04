package com.tyr.finance.stock.service.impl;

import com.tyr.finance.stock.entity.StockDailyDeal;
import com.tyr.finance.stock.entity.StockDailyDealSyncLog;
import com.tyr.finance.stock.entity.StockHeader;
import com.tyr.finance.stock.repository.StockDailyDealRepository;
import com.tyr.finance.stock.repository.StockDailyDealSyncLogRepository;
import com.tyr.finance.stock.repository.StockHeaderRepository;
import com.tyr.finance.stock.service.StockDailyDealService;
import com.tyr.finance.stock.service.StockDataSyncService;
import com.tyr.finance.stock.util.DateUtil;
import com.tyr.finance.stock.util.RemoteDataUtil;
import com.tyr.finance.stock.vo.ResultVo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class StockDataSyncServiceImpl implements StockDataSyncService {
    private static final Logger logger = LoggerFactory.getLogger(StockDataSyncServiceImpl.class);

    @Autowired
    private StockHeaderRepository stockHeaderRepository;
    @Autowired
    private StockDailyDealRepository stockDailyDealRepository;
    @Autowired
    private StockDailyDealSyncLogRepository stockDailyDealSyncLogRepository;

    @Autowired
    private StockDailyDealService stockDailyDealService;

    @Override
    @Transactional
    public boolean saveData(StockHeader stockHeader, List<StockDailyDeal> stockDailyDeals) throws Exception {
        StockHeader exist = stockHeaderRepository.findByCode(stockHeader.getCode());
        if(exist==null) {
            stockHeaderRepository.save(stockHeader);
            exist = stockHeader;
        }
        for (StockDailyDeal sdd : stockDailyDeals) {
            sdd.setStockId(exist.getOid());
        }
        Collections.reverse(stockDailyDeals);
        stockDailyDealRepository.saveAll(stockDailyDeals);
        return false;
    }

    public StockDailyDealSyncLog createOrUpdateData(String code, Date startDate, Date endDate, Date latestDealDay) {
        try {
            //格式化时间
            startDate = DateUtil.fomatToyyyy_MM_dd(startDate);
            endDate = DateUtil.fomatToyyyy_MM_dd(endDate);

            logger.info("sync data: {code:" + code +", start:" + DateUtil.fomatToyyyy_MM_ddStr(startDate) +", end:" + DateUtil.fomatToyyyy_MM_ddStr(endDate) + "}");

            StockDailyDealSyncLog log = new StockDailyDealSyncLog();
            log.setStockCode(code);
            log.setEntryDatetime(new Timestamp(System.currentTimeMillis()));

            Date latestDate = stockDailyDealService.getLatestDate(code);
            log.setCurrentLatestDate(latestDate);
            log.setSyncStartDate(startDate);
            log.setSyncEndDate(endDate);
            log.setSynceStatus(SUCCESS);
            if (latestDate != null && (latestDate.after(latestDealDay)||latestDate.equals(latestDealDay))) {
                log.setSyncDesc("数据是最新的，不需要进行同步");
                stockDailyDealSyncLogRepository.save(log);
                return log;
            }
            startDate = latestDate != null ? DateUtil.add(latestDate, Calendar.DAY_OF_MONTH, 1) : startDate;
            log.setSyncStartDate(startDate);
            List<StockDailyDeal> stockDailyDeals = null;
            try {
                stockDailyDeals = RemoteDataUtil.getDailyData(code, startDate, endDate);
            } catch (Exception e) {
                log.setSynceStatus(ERROR);
                log.setSyncDesc("调用远程API错误");
                stockDailyDealSyncLogRepository.save(log);
                logger.error(e.getMessage(), e);
                return log;
            }
            if (CollectionUtils.isEmpty(stockDailyDeals)) {
                log.setSynceStatus(FAILED);
                log.setSyncDesc("未从远程拉取到相应的数据");
                stockDailyDealSyncLogRepository.save(log);
                return log;
            }
            StockHeader header = new StockHeader();
            header.setCode(code);
            header.setName(stockDailyDeals.get(0).getCurrentStockName());
            try {
                saveData(header, stockDailyDeals);
                log.setSyncDesc("数据同步成功");
                stockDailyDealSyncLogRepository.save(log);
                return log;
            } catch (Exception e) {
                log.setSynceStatus(ERROR);
                log.setSyncDesc("保存数据出错：" + e.getMessage());
                stockDailyDealSyncLogRepository.save(log);
                logger.error(e.getMessage(), e);
                return log;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Async("CommonPool")
    public void createOrUpdateDataAsync(String code, Date startDate, Date endDate, Date latestDealDay) {
        createOrUpdateData(code, startDate, endDate, latestDealDay);
    }

}
