package com.bnpp.assessment.controllers;

import com.bnpp.assessment.models.User;
import com.bnpp.assessment.models.Wallet;
import com.bnpp.assessment.service.WalletService;
import com.bnpp.assessment.service.WalletServiceImpl;
import com.bnpp.assessment.ui.panels.WalletPanel;

import javax.swing.*;

public class WalletController {

    private WalletPanel panel;
    private User user;
    private final WalletService service = new WalletServiceImpl();

    public WalletController(WalletPanel panel, User user) {
        this.panel = panel;
        this.user = user;
        loadWallet();
        attachListeners();
    }

    public void loadWallet() {
        Wallet wallet = service.getWallet(user.getUserId());
        panel.getBalanceLabel().setText("Balance : ₹ " + String.format("%.2f", wallet.getCashBalance()));
        panel.getRealisedLabel().setText("Realized P&L : ₹ " + String.format("%.2f", wallet.getRealisedPnl()));
        panel.getUnrealisedLabel().setText("Unrealized P&L : ₹ " + String.format("%.2f", wallet.getUnrealisedPnl()));
    }

    private void attachListeners() {
        panel.getDepositButton().addActionListener(e -> handleDeposit());
        panel.getWithdrawButton().addActionListener(e -> handleWithdraw());
    }

    private void handleDeposit() {
        try {
            double amount = Double.parseDouble(panel.getAmountField().getText().trim());
            String password = new String(panel.getPasswordField().getPassword());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(panel, "Amount must be positive");
                return;
            }
            boolean success = service.deposit(user.getUserId(), amount, password);
            if (success) {
                JOptionPane.showMessageDialog(panel, "Deposit Successful");
                loadWallet();
                panel.getAmountField().setText("");
                panel.getPasswordField().setText("");
            } else {
                JOptionPane.showMessageDialog(panel, "Wrong Password");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "Invalid amount");
        }
    }

    private void handleWithdraw() {
        try {
            double amount = Double.parseDouble(panel.getAmountField().getText().trim());
            String password = new String(panel.getPasswordField().getPassword());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(panel, "Amount must be positive");
                return;
            }
            boolean success = service.withdraw(user.getUserId(), amount, password);
            if (success) {
                JOptionPane.showMessageDialog(panel, "Withdraw Successful");
                loadWallet();
                panel.getAmountField().setText("");
                panel.getPasswordField().setText("");
            } else {
                JOptionPane.showMessageDialog(panel, "Withdraw Failed – check password or balance");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "Invalid amount");
        }
    }
}