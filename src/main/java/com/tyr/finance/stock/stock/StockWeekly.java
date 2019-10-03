package com.tyr.finance.stock.stock;

import com.tyr.finance.stock.bo.SimpleStockDealDataBo;
import com.tyr.finance.stock.entity.StockDailyDeal;
import org.apache.commons.collections.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class StockWeekly extends StockAdapter {

    public StockWeekly(List<StockDailyDeal> dailyDeals) {
        super(dailyDeals);
    }

    public StockWeekly(List<StockDailyDeal> dailyDeals, List<Object> excludeList) {
        super(dailyDeals, excludeList);
    }


    @Override
    public boolean isExcludeData(StockDailyDeal daily) {
        if(CollectionUtils.isEmpty(excludeLisst)) {
            return false;
        }
        if(excludeLisst.contains(daily.getDayOfWeek())) {
            return true;
        }
        return false;
    }

    @Override
    public Date getCycleEndDate(Date currentDate) {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(currentDate);
        int daOfWeek = c.get(Calendar.DAY_OF_WEEK)-1==0?7:c.get(Calendar.DAY_OF_WEEK)-1;
        c.add(Calendar.DAY_OF_MONTH, 7 - daOfWeek);
        return c.getTime();
    }
}
