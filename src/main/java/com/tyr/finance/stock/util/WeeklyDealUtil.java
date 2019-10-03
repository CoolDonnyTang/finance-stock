package com.tyr.finance.stock.util;

import com.tyr.finance.stock.bo.SimpleStockDealDataBo;
import com.tyr.finance.stock.entity.StockDailyDeal;
import com.tyr.finance.stock.util.myenum.CalculateCycleEnum;
import com.tyr.finance.stock.util.myenum.WeekDayEnum;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class WeeklyDealUtil {


    public static List<SimpleStockDealDataBo> getWeeklyDeals(List<StockDailyDeal> dailyDeals, List<WeekDayEnum> exclude) {
        List<SimpleStockDealDataBo> result = new ArrayList<>();
        if(CollectionUtils.isEmpty(dailyDeals)) {
            return result;
        }
        Collections.sort(dailyDeals);

        Date currentCycleEnd = null;
        SimpleStockDealDataBo currentWeekData = null;
        for(StockDailyDeal daily : dailyDeals) {
            //检查是否开始了新的一周
            if(currentCycleEnd==null || currentCycleEnd.before(daily.getDateOfData())) {
                Calendar c = GregorianCalendar.getInstance();
                c.setTime(daily.getDateOfData());
                c.add(Calendar.DAY_OF_MONTH, 7-daily.getDayOfWeek());
                //开始了新的一周，更新新的一周的结束时间
                currentCycleEnd = c.getTime();
                //开始了新的一周则构建新的周数据对象，并初始化开盘价及开始日期
                currentWeekData = new SimpleStockDealDataBo();
                currentWeekData.setTopen(daily.getTopen());
                currentWeekData.setStartDate(daily.getDateOfData());
                result.add(currentWeekData);
            }
            //更新最高价
            if(currentWeekData.getMaxPrice()==null || currentWeekData.getMaxPrice()<daily.getMaxPrice()) {
                currentWeekData.setMaxPrice(daily.getMaxPrice());
            }
            //更新最低价
            if(currentWeekData.getMinPrice()==null || currentWeekData.getMinPrice()>daily.getMinPrice()) {
                currentWeekData.setMinPrice(daily.getMinPrice());
            }
            //更新最高价最低价振幅
            currentWeekData.setMinMaxPriceAmplitude(currentWeekData.getMaxPrice()-currentWeekData.getMinPrice());
            //更新收盘价
            currentWeekData.setTclose(daily.getTclose());
            //更新结束日期
            currentWeekData.setEndDate(daily.getDateOfData());
        }
        return result;
    }

}
