package com.tyr.finance.stock.repository;

import com.tyr.finance.stock.entity.StockHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockHeaderRepository extends JpaRepository<StockHeader, Integer> {
    StockHeader findByCode(String code);
}
