package com.tyr.finance.stock.rangebreak;

import com.tyr.finance.stock.bo.SimpleStockDealDataBo;
import com.tyr.finance.stock.rangebreak.bo.RangeBreakResultBO;
import org.apache.commons.collections.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class RangeBreakExecutor {

    private List<SimpleStockDealDataBo> dealData;

    public RangeBreakExecutor(List<SimpleStockDealDataBo> dealData) {
        this.dealData = new ArrayList<>();
        for(SimpleStockDealDataBo bo : dealData) {
            this.dealData.add(bo);
        }
        Collections.sort(this.dealData);
    }

    public RangeBreakResultBO runNormalModel(Integer startIndex, Integer endIndex, float coefficient) {
        if(endIndex==null) {
            endIndex = dealData.size();
        }
        if(startIndex!=null&&endIndex!=null&&endIndex>startIndex && startIndex>=0 && startIndex<dealData.size()-1 && endIndex>0 && endIndex<=dealData.size()) {
            RangeBreakResultBO result = new RangeBreakResultBO();
            result.setStartIndex(startIndex);
            result.setEndIndex(endIndex);
            result.setStartPrice(dealData.get(startIndex).getTopen());
            result.setEndPrice(dealData.get(endIndex).getTclose());
            result.setRealPriceChange(result.getEndPrice() - result.getStartPrice());
            result.setCoefficient(coefficient);
            for(int i=startIndex; i<endIndex-1; i++) {
                //以i节点的波动幅度计算在下一结点的盈利
                SimpleStockDealDataBo deal = dealData.get(i);
                SimpleStockDealDataBo nextDeal = dealData.get(i+1);
                //上边界，也就是买入价
                float rangeTop = deal.getMinMaxPriceAmplitude() * coefficient + nextDeal.getTopen();
                //最高价未达到上边界，不买入，盈利为0
                if(nextDeal.getMaxPrice() < rangeTop) {
                    result.getProfitDetail().add(0f);
                    result.getProfitRatioDetail().add(0f);
                    continue;
                } else { //最高价达到上边界，买入，以下一结点的收盘价计算盈亏
                    //本次盈利值
                    float profit = nextDeal.getTclose() - rangeTop;
                    //本次盈利百分比
                    float profitRatio = profit / rangeTop * 100;
                    //更新总盈利值
                    result.setProfit(result.getProfit() + profit);
//                    //更新总盈利百分比
//                    result.setProfitRatio(result.getProfitRatio() + profitRatio);
                    //添加详细信息
                    result.getProfitDetail().add(profit);
                    result.getProfitRatioDetail().add(profitRatio);
                }
            }
            result.setProfitRatio(result.getProfit()/result.getStartPrice() * 100);
            return result;
        } else {
            throw new RuntimeException("错误的输入参数 startIndex:"+startIndex+", "+"endIndex:"+endIndex+", coefficient:"+ coefficient);
        }
    }

    /**
     *
     * @return
     */
    public RangeBreakResultBO runModelByYearRange(int year, float coefficient) {
        return runModelByYearRange(year, year, coefficient);
    }
    public RangeBreakResultBO runModelByYearRange(int startYear, int endYear, float coefficient) {
        Calendar c = GregorianCalendar.getInstance();
        Integer startIndex = null;
        Integer endIndex = null;
        for(int i=0; i<dealData.size(); i++) {
            SimpleStockDealDataBo bo = dealData.get(i);
            c.setTime(bo.getEndDate());
            int year = c.get(Calendar.YEAR);
            if(i==0&&startYear<=year) {
                startIndex = 0;
             }
            if(startYear==year && startIndex==null) {
                startIndex = i;
            }
            if(endYear>=year) {
                endIndex = i;
            }
        }
        if(startIndex!=null && endIndex!=null && startIndex<endIndex) {
            return runNormalModel(startIndex, endIndex, coefficient);
        }
        return null;
    }

    public RangeBreakResultBO runBestCoefficientModel(Integer startIndex, Integer endIndex, float gap) {
        RangeBreakResultBO bestResult = null;
        for(float coefficient=0.1f; coefficient<1f; coefficient+=gap) {
            RangeBreakResultBO test = runNormalModel(startIndex, endIndex, coefficient);
            if(bestResult==null || bestResult.getProfit()<test.getProfit()) {
                bestResult = test;
            }
        }
        return bestResult;
    }

    /**
     * 获取给定输入参数下的最佳系数变化趋势
     * 计算次数：(数据长度-unitCount)/trendGap + 1
     * 开始位置：(数据长度-1)-((计算次数-1)*trendGap+(unitCount-1))
     * @param unitCount 以多少个为一个整体进行计算
     * @param trendGap 第二次计算起点与开始起点的间隙
     * @return
     */
    public List<RangeBreakResultBO> getCoefficientTrendBySomeCycle(Integer unitCount, Integer trendGap) {
        List result = new ArrayList();
        if(trendGap==null) {
            trendGap = 1;
        }
        //计算次数
        Integer times = (dealData.size()-unitCount)/trendGap + 1;
        //开始位置
        Integer startIndex = dealData.size()-1-((times-1)*trendGap+(unitCount-1));
        if(unitCount == null || times<=0) {
            throw new RuntimeException("错误的输入参数！！！");
        }
        while (times>0) {
            times--;
            RangeBreakResultBO test = runBestCoefficientModel(startIndex, startIndex+unitCount-1, 0.01f);
            result.add(test);
            startIndex += trendGap;
        }
        return result;
    }

    /**
     * 一年为一个单位计算系数趋势
     * @param startYear
     * @param endYear
     * @return
     */
    public List<RangeBreakResultBO> getCoefficientTrendByYear(int startYear, int endYear) {
        List result = new ArrayList();
        Calendar c = GregorianCalendar.getInstance();
        for(int current = startYear; current<=endYear; current++) {
            Integer startIndex = null;
            Integer endIndex = null;
            for(int i=0; i<dealData.size(); i++) {
                SimpleStockDealDataBo bo = dealData.get(i);
                c.setTime(bo.getEndDate());
                if(current==c.get(Calendar.YEAR)) {
                    if(startIndex==null) {
                        startIndex = i;
                    }
                    endIndex = i;
                }
            }
            if(startIndex!=null && endIndex!=null && startIndex<endIndex) {
                RangeBreakResultBO test = runBestCoefficientModel(startIndex, endIndex, 0.01f);
                result.add(test);
            }
        }
        return result;
    }

    /**
     * 使用所有的数据以一年为一个单位计算系数趋势
     * @return
     */
    public List<RangeBreakResultBO> getCoefficientTrendByYear() {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(dealData.get(0).getEndDate());
        int startYear = c.get(Calendar.YEAR);
        c.setTime(dealData.get(dealData.size()-1).getEndDate());
        int endYear = c.get(Calendar.YEAR);
        return getCoefficientTrendByYear(startYear, endYear);
    }


    /**
     * 获取加权系数，离现在越近权重越大
     * @trend 趋势变化数据
     * @return
     */
    public float getWeightingCoefficient(List<RangeBreakResultBO> trend) {
        float totalValue = 0;
        Integer weighting = 0;
        if(CollectionUtils.isNotEmpty(trend)) {
            for(int i=0; i<trend.size(); i++) {
                totalValue += (trend.get(i).getCoefficient() * (i+1));
                weighting += i+1;
            }
        }
        return totalValue/weighting;
    }

    public Integer getMinYear() {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(dealData.get(0).getEndDate());
        return c.get(Calendar.YEAR);
    }
    public Integer getMaxYear() {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(dealData.get(dealData.size()-1).getEndDate());
        return c.get(Calendar.YEAR);
    }

    public String getDateStringByIndex(Integer index) {
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(dealData.get(index).getEndDate());
    }

    public SimpleStockDealDataBo getStockDealByIndex(Integer index) {
        return dealData.get(index);
    }

    public String stockName() {
        return dealData.get(dealData.size()-1).getStockName();
    }
}
