package com.bnpp.assessment.ui.panels;

import com.bnpp.assessment.controllers.TradeController;
import com.bnpp.assessment.dao.OptionStrikeDao;
import com.bnpp.assessment.dao.OptionStrikeDaoImpl;
import com.bnpp.assessment.models.OptionStrike;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.util.RefreshScheduler;

import javax.swing.*;
import java.awt.*;

public class TradeOrderDialog extends JDialog {

    private JLabel strikeLabel, optionTypeLabel, premiumLabel, lotSizeLabel, totalLabel;
    private JTextField lotsField;          // now represents "Number of Lots"
    private JButton buyButton, sellButton, cancelButton;
    private OptionStrike strike;
    private User user;
    private String optionType;
    private TradeController controller;
    private final OptionStrikeDao strikeDao = new OptionStrikeDaoImpl();
    private final Runnable refreshTask = this::refreshFromLatestStrike;
    private boolean cleanedUp;

    // lot size mapping based on the underlying index symbol
    private static final int DEFAULT_LOT = 1;

    public TradeOrderDialog(JFrame parent, User user, OptionStrike strike, String optionType) {
        super(parent, "Trade " + optionType, true);
        this.user = user;
        this.strike = strike;
        this.optionType = optionType;
        this.controller = new TradeController(this, user, strike, optionType);
        initUI();
        RefreshScheduler.getInstance().register(refreshTask);
        refreshFromLatestStrike();
        pack();
        setLocationRelativeTo(parent);
    }

    public int getLotSize() {
        String symbol = strike.getOptionChain().getIndex().getSymbol().toUpperCase();
        switch (symbol) {
            case "NIFTY50": return 75;
            case "BANKNIFTY": return 25;
            case "SENSEX": return 10;
            default: return DEFAULT_LOT;
        }
    }

    private void initUI() {
        setLayout(new GridLayout(7, 1, 10, 10));

        int lotSize = getLotSize();
        double premiumPerShare = getCurrentPremium();

        strikeLabel = new JLabel("Strike: " + strike.getStrikePrice());
        optionTypeLabel = new JLabel("Option: " + optionType);
        premiumLabel = new JLabel("Premium/Share: ₹ " + String.format("%.2f", premiumPerShare));
        lotSizeLabel = new JLabel("Lot Size: " + lotSize);
        totalLabel = new JLabel("Total: ₹ 0");

        lotsField = new JTextField("1");   // default 1 lot
        lotsField.addCaretListener(e -> updateTotal());

        buyButton = new JButton("BUY");
        sellButton = new JButton("SELL");
        cancelButton = new JButton("Cancel");

        add(strikeLabel);
        add(optionTypeLabel);
        add(premiumLabel);
        add(lotSizeLabel);
        add(new JLabel("Lots:"));
        add(lotsField);
        add(totalLabel);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(buyButton);
        btnPanel.add(sellButton);
        btnPanel.add(cancelButton);
        add(btnPanel);

        buyButton.addActionListener(e -> controller.executeTrade("BUY"));
        sellButton.addActionListener(e -> controller.executeTrade("SELL"));
        cancelButton.addActionListener(e -> dispose());
    }

    public double getCurrentPremium() {
        return optionType.equals("CE") ? strike.getCallPremium() : strike.getPutPremium();
    }

    private void refreshFromLatestStrike() {
        OptionStrike latest = strikeDao.findById(strike.getId());
        if (latest != null) {
            strike = latest;
        }

        if (premiumLabel != null) {
            premiumLabel.setText("Premium/Share: ₹ " + String.format("%.2f", getCurrentPremium()));
        }

        if (lotSizeLabel != null) {
            lotSizeLabel.setText("Lot Size: " + getLotSize());
        }

        if (strikeLabel != null) {
            strikeLabel.setText("Strike: " + String.format("%.2f", strike.getStrikePrice()));
        }

        updateTotal();
    }

    private void updateTotal() {
        try {
            int lots = Integer.parseInt(lotsField.getText().trim());
            if (lots <= 0) {
                totalLabel.setText("Total: ₹ 0.00");
                return;
            }
            int lotSize = getLotSize();
            double total = lots * lotSize * getCurrentPremium();
            totalLabel.setText("Total: ₹ " + String.format("%.2f", total));
        } catch (NumberFormatException ex) {
            totalLabel.setText("Total: ₹ 0.00");
        }
    }

    /** Returns the number of lots (not the total quantity) */
    public int getLots() {
        try { return Integer.parseInt(lotsField.getText().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

   

    public void closeDialog() {
        cleanup();
        dispose();
    }

    @Override
    public void dispose() {
        cleanup();
        super.dispose();
    }

    private void cleanup() {
        if (cleanedUp) {
            return;
        }

        cleanedUp = true;
        RefreshScheduler.getInstance().deregister(refreshTask);
    }
}