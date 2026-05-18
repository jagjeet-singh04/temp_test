package com.bnpp.assessment.ui.panels;

import javax.swing.*;
import java.awt.*;

public class WalletPanel extends JPanel {

    private JLabel balanceLabel;

    private JLabel realisedLabel;

    private JLabel unrealisedLabel;

    private JTextField amountField;

    private JPasswordField passwordField;

    private JButton depositButton;

    private JButton withdrawButton;

    public WalletPanel() {

    	setLayout(new GridLayout(9, 1, 10, 10)); 
        balanceLabel =
                new JLabel(
                        "Balance : ₹0"
                );

        realisedLabel =
                new JLabel(
                        "Realized P&L : ₹0"
                );

        unrealisedLabel =
                new JLabel(
                        "Unrealized P&L : ₹0"
                );

        amountField =
                new JTextField();

        passwordField =
                new JPasswordField();

        depositButton =
                new JButton("Deposit");

        withdrawButton =
                new JButton("Withdraw");

        add(balanceLabel);

        add(realisedLabel);

        add(unrealisedLabel);

        add(new JLabel("Amount"));

        add(amountField);

        add(new JLabel("Password"));

        add(passwordField);

        add(depositButton);

        add(withdrawButton);
    }

    // =========================
    // GETTERS
    // =========================

    public JLabel getBalanceLabel() {
        return balanceLabel;
    }

    public JLabel getRealisedLabel() {
        return realisedLabel;
    }

    public JLabel getUnrealisedLabel() {
        return unrealisedLabel;
    }

    public JTextField getAmountField() {
        return amountField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getDepositButton() {
        return depositButton;
    }

    public JButton getWithdrawButton() {
        return withdrawButton;
    }
}