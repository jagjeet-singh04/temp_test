package com.bnpp.assessment.controllers;

import com.bnpp.assessment.models.OptionStrike;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.service.TradeService;
import com.bnpp.assessment.service.TradeServiceImpl;
import com.bnpp.assessment.ui.panels.TradeOrderDialog;

import javax.swing.*;

public class TradeController {

    private TradeOrderDialog panel;
    private User user;
    private OptionStrike strike;
    private String optionType;
    private TradeService tradeService = new TradeServiceImpl();

    public TradeController(TradeOrderDialog panel, User user, OptionStrike strike, String optionType) {
        this.panel = panel;
        this.user = user;
        this.strike = strike;
        this.optionType = optionType;
    }

    public void executeTrade(String tradeType) {
        int lots = panel.getLots();
        if (lots <= 0) {
            JOptionPane.showMessageDialog(panel, "Number of lots must be positive");
            return;
        }

        int lotSize = panel.getLotSize();
        int quantity = lots * lotSize;

        boolean success = tradeService.executeTrade(user, strike, optionType, tradeType, quantity);
        if (success) {
            JOptionPane.showMessageDialog(panel, tradeType + " order executed successfully!");
            panel.closeDialog();
        } else {
            JOptionPane.showMessageDialog(panel, tradeType + " failed. Check balance or margin.");
        }
    }
}