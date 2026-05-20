//package com.bnpp.assessment.controllers;
//
//import com.bnpp.assessment.models.OptionStrike;
//import com.bnpp.assessment.models.User;
//import com.bnpp.assessment.service.TradeService;
//import com.bnpp.assessment.service.TradeServiceImpl;
//import com.bnpp.assessment.ui.panels.TradeOrderDialog;
//
//import javax.swing.*;
//
//public class TradeController {
//
//    private TradeOrderDialog panel;
//    private User user;
//    private OptionStrike strike;
//    private String optionType;
//    private TradeService tradeService = new TradeServiceImpl();
//
//    public TradeController(TradeOrderDialog panel, User user, OptionStrike strike, String optionType) {
//        this.panel = panel;
//        this.user = user;
//        this.strike = strike;
//        this.optionType = optionType;
//    }
//
//    public void executeTrade(String tradeType) {
//        int lots = panel.getLots();
//        if (lots <= 0) {
//            JOptionPane.showMessageDialog(panel, "Number of lots must be positive");
//            return;
//        }
//
//        int lotSize = panel.getLotSize();
//        int quantity = lots * lotSize;
//
//        boolean success = tradeService.executeTrade(user, strike, optionType, tradeType, quantity);
//        if (success) {
//            JOptionPane.showMessageDialog(panel, tradeType + " order executed successfully!");
//            panel.closeDialog();
//        } else {
//            JOptionPane.showMessageDialog(panel, tradeType + " failed. Check balance or margin.");
//        }
//    }
//}


package com.bnpp.assessment.controllers;

import javax.swing.JOptionPane;

import com.bnpp.assessment.models.OptionStrike;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.service.TradeService;
import com.bnpp.assessment.service.TradeServiceImpl;
import com.bnpp.assessment.ui.panels.TradeOrderDialog;

public class TradeController {

    private final TradeOrderDialog dialog;
    private final User user;
    private final OptionStrike strike;
    private final String optionType;
    private final TradeService tradeService = new TradeServiceImpl();

    public TradeController(TradeOrderDialog dialog, User user, OptionStrike strike, String optionType) {
        this.dialog = dialog;
        this.user = user;
        this.strike = strike;
        this.optionType = optionType;
    }

    /**
     * Executes a BUY or SELL order and closes the dialog on success.
     */
    public void executeTrade(String tradeType) {
        int lots = dialog.getLots();
        if (lots <= 0) {
            JOptionPane.showMessageDialog(
                dialog,
                "Number of lots must be positive.",
                "Invalid input",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int lotSize = dialog.getLotSize();
        int quantity = lots * lotSize;

        // Use the premium currently shown in the dialog to guarantee the executed
        // price matches what the user saw at the time they clicked BUY/SELL.
        double displayedPremium = dialog.getCurrentPremium();
        if (optionType.equalsIgnoreCase("CE")) {
            strike.setCallPremium(displayedPremium);
        } else {
            strike.setPutPremium(displayedPremium);
        }

        boolean success = tradeService.executeTrade(user, strike, optionType, tradeType, quantity);

        if (success) {
            JOptionPane.showMessageDialog(
                dialog,
                tradeType + " order executed successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
            dialog.closeDialog();
        } else {
            JOptionPane.showMessageDialog(
                dialog,
                tradeType + " failed. Check balance.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}