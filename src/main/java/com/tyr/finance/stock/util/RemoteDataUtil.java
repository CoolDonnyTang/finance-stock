package com.tyr.finance.stock.util;

import com.tyr.finance.stock.entity.StockDailyDeal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

public class RemoteDataUtil {
    private static final Logger logger = LoggerFactory.getLogger(RemoteDataUtil.class);

    public static String getStackCode4Remote(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("sh", "0");
        map.put("sz", "1");
        String type = code.substring(0,2).toLowerCase();
        String numberCode = code.substring(2);
        return map.get(type) + numberCode;
    }

    public static List<StockDailyDeal> getDailyData(String code, Date startDate, Date endDate) throws Exception {

        List<StockDailyDeal> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        logger.info("code:" + getStackCode4Remote(code));
        logger.info("startDate:" + sdf.format(startDate));
        logger.info("endDate:" + sdf.format(endDate));


        String field = "LCLOSE;TOPEN;TCLOSE;HIGH;LOW;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
        String dataSeparator = ",";
        StringBuilder sb = new StringBuilder("http://quotes.money.163.com/service/chddata.html")
                .append("?code=").append(getStackCode4Remote(code))
                .append("&start=").append(sdf.format(startDate))
                .append("&end=").append(sdf.format(endDate))
                .append("&fields=").append(field);
        logger.info("UEL:" + sb.toString());

        URL url = new URL(sb.toString());
        URLConnection URLconnection = url.openConnection();
        HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;
        int responseCode = httpConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream in = httpConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(in, "GBK");
            BufferedReader bufr = new BufferedReader(isr);
            String line;
            int fieldLenth = 0;
            le:while ((line = bufr.readLine()) != null) {
                logger.info("remote data:" + line);
                if(fieldLenth == 0) {
                    fieldLenth = line.split(dataSeparator).length;
                    continue;
                }
                String data[] = line.replaceAll("'", "").replace(","," , ").split(dataSeparator);
                if(fieldLenth != data.length) {
                    logger.error("读取远程数据时发生错误！！！");
                    throw new RuntimeException("读取远程数据时发生错误！！！");
                }
                for(int i=0; i<data.length; i++) {
                    if("None".equalsIgnoreCase(data[i].trim())) {
                        logger.warn("格式错误的数据：" + line);
                        continue le;
                    }
                    if(StringUtils.isEmpty(data[i].trim())) {
                        data[i] = "0";
                    }
                }
                StockDailyDeal daily = new StockDailyDeal();
                daily.setDateOfData(sdf2.parse(data[0].trim()));
                daily.setCurrentStockName(data[2].trim());
                daily.setLclose(Float.valueOf(data[3].trim()));
                daily.setTopen(Float.valueOf(data[4].trim()));
                daily.setTclose(Float.valueOf(data[5].trim()));
                daily.setMaxPrice(Float.valueOf(data[6].trim()));
                daily.setMinPrice(Float.valueOf(data[7].trim()));
                daily.setLtPriceChange(Float.valueOf(data[8].trim()));
                daily.setLtPriceChangeRatio(Float.valueOf(data[9].trim()));
                daily.setHandChangeRatio(Float.valueOf(data[10].trim()));
                daily.setTotalHand(Long.valueOf(data[11].trim()));
                daily.setTurnover(Double.valueOf(data[12].trim()));
                daily.setTotalMarketCap(Double.valueOf(data[13].trim()));
                daily.setNegotiableMarketCap(Double.valueOf(data[14].trim()));

                //设置 年、月、日、星期
                Calendar c = GregorianCalendar.getInstance();
                c.setTime(daily.getDateOfData());
                daily.setYear(c.get(Calendar.YEAR));
                daily.setMonth(c.get(Calendar.MONTH) + 1);
                daily.setDay(c.get(Calendar.DAY_OF_MONTH));
                daily.setDayOfWeek(c.get(Calendar.DAY_OF_WEEK)-1==0?7:c.get(Calendar.DAY_OF_WEEK)-1);
                //计算 今日最大振幅：max-min
                daily.setMinMaxPriceAmplitude(daily.getMaxPrice()-daily.getMinPrice());
                //计算 今日收盘价较开盘价的变化:t_close-t_open
                daily.setOpenClosePriceChange(daily.getTclose()-daily.getTopen());
                //计算 今日收盘价较开盘价的变化百分比:price_open_close_change/t_open*100
                daily.setOpenClosePriceChangeRatio(daily.getOpenClosePriceChange()/daily.getTopen()*100);
                if(daily.getOpenClosePriceChangeRatio().isNaN()) {
                   continue ;
                }
                result.add(daily);
            }
            bufr.close();
        }
        return result;
    }

    public static Date getLatestDealDay() throws Exception {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);

        List<StockDailyDeal> deals = getDailyData("sh000001", c.getTime(), new Date());
        Collections.sort(deals);

        return deals.get(deals.size()-1).getDateOfData();
    }
}
