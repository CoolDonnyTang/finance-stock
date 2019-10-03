package com.tyr.finance.stock.repository;

import com.tyr.finance.stock.entity.StockDailyDeal;
import com.tyr.finance.stock.entity.StockDailyDealSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface StockDailyDealSyncLogRepository extends JpaRepository<StockDailyDealSyncLog,Integer> {

}
