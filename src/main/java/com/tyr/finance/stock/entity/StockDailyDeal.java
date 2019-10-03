package com.tyr.finance.stock.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@Table(name="stock_daily_deal")
public class StockDailyDeal implements Serializable, Comparable<StockDailyDeal> {
    private static final long serialVersionUID = -5682997117482811376L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oid;
    @Column(name="stock_id")
    private Integer stockId;
    @Column(name="current_stock_name")
    private String currentStockName;
    @Column(name="date_of_data")
    private Date dateOfData;
    @Column(name="year")
    private Integer year;
    @Column(name="month")
    private Integer month;
    @Column(name="day")
    private Integer day;
    @Column(name="day_of_week")
    private Integer dayOfWeek;
    @Column(name="l_close")
    private Float lclose;
    @Column(name="t_open")
    private Float topen;
    @Column(name="t_close")
    private Float tclose;
    @Column(name="max_price")
    private Float maxPrice;
    @Column(name="min_price")
    private Float minPrice;
    @Column(name="min_max_price_amplitude")
    private Float minMaxPriceAmplitude;
    @Column(name="lt_price_change")
    private Float ltPriceChange;
    @Column(name="lt_price_change_ratio")
    private Float ltPriceChangeRatio;
    @Column(name="open_close_price_change")
    private Float openClosePriceChange;
    @Column(name="open_close_price_change_ratio")
    private Float openClosePriceChangeRatio;
    @Column(name="total_hand")
    private Long totalHand;
    @Column(name="hand_change_ratio")
    private Float handChangeRatio;
    @Column(name="turnover")
    private Double turnover;
    @Column(name="total_market_cap")
    private Double totalMarketCap;
    @Column(name="negotiable_market_cap")
    private Double negotiableMarketCap;
    @Column(name="entry_datetime")
    private Timestamp entryDatetime;

    @Override
    public int compareTo(StockDailyDeal o2) {
        if(this.getDateOfData().getTime() - o2.getDateOfData().getTime() > 0){
            return 1;
        } else if(this.getDateOfData().getTime() - o2.getDateOfData().getTime() == 0) {
            return 0;
        }
        return -1;
    }
}
