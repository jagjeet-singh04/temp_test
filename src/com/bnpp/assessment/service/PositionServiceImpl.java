package com.bnpp.assessment.service;

import com.bnpp.assessment.dao.OptionStrikeDao;
import com.bnpp.assessment.dao.OptionStrikeDaoImpl;
import com.bnpp.assessment.dao.TradeDao;
import com.bnpp.assessment.dao.TradeDaoImpl;
import com.bnpp.assessment.dto.PositionRow;
import com.bnpp.assessment.models.OptionStrike;
import com.bnpp.assessment.models.Trade;

import java.util.ArrayList;
import java.util.List;

public class PositionServiceImpl implements PositionService {

    private TradeDao tradeDao = new TradeDaoImpl();
    private OptionStrikeDao strikeDao = new OptionStrikeDaoImpl();

    @Override
    public List<PositionRow> getPositions(Long userId) {
    	List<Trade> trades = tradeDao.findOpenTradesByUserId(userId);
        List<PositionRow> rows = new ArrayList<>();

        for (Trade trade : trades) {
            OptionStrike strike = trade.getOptionStrike();
            if (strike == null) continue;

            // Refresh the strike from DB to get latest premium
            OptionStrike latest = strikeDao.findById(strike.getId());
            if (latest == null) continue;

            double currentPremium = trade.getOptionType().equalsIgnoreCase("CE") ?
                    latest.getCallPremium() : latest.getPutPremium();

            String indexSymbol = latest.getOptionChain().getIndex().getSymbol();

            rows.add(new PositionRow(trade, indexSymbol, currentPremium));
        }
        return rows;
    }
}