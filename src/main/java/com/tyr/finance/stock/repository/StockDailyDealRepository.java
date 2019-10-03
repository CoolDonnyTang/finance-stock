package com.tyr.finance.stock.repository;

import com.tyr.finance.stock.entity.StockDailyDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface StockDailyDealRepository extends JpaRepository<StockDailyDeal,Integer> {

    @Query(value = "SELECT " +
            "  MAX(date_of_data) " +
            " FROM " +
            "  `stock_header` hd " +
            "  LEFT JOIN `stock_daily_deal` dd " +
            "    ON hd.`oid` = dd.`stock_id` " +
            "WHERE hd.`code` = :code ", nativeQuery = true)
    Date getLatestDateByCode(@Param("code") String code);
}
