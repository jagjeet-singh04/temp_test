package com.bnpp.assessment.service;

import com.bnpp.assessment.dao.OptionStrikeDao;
import com.bnpp.assessment.dao.OptionStrikeDaoImpl;
import com.bnpp.assessment.dao.TradeDao;
import com.bnpp.assessment.dao.TradeDaoImpl;
import com.bnpp.assessment.dao.WalletDao;
import com.bnpp.assessment.dao.WalletDaoImpl;
import com.bnpp.assessment.models.OptionStrike;
import com.bnpp.assessment.models.Trade;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.models.Wallet;

import java.util.Date;

public class TradeServiceImpl implements TradeService {

    private TradeDao tradeDao = new TradeDaoImpl();
    private WalletDao walletDao = new WalletDaoImpl();
    private WalletService walletService = new WalletServiceImpl();
    private OptionStrikeDao strikeDao = new OptionStrikeDaoImpl(); 
    
    @Override
    public boolean executeTrade(User user, OptionStrike strike, String optionType, String tradeType, int quantity) {
        if (!tradeType.equalsIgnoreCase("BUY") && !tradeType.equalsIgnoreCase("SELL")) {
            return false;
        }

        double premiumPerShare = optionType.equalsIgnoreCase("CE") ?
                strike.getCallPremium() : strike.getPutPremium();
        double totalPremium = premiumPerShare * quantity;

        Wallet wallet = walletService.getWallet(user.getUserId());

        if (tradeType.equalsIgnoreCase("BUY")) {
            // Buying options: need to pay total premium
            if (wallet.getCashBalance() < totalPremium) {
                return false;
            }
            wallet.setCashBalance(wallet.getCashBalance() - totalPremium);
        } else { // SELL
            // Selling options: require margin.
            // Simple margin rule: 30% of (strike price * quantity) + total premium
            double marginRequired = (0.3 * strike.getStrikePrice() * quantity) + totalPremium;

            if (wallet.getCashBalance() < marginRequired) {
                return false;
            }
            // Lock margin
            wallet.setCashBalance(wallet.getCashBalance() - marginRequired);
            wallet.setUsedMargin(wallet.getUsedMargin() + marginRequired);
        }

        // Save trade record
        Trade trade = new Trade();
        trade.setUser(user);
        trade.setOptionStrike(strike);
        trade.setOptionType(optionType.toUpperCase());
        trade.setTradeType(tradeType.toUpperCase());
        trade.setQuantity(quantity);
        trade.setPremium(premiumPerShare);
        trade.setTotalAmount(totalPremium);
        trade.setStatus("OPEN");
        trade.setTradeTime(new Date());

        tradeDao.save(trade);
        walletDao.update(wallet);

        return true;
    }
    
    @Override
    public boolean closeTrade(Long tradeId, User user) {
        Trade trade = tradeDao.findById(tradeId);   // need findById method
        if (trade == null || !trade.getStatus().equals("OPEN")) {
            return false;
        }

        // Get latest premium
        OptionStrike strike = trade.getOptionStrike();
        OptionStrike latest = strikeDao.findById(strike.getId());  // ensure fresh
        double currentPremium = trade.getOptionType().equalsIgnoreCase("CE") ?
                latest.getCallPremium() : latest.getPutPremium();

        double entryPremium = trade.getPremium();
        int quantity = trade.getQuantity();
        double pnl;

        if (trade.getTradeType().equalsIgnoreCase("BUY")) {
            pnl = (currentPremium - entryPremium) * quantity;
        } else { // SELL
            pnl = (entryPremium - currentPremium) * quantity;
        }

        // Update wallet
        Wallet wallet = walletService.getWallet(user.getUserId());

        if (trade.getTradeType().equalsIgnoreCase("BUY")) {
            // Release premium + P&L
            wallet.setCashBalance(wallet.getCashBalance() + trade.getTotalAmount() + pnl);
        } else { // SELL – release margin + P&L
            // Margin was: 0.3 * strikePrice * quantity + totalPremium
            double marginUsed = (0.3 * strike.getStrikePrice() * quantity) + trade.getTotalAmount();
            wallet.setCashBalance(wallet.getCashBalance() + marginUsed + pnl);
            wallet.setUsedMargin(wallet.getUsedMargin() - marginUsed);
        }

        trade.setStatus("CLOSED");
        tradeDao.update(trade);
        walletDao.update(wallet);
        return true;
    }
}