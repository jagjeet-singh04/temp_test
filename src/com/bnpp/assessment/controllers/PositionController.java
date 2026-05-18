package com.bnpp.assessment.controllers;

import com.bnpp.assessment.dto.PositionRow;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.service.PositionService;
import com.bnpp.assessment.service.PositionServiceImpl;
import com.bnpp.assessment.service.TradeService;
import com.bnpp.assessment.service.TradeServiceImpl;
import com.bnpp.assessment.ui.panels.PositionPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PositionController {

    private PositionPanel panel;
    private User user;
    private TradeService tradeService = new TradeServiceImpl();
    private PositionService service = new PositionServiceImpl();
    private Timer timer;

    public PositionController(PositionPanel panel, User user) {
        this.panel = panel;
        this.user = user;
        loadPositions();
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        timer = new Timer(2000, e -> loadPositions());
        timer.start();
    }

   
    
    private void attachCloseListener() {
        panel.addPropertyChangeListener("closePosition", evt -> {
            int row = (int) evt.getNewValue();
            DefaultTableModel model = panel.getModel();
            if (row >= 0 && row < model.getRowCount()) {
                int confirm = JOptionPane.showConfirmDialog(panel,
                        "Close this position?",
                        "Confirm Close",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // We need the trade ID. Let's store it in the table model in a hidden way, or via the row list.
                    // Better: In loadPositions, keep a list of trades and use row index.
                    // We'll modify loadPositions to store the list of trades in a field.
                    if (currentRows != null && row < currentRows.size()) {
                        PositionRow posRow = currentRows.get(row);
                        boolean success = tradeService.closeTrade(posRow.getTrade().getId(), user);
                        if (success) {
                            JOptionPane.showMessageDialog(panel, "Position closed. P&L: ₹" + String.format("%.2f", posRow.getPnl()));
                            loadPositions();
                            // Notify wallet to refresh – we can fire another event or call a callback
                            // We'll fire a custom event that MainAppPanel catches to refresh wallet
                            panel.firePropertyChange("walletRefresh", false, true);
                        } else {
                            JOptionPane.showMessageDialog(panel, "Failed to close position.");
                        }
                    }
                }
            }
        });
    }

    private List<PositionRow> currentRows; // store current rows to know which trade to close

    private void loadPositions() {
        DefaultTableModel model = panel.getModel();
        currentRows = service.getPositions(user.getUserId());

        model.setRowCount(0);
        for (PositionRow row : currentRows) {
            model.addRow(new Object[]{
                row.getIndexSymbol(),
                row.getOptionType(),
                row.getStrike(),
                row.getTradeType(),
                row.getQuantity(),
                String.format("%.2f", row.getEntryPremium()),
                String.format("%.2f", row.getCurrentPremium()),
                String.format("%.2f", row.getPnl())
            });
        }
    }
}