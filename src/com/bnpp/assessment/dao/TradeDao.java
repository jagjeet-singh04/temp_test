package com.bnpp.assessment.dao;

import com.bnpp.assessment.models.Trade;
import java.util.List;

public interface TradeDao {
    void save(Trade trade);
    List<Trade> findOpenTradesByUserId(Long userId);
    List<Trade> findByUserId(Long userId);
    void update(Trade trade);
    Trade findById(Long id);
}