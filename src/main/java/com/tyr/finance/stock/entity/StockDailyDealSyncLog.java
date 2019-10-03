package com.tyr.finance.stock.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@Table(name="stock_daily_deal_sync_log")
public class StockDailyDealSyncLog implements Serializable {
    private static final long serialVersionUID = 4143141361437370754L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oid;
    @Column(name="stock_code")
    private String stockCode;
    @Column(name="current_latest_date")
    private Date currentLatestDate;
    @Column(name="sync_start_date")
    private Date syncStartDate;
    @Column(name="sync_end_date")
    private Date syncEndDate;
    @Column(name="synce_status")
    private String synceStatus;
    @Column(name="sync_desc")
    private String syncDesc;
    @Column(name="entry_datetime")
    private Timestamp entryDatetime;
}
