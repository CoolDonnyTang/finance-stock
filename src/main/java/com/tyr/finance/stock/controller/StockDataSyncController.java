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
        stockDataSyncService.createOrUpdateData(code, startDate, endDate);
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

        String prefix = "";
        int maxCode = 0;
        int start = 0;

        //上海
        prefix = "sh";
        maxCode = 699999;
        start = 600001;
        syncAll(prefix, maxCode, start, startDate, endDate);

        //深圳主板
        prefix = "sz";
        maxCode = 100000;
        start = 1;
        syncAll(prefix, maxCode, start, startDate, endDate);

        //深圳创业板
        prefix = "sz";
        maxCode = 399999;
        start = 300001;
        syncAll(prefix, maxCode, start, startDate, endDate);

        result.setStatus(200);
        result.setMessage("同步成功");
        return result;
    }

    private void syncAll(String prefix, int maxCode, int start, Date startDate, Date endDate) {
        int errorTime = 0;
        int maxErrorTime = 3000;
        for(int i=start; i<maxCode; i++) {
            if(errorTime >= maxErrorTime) {
                logger.info("error times:" + errorTime +", code index:" + i);
                break;
            }
            String code = prefix + String.format("%06d", i);
            StockDailyDealSyncLog log = stockDataSyncService.createOrUpdateData(code, startDate, endDate);
            if(log==null || stockDataSyncService.FAILED.equalsIgnoreCase(log.getSynceStatus())) {
                errorTime++;
            } else {
                errorTime = 0;
            }
        }
    }
}
