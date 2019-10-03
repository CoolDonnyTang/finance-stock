package com.tyr.finance.stock.service.rangebreak.impl;

import com.tyr.finance.stock.bo.SimpleStockDealDataBo;
import com.tyr.finance.stock.entity.StockDailyDeal;
import com.tyr.finance.stock.rangebreak.RangeBreakExecutor;
import com.tyr.finance.stock.rangebreak.bo.RangeBreakResultBO;
import com.tyr.finance.stock.service.rangebreak.RangeBreakService;
import com.tyr.finance.stock.stock.Stock;
import com.tyr.finance.stock.stock.StockWeekly;
import com.tyr.finance.stock.util.DateUtil;
import com.tyr.finance.stock.util.RemoteDataUtil;
import com.tyr.finance.stock.vo.rangebreak.CalculateParamVo;
import com.tyr.finance.stock.vo.rangebreak.CalculateResultVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RangeBreakServiceImpl implements RangeBreakService {
    @Override
    public List<CalculateResultVo> calculateByWeeklyDeals(CalculateParamVo param) throws Exception {
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
        String startStr = "1990-1-1";
        String endStr = param.getEndYear() + "-12-31";
        Date start = sdf.parse(startStr);
        Date end = sdf.parse(endStr);
        List<StockDailyDeal> dailyDeals = RemoteDataUtil.getDailyData(param.getCode(),start, end);
        Stock s = new StockWeekly(dailyDeals, null);
        List<SimpleStockDealDataBo> dealData = s.getDeals();
        RangeBreakExecutor executor = new RangeBreakExecutor(dealData);

        if(param.getStartYear()<=executor.getMinYear()) {
            throw new RuntimeException("开始年份必须大于" + executor.getMinYear());
        }
        if(param.getEndYear()>executor.getMaxYear()) {
            throw new RuntimeException("结束年份小于等于" + executor.getMaxYear());
        }
        List<CalculateResultVo> result = new ArrayList<>();

        RangeBreakResultBO bo0 = executor.runModelByYearRange(param.getStartYear()-1, 0.1f);
        CalculateResultVo resultUnit0 = new CalculateResultVo();
        resultUnit0.setStockName(executor.stockName());
        resultUnit0.setStartDate(executor.getDateStringByIndex(0));
        resultUnit0.setEndDate(executor.getDateStringByIndex(bo0.getEndIndex()));
        resultUnit0.setUseCoefficient(0f);
        resultUnit0.setCoefficientStarDate("-");
        resultUnit0.setCoefficientEndDate("-");
        resultUnit0.setProfit(executor.getStockDealByIndex(bo0.getEndIndex()+1).getTopen());
        resultUnit0.setRealPriceChange(executor.getStockDealByIndex(bo0.getEndIndex()+1).getTopen());
        result.add(resultUnit0);

        for(int calculateYear = param.getStartYear(); calculateYear<=param.getEndYear(); calculateYear++) {
            List<RangeBreakResultBO> trend = executor.getCoefficientTrendByYear(calculateYear-param.getWeightingNum()-1, calculateYear-1);
            if(CollectionUtils.isEmpty(trend)) {
                continue;
            }
            float coefficient = executor.getWeightingCoefficient(trend);
            RangeBreakResultBO bo = executor.runModelByYearRange(calculateYear, coefficient);
            CalculateResultVo resultUnit = new CalculateResultVo();
            resultUnit.setStockName(executor.stockName());
            resultUnit.setStartDate(executor.getDateStringByIndex(bo.getStartIndex()));
            resultUnit.setEndDate(executor.getDateStringByIndex(bo.getEndIndex()));
            resultUnit.setUseCoefficient(coefficient);
            resultUnit.setCoefficientStarDate(executor.getDateStringByIndex(trend.get(0).getStartIndex()));
            resultUnit.setCoefficientEndDate(executor.getDateStringByIndex(trend.get(trend.size()-1).getEndIndex()));
            resultUnit.setProfit(bo.getProfit());
            resultUnit.setRealPriceChange(bo.getRealPriceChange());
            result.add(resultUnit);
        }
        return result;
    }
}
