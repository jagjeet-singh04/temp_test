package com.bnpp.assessment.dto;

import com.bnpp.assessment.models.Trade;

public class PositionRow {
    private Trade trade;
    private String indexSymbol;
    private double currentPremium;
    private double pnl;

    // constructor, getters
    public PositionRow(Trade trade, String indexSymbol, double currentPremium) {
        this.trade = trade;
        this.indexSymbol = indexSymbol;
        this.currentPremium = currentPremium;
        // P&L: for buy: (current - entry) * qty, for sell: (entry - current) * qty
        double entry = trade.getPremium();
        int qty = trade.getQuantity();
        if (trade.getTradeType().equalsIgnoreCase("BUY")) {
            this.pnl = (currentPremium - entry) * qty;
        } else { // SELL
            this.pnl = (entry - currentPremium) * qty;
        }
    }

    public Trade getTrade() { return trade; }
    public String getIndexSymbol() { return indexSymbol; }
    public double getCurrentPremium() { return currentPremium; }
    public double getPnl() { return pnl; }
    public String getOptionType() { return trade.getOptionType(); }
    public double getStrike() { return trade.getOptionStrike().getStrikePrice(); }
    public String getTradeType() { return trade.getTradeType(); }
    public int getQuantity() { return trade.getQuantity(); }
    public double getEntryPremium() { return trade.getPremium(); }
}