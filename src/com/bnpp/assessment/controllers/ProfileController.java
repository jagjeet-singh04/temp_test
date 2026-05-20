package com.bnpp.assessment.controllers;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.bnpp.assessment.dao.UserDao;
import com.bnpp.assessment.dto.TradeRow;
import com.bnpp.assessment.dto.TransactionRow;
import com.bnpp.assessment.models.Trade;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.service.TradeService;
import com.bnpp.assessment.service.TradeServiceImpl;
import com.bnpp.assessment.service.TransactionService;
import com.bnpp.assessment.service.TransactionServiceImpl;
import com.bnpp.assessment.ui.chart.PnlChart;
import com.bnpp.assessment.ui.panels.ProfilePanel;

/**
 * Handles loading **both** trade history and transaction history,
 * plus the new "Performance" chart.
 */
public class ProfileController {

    private final ProfilePanel panel;
    private final User user;
    private final TradeService tradeService = new TradeServiceImpl();
    private final TransactionService txnService = new TransactionServiceImpl();

    /** The chart is kept as a field so we can keep adding points later. */
    private final PnlChart pnlChart;

    public ProfileController(ProfilePanel panel, User user) {
        this.panel = panel;
        this.user = user;
        this.pnlChart = panel.getPnlChart();

        loadUserInfo();               // name / eMail fields
        loadTradeHistory();          // default view = trades
        loadInitialPerformanceChart(); // build the chart from existing CLOSED trades

        attachSaveListener();
        attachToggleListeners();
        attachPerformanceButton();
        attachTradeCloseListener();
    }

    /* ----------------------------------------------------------------------
     * Existing helpers (unchanged)
     * ---------------------------------------------------------------------- */
    private void loadUserInfo() {
        panel.getNameField().setText(user.getUsername());
        panel.getEmailField().setText(user.getEmail());
    }

    private void loadTradeHistory() {
        List<TradeRow> rows = tradeService.getTradeHistory(user.getUserId())
                                          .stream()
                                          .map(TradeRow::new)
                                          .collect(Collectors.toList());
        panel.setTradeHistory(rows);
    }

    private void loadTransactionHistory() {
        List<TransactionRow> rows = txnService.getHistory(user.getUserId())
                                              .stream()
                                              .map(TransactionRow::new)
                                              .collect(Collectors.toList());
        panel.setTransactionHistory(rows);
    }

    private void attachToggleListeners() {
        panel.getBtnShowTrades().addActionListener(e -> loadTradeHistory());
        panel.getBtnShowTxns().addActionListener(e -> loadTransactionHistory());
    }

    private void attachSaveListener() {
        panel.getSaveButton().addActionListener(e -> {
            String name = panel.getNameField().getText().trim();
            String email = panel.getEmailField().getText().trim();
            String pwd = new String(panel.getPwdField().getPassword()).trim();
            String pwdRpt = new String(panel.getConfirmPwdField().getPassword()).trim();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(panel, 
                    "Name and E-mail cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!pwd.isEmpty() && !pwd.equals(pwdRpt)) {
                JOptionPane.showMessageDialog(panel, 
                    "Passwords do not match.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            user.setUsername(name);
            user.setEmail(email);
            if (!pwd.isEmpty()) {
                user.setPassword(pwd);
            }

            UserDao userDao = new UserDaoImpl();
            userDao.update(user);

            JOptionPane.showMessageDialog(panel, 
                "Profile saved.", "Success", JOptionPane.INFORMATION_MESSAGE);

            panel.clearPasswordFields();
            panel.setEditable(false);
        });

        panel.getEditButton().addActionListener(e -> panel.setEditable(true));

        panel.getCancelButton().addActionListener(e -> {
            loadUserInfo();
            panel.clearPasswordFields();
            panel.setEditable(false);
        });
    }

    /* ----------------------------------------------------------------------
     * NEW - build the chart from already closed trades
     * ---------------------------------------------------------------------- */
    private void loadInitialPerformanceChart() {
        List<Trade> closed = tradeService.getTradeHistory(user.getUserId())
                                         .stream()
                                         .filter(t -> "CLOSED".equalsIgnoreCase(t.getStatus()))
                                         .collect(Collectors.toList());

        pnlChart.setTrades(closed); // fills the series once
    }

    /* ----------------------------------------------------------------------
     * NEW - show the dialog when the button is pressed
     * ---------------------------------------------------------------------- */
    private void attachPerformanceButton() {
        panel.getBtnShowPerformance().addActionListener(e -> showPerformanceDialog());
    }

    private void showPerformanceDialog() {
        JDialog dlg = new JDialog(
            SwingUtilities.getWindowAncestor(panel),
            "Performance - Cumulative P&L",
            Dialog.ModalityType.APPLICATION_MODAL
        );

        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.add(pnlChart.getChartPanel(), BorderLayout.CENTER);
        dlg.pack();
        dlg.setLocationRelativeTo(panel);
        dlg.setVisible(true);
    }

    /* ----------------------------------------------------------------------
     * NEW - listen for *closed* trades and push them to the chart
     * ---------------------------------------------------------------------- */
    private void attachTradeCloseListener() {
        // If the TradeServiceImpl we created above implements the helper method
        if (tradeService instanceof TradeServiceImpl tradeServiceImpl) {
            tradeServiceImpl.addPropertyChangeListener("tradeClosed", evt -> {
                Trade closedTrade = (Trade) evt.getNewValue();
                SwingUtilities.invokeLater(() -> pnlChart.addTrade(closedTrade));
            });
        }
    }
}