package com.tyr.finance.stock.stock;

import com.tyr.finance.stock.bo.SimpleStockDealDataBo;
import com.tyr.finance.stock.entity.StockDailyDeal;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public abstract class StockAdapter implements Stock {

    protected List<StockDailyDeal> dailyDeals;
    protected List<Object> excludeLisst;

    public StockAdapter(List<StockDailyDeal> dailyDeals) {
        this.dailyDeals = dailyDeals;
    }

    public StockAdapter(List<StockDailyDeal> dailyDeals, List<Object> excludeLisst) {
        this.dailyDeals = dailyDeals;
        this.excludeLisst = excludeLisst;
    }

    @Override
    public List<SimpleStockDealDataBo> getDeals() {
        List<SimpleStockDealDataBo> result = new ArrayList<>();
        if(CollectionUtils.isEmpty(dailyDeals)) {
            return result;
        }
        Collections.sort(dailyDeals);

        Date currentCycleEnd = null;
        SimpleStockDealDataBo currentCycleWeekData = null;
        for(StockDailyDeal daily : dailyDeals) {
            if(isExcludeData(daily)) {
                continue;
            }
            //检查是否开始了新的一周
            if(currentCycleEnd==null || currentCycleEnd.before(daily.getDateOfData())) {
                //开始了新的一周，更新新的一周的结束时间
                currentCycleEnd = getCycleEndDate(daily.getDateOfData());
                //开始了新的一周则构建新的周数据对象，并初始化开盘价及开始日期
                currentCycleWeekData = new SimpleStockDealDataBo();
                currentCycleWeekData.setStockName(daily.getCurrentStockName());
                currentCycleWeekData.setTopen(daily.getTopen());
                currentCycleWeekData.setStartDate(daily.getDateOfData());
                result.add(currentCycleWeekData);
            }
            //更新最高价
            if(currentCycleWeekData.getMaxPrice()==null || currentCycleWeekData.getMaxPrice()<daily.getMaxPrice()) {
                currentCycleWeekData.setMaxPrice(daily.getMaxPrice());
            }
            //更新最低价
            if(currentCycleWeekData.getMinPrice()==null || currentCycleWeekData.getMinPrice()>daily.getMinPrice()) {
                currentCycleWeekData.setMinPrice(daily.getMinPrice());
            }
            //更新最高价最低价振幅
            currentCycleWeekData.setMinMaxPriceAmplitude(currentCycleWeekData.getMaxPrice()-currentCycleWeekData.getMinPrice());
            //更新收盘价
            currentCycleWeekData.setTclose(daily.getTclose());
            //更新结束日期
            currentCycleWeekData.setEndDate(daily.getDateOfData());
        }
        return result;
    }

    public abstract boolean isExcludeData(StockDailyDeal daily);

    public List<StockDailyDeal> getDailyDeals() {
        return dailyDeals;
    }

    public List<Object> getExcludeLisst() {
        return excludeLisst;
    }

    public void setExcludeLisst(List<Object> excludeLisst) {
        this.excludeLisst = excludeLisst;
    }
}
