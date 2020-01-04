package com.tyr.finance.stock.controller;

import com.tyr.finance.stock.entity.StockDailyDeal;
import com.tyr.finance.stock.entity.StockDailyDealSyncLog;
import com.tyr.finance.stock.entity.StockHeader;
import com.tyr.finance.stock.service.StockDailyDealService;
import com.tyr.finance.stock.service.StockDataSyncService;
import com.tyr.finance.stock.util.DateUtil;
import com.tyr.finance.stock.util.RemoteDataUtil;
import com.tyr.finance.stock.vo.ResultVo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
public class StockDataSyncController {
    private static final Logger logger = LoggerFactory.getLogger(StockDataSyncController.class);

    @Autowired
    private StockDataSyncService stockDataSyncService;
    @Autowired
    private StockDailyDealService stockDailyDealService;

    @RequestMapping(value = "/stock/{code}", method = RequestMethod.POST)
    public ResultVo<?> createOrUpdateData(@PathVariable("code") String code, Date startDate, Date endDate) throws Exception {
        ResultVo<?> result = new ResultVo<>();
        Date latestDealDay = RemoteDataUtil.getLatestDealDay();
        stockDataSyncService.createOrUpdateData(code, startDate, endDate, latestDealDay);
        return result;
    }

    @RequestMapping(value = "/stock", method = RequestMethod.POST)
    public ResultVo<?> syncAll() throws Exception {

        ResultVo<?> result = new ResultVo<>();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 1990);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = DateUtil.fomatToyyyy_MM_dd(c.getTime());
        Date endDate = DateUtil.fomatToyyyy_MM_dd(new Date());

        //最新交易日
        Date latestDealDay = RemoteDataUtil.getLatestDealDay();

        String prefixSH = "sh";
        final String prefixSZ = "sz";

        /*上海主板*/
        //同步600-604号段
        syncAll(prefixSH, 600001, 604000, startDate, endDate, latestDealDay);

        /*深圳主板*/
        //同步000-004号段
        syncAll(prefixSZ, 1, 4000, startDate, endDate, latestDealDay);

        /*深圳创业板*/
        //同步300-301号段
        syncAll(prefixSZ, 300001, 301000, startDate, endDate, latestDealDay);

        result.setStatus(200);
        result.setMessage("同步进行中...");
        return result;
    }

    private void syncAll(String prefix, int start, int maxCode, Date startDate, Date endDate, Date latestDealDay) {
        for(int i=start; i<maxCode; i++) {
            String code = prefix + String.format("%06d", i);
            try {
                stockDataSyncService.createOrUpdateDataAsync(code, startDate, endDate,latestDealDay);
                logger.info("任务已创建：" + code);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                logger.info("任务创建失败：" + code);
            }
        }
    }
}
