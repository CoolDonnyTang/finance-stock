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

        //上海
        final String prefix1 = "sh";
        final int maxCode1 = 699999;
        final int start1 = 600001;
        Thread t1 = new Thread(()->{
            syncAll(prefix1, maxCode1, start1, startDate, endDate, latestDealDay);
        });
        t1.start();

        //深圳主板
        final String prefix2 = "sz";
        final int maxCode2 = 100000;
        final int start2 = 1;
        Thread t2 = new Thread(()->{
            syncAll(prefix2, maxCode2, start2, startDate, endDate, latestDealDay);
        });
        t2.start();

        //深圳创业板
        final String prefix3 = "sz";
        final int maxCode3 = 399999;
        final int start3 = 300001;
        Thread t3 = new Thread(()->{
            syncAll(prefix3, maxCode3, start3, startDate, endDate, latestDealDay);
        });
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        result.setStatus(200);
        result.setMessage("同步成功");
        return result;
    }

    private void syncAll(String prefix, int maxCode, int start, Date startDate, Date endDate, Date latestDealDay) {
        int errorTime = 0;
        int maxErrorTime = 3000;
        for(int i=start; i<maxCode; i++) {
            if(errorTime >= maxErrorTime) {
                logger.info("error times:" + errorTime +", code index:" + i);
                break;
            }
            String code = prefix + String.format("%06d", i);
            StockDailyDealSyncLog log = stockDataSyncService.createOrUpdateData(code, startDate, endDate,latestDealDay);
            if(log==null || stockDataSyncService.FAILED.equalsIgnoreCase(log.getSynceStatus())) {
                errorTime++;
            } else {
                errorTime = 0;
            }
        }
    }
}
