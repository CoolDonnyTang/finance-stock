package com.tyr.finance.stock;

import com.tyr.finance.stock.bo.SimpleStockDealDataBo;
import com.tyr.finance.stock.entity.StockDailyDeal;
import com.tyr.finance.stock.rangebreak.RangeBreakExecutor;
import com.tyr.finance.stock.rangebreak.bo.RangeBreakResultBO;
import com.tyr.finance.stock.stock.Stock;
import com.tyr.finance.stock.stock.StockWeekly;
import com.tyr.finance.stock.util.RemoteDataUtil;
import com.tyr.finance.stock.util.WeeklyDealUtil;
import com.tyr.finance.stock.util.myenum.WeekDayEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

public class SimpleTest {

    @Test
    public void testUrl() {
        try {
            URL url = new URL("http://quotes.money.163.com/service/chddata.html?code=0601857&start=20071105&end=20150618&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP");
            URLConnection URLconnection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.err.println("成功");
                InputStream in = httpConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(in, "GBK");
                BufferedReader bufr = new BufferedReader(isr);
                String str;
                while ((str = bufr.readLine()) != null) {
                    System.out.println(str);
                }
                bufr.close();
            } else {
                System.err.println("失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEnum() {
        System.out.println(WeekDayEnum.MON.getValue());
        System.out.println(WeekDayEnum.getDataByValue(5));
    }

    @Test
    public void testCalendar() {
        Calendar c = GregorianCalendar.getInstance();

        System.out.println(c.getFirstDayOfWeek());

    }

    @Test
    public void testWeeklyDeal() throws Exception {
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");

        Date start = sdf.parse("2019-3-2");
        Date end = sdf.parse("2019-3-31");

        List<StockDailyDeal> dailyDeals = RemoteDataUtil.getDailyData("sz000725",start, end);

        List<SimpleStockDealDataBo> result =  WeeklyDealUtil.getWeeklyDeals(dailyDeals, null);

        System.out.println(result);
    }

    @Test
    public void testStockWeekly() throws Exception {
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");

        Date start = sdf.parse("1990-3-2");
        Date end = sdf.parse("2019-5-16");

        List<StockDailyDeal> dailyDeals = RemoteDataUtil.getDailyData("sz000725",start, end);

        List<Object> excl = new ArrayList<>();
        excl.add(1);
        Stock s = new StockWeekly(dailyDeals, null);

        List<SimpleStockDealDataBo> result = s.getDeals();

        System.out.println(result);
    }

    @Test
    public void testRangeBreak() throws Exception {
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");

        //测试上升
//        String startStr = "2016-2-27";
//        String endStr = "2018-1-25";

        //测试下降
//        String startStr = "2018-2-27";
//        String endStr = "2019-2-2";

        //测试
        String startStr = "1991-1-1";
        String endStr = "1991-12-31";


        Date start = sdf.parse(startStr);
        Date end = sdf.parse(endStr);

        List<StockDailyDeal> dailyDeals = RemoteDataUtil.getDailyData("sh000001",start, end);

        List<Object> excl = new ArrayList<>();
        excl.add(1);
        Stock s = new StockWeekly(dailyDeals, null);

        List<SimpleStockDealDataBo> result = s.getDeals();

        System.out.println(result);

        RangeBreakExecutor executor = new RangeBreakExecutor(result);
        RangeBreakResultBO bo = executor.runNormalModel(0, null, 0.1f);
        RangeBreakResultBO bo1 = executor.runNormalModel(0, result.size(), 0.1f);

        RangeBreakResultBO bo2 = executor.runNormalModel(0, null, 0.2f);
        RangeBreakResultBO bo3 = executor.runNormalModel(0, null, 0.3f);
        RangeBreakResultBO bo4 = executor.runNormalModel(0, null, 0.4f);

        RangeBreakResultBO best = executor.runBestCoefficientModel(0, null,0.01f);

        List<RangeBreakResultBO> trend = executor.getCoefficientTrendBySomeCycle(48, 48);

        float c111 = executor.getWeightingCoefficient(trend);

        System.out.println("");
    }

    @Test
    public void testWeighting() throws Exception {
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");

        //测试上升
//        String startStr = "2016-2-27";
//        String endStr = "2018-1-25";

        //测试下降
//        String startStr = "2018-2-27";
//        String endStr = "2019-2-2";

        //测试下降
        String startStr = "2018-12-31";
        String endStr = "2029-12-31";


        Date start = sdf.parse(startStr);
        Date end = sdf.parse(endStr);
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(start);
        int year = c.get(Calendar.YEAR);
        c.setTime(end);
        int year1 = c.get(Calendar.YEAR);

        List<StockDailyDeal> dailyDeals = RemoteDataUtil.getDailyData("sz000002",start, end);
        Stock s = new StockWeekly(dailyDeals, null);

        List<SimpleStockDealDataBo> result = s.getDeals();

        System.out.println(result);

        RangeBreakExecutor executor = new RangeBreakExecutor(result);
        RangeBreakResultBO bo = executor.runNormalModel(0, null, 0.01f);
        RangeBreakResultBO bo1 = executor.runNormalModel(0, result.size(), 0.16f);

        System.out.println("");
    }

    @Test
    public void testYearlyTrend() throws Exception {
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
        String startStr = "1991-1-1";
        List<String> xishu = new ArrayList<>();
        List<RangeBreakResultBO> trends = null;
        for(int year = 1991; year<=2018; year++) {
            String endStr = year + "-12-31";
            Date start = sdf.parse(startStr);
            Date end = sdf.parse(endStr);
            List<StockDailyDeal> dailyDeals = RemoteDataUtil.getDailyData("sh000001",start, end);
            Stock s = new StockWeekly(dailyDeals, null);
            List<SimpleStockDealDataBo> result = s.getDeals();
            RangeBreakExecutor executor = new RangeBreakExecutor(result);
            List<RangeBreakResultBO> trend = executor.getCoefficientTrendByYear();
            float c111 = executor.getWeightingCoefficient(trend);

            xishu.add("year " + year +": " + c111);
            if(year==2018) {
                trends = trend;
            }
        }
        for (int i=0; i<xishu.size(); i++) {
            System.out.println(xishu.get(i));
        }
        System.out.println("=====================================");
        System.out.println(StringUtils.join(trends, "\n"));

    }

    @Test
    public void testYearlyTrend2() throws Exception {
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
        String startStr = "1990-1-1";
        String endStr = "2019-12-31";
        Date start = sdf.parse(startStr);
        Date end = sdf.parse(endStr);
        List<StockDailyDeal> dailyDeals = RemoteDataUtil.getDailyData("sz000725",start, end);
        Stock s = new StockWeekly(dailyDeals, null);
        List<SimpleStockDealDataBo> result = s.getDeals();
        RangeBreakExecutor executor = new RangeBreakExecutor(result);
        float total = 0;
        List<String> jisuan = new ArrayList<>();
        List<String> xishu = new ArrayList<>();
        List<RangeBreakResultBO> trends = null;
        int base = 1991;
        for(int year = 1991; year<=2018; year++) {
            List<RangeBreakResultBO> trend = executor.getCoefficientTrendByYear(base, year);
            if(CollectionUtils.isEmpty(trend)) {
                continue;
            }
            float c111 = executor.getWeightingCoefficient(trend);
            if(executor.runModelByYearRange(year+1, c111)==null) {
                continue;
            }
            //计算下一年
            total += (executor.runModelByYearRange(year+1, c111).getProfit()-executor.runModelByYearRange(year+1, c111).getRealPriceChange());
            jisuan.add("" + (year+1) + "(" + c111 + "):" + executor.runModelByYearRange(year+1, c111).getProfit() + "\n" + (executor.runModelByYearRange(year+1, c111).getProfit()-executor.runModelByYearRange(year+1, c111).getRealPriceChange()) + "\n" + executor.runModelByYearRange(year+1, c111));

            xishu.add("year " + year +": " + c111);
            if(year==2017) {
                trends = trend;
            }
        }
        for (int i=0; i<xishu.size(); i++) {
            System.out.println(xishu.get(i));
            System.out.println(jisuan.get(i));
            System.out.println("-------------");
        }
        System.out.println("=====================================" + total);
        System.out.println(StringUtils.join(trends, "\n"));

    }

    @Test
    public void testLatestDealDay() throws Exception {
        System.out.println();
    }

}
