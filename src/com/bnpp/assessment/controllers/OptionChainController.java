//package com.bnpp.assessment.controllers;
//
//import com.bnpp.assessment.dao.OptionStrikeDao;
//import com.bnpp.assessment.dao.OptionStrikeDaoImpl;
//import com.bnpp.assessment.models.Index;
//import com.bnpp.assessment.models.OptionStrike;
//import com.bnpp.assessment.models.User;
//import com.bnpp.assessment.ui.panels.OptionChainPanel;
//import com.bnpp.assessment.ui.panels.TradeOrderDialog;  // renamed dialog
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.util.List;
//
//public class OptionChainController {
//
//    private OptionChainPanel panel;
//    private Index index;
//    private User user;
//    private OptionStrikeDao dao = new OptionStrikeDaoImpl();
//    private Timer timer;
//
//    public OptionChainController(OptionChainPanel panel, Index index, User user) {
//        this.panel = panel;
//        this.index = index;
//        this.user = user;
//        startAutoRefresh();
//        attachTradeListener();
//    }
//
//    private void startAutoRefresh() {
//        if (timer != null) {
//            timer.stop();
//        }
//        timer = new Timer(2000, e -> loadOptionChain());
//        timer.start();
//    }
//
//    public void stopAutoRefresh() {
//        if (timer != null) {
//            timer.stop();
//        }
//    }
//
//    // Restore original columns: "CE PRICE", "STRIKE", "PE PRICE"
//    public void loadOptionChain() {
//        panel.getSelectedIndexLabel().setText(index.getSymbol() + " OPTION CHAIN");
//
//        DefaultTableModel model = panel.getModel();
//        List<OptionStrike> strikes = dao.findByIndexId(index.getId());
//
//        if (model.getRowCount() != strikes.size()) {
//            model.setRowCount(0);
//            for (OptionStrike strike : strikes) {
//                model.addRow(new Object[]{
//                        strike.getCallPremium(),
//                        strike.getStrikePrice(),
//                        strike.getPutPremium()
//                });
//            }
//        } else {
//            for (int i = 0; i < strikes.size(); i++) {
//                OptionStrike strike = strikes.get(i);
//                model.setValueAt(strike.getCallPremium(), i, 0);
//                model.setValueAt(strike.getStrikePrice(), i, 1);
//                model.setValueAt(strike.getPutPremium(), i, 2);
//            }
//        }
//    }
//
//    private void attachTradeListener() {
//        panel.getOptionTable().addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (e.getClickCount() == 2) {
//                    int row = panel.getOptionTable().getSelectedRow();
//                    if (row == -1) return;
//
//                    List<OptionStrike> strikes = dao.findByIndexId(index.getId());
//                    OptionStrike selectedStrike = strikes.get(row);
//
//                    // Choose CE or PE
//                    String[] options = {"CE", "PE"};
//                    int choice = JOptionPane.showOptionDialog(
//                            panel,
//                            "Select option type to trade:",
//                            "Trade Option",
//                            JOptionPane.DEFAULT_OPTION,
//                            JOptionPane.QUESTION_MESSAGE,
//                            null,
//                            options,
//                            options[0]);
//
//                    if (choice >= 0) {
//                        String optionType = options[choice];
//                        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
//                        TradeOrderDialog dialog = new TradeOrderDialog(topFrame, user, selectedStrike, optionType);
//                        dialog.setVisible(true);
//                    }
//                }
//            }
//        });
//    }
//}



package com.bnpp.assessment.controllers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.bnpp.assessment.dao.OptionStrikeDao;
import com.bnpp.assessment.dao.OptionStrikeDaoImpl;
import com.bnpp.assessment.models.Index;
import com.bnpp.assessment.models.OptionStrike;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.ui.panels.OptionChainPanel;
import com.bnpp.assessment.ui.panels.TradeOrderDialog;
import com.bnpp.assessment.util.RefreshScheduler;

public class OptionChainController {

    private final OptionChainPanel panel;
    private final Index index;
    private final User user;
    private final OptionStrikeDao dao = new OptionStrikeDaoImpl();
    private final Runnable refreshTask = this::loadOptionChain;
    private final MouseListener tradeListener;

    public OptionChainController(OptionChainPanel panel, Index index, User user) {
        this.panel = panel;
        this.index = index;
        this.user = user; // <-- correct field

        this.tradeListener = createTradeListener();

        // Register the refresh method with the global scheduler
        RefreshScheduler.getInstance().register(refreshTask);
        panel.getOptionTable().addMouseListener(tradeListener);
    }

    /**
     * Load / refresh the option chain table - called by the scheduler.
     */
    public void loadOptionChain() {
        panel.getSelectedIndexLabel().setText(index.getSymbol() + " OPTION CHAIN");

        DefaultTableModel model = panel.getModel();
        List<OptionStrike> strikes = dao.findByIndexId(index.getId());

        if (model.getRowCount() != strikes.size()) {
            model.setRowCount(0);
            for (OptionStrike s : strikes) {
                model.addRow(new Object[]{
                    s.getCallPremium(),
                    s.getStrikePrice(),
                    s.getPutPremium()
                });
            }
        } else {
            for (int i = 0; i < strikes.size(); i++) {
                OptionStrike s = strikes.get(i);
                model.setValueAt(s.getCallPremium(), i, 0);
                model.setValueAt(s.getStrikePrice(), i, 1);
                model.setValueAt(s.getPutPremium(), i, 2);
            }
        }
    }

    /**
     * Stops the periodic refresh when the controller is no longer needed.
     */
    public void stopAutoRefresh() {
        RefreshScheduler.getInstance().deregister(refreshTask);
        panel.getOptionTable().removeMouseListener(tradeListener);
    }

    /**
     * Double click -> CE/PE chooser -> show the trade dialog.
     */
    private MouseListener createTradeListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = panel.getOptionTable().getSelectedRow();
                    if (row == -1) return;

                    List<OptionStrike> strikes = dao.findByIndexId(index.getId());
                    OptionStrike selectedStrike = strikes.get(row);

                    String[] opts = {"CE", "PE"};
                    int choice = JOptionPane.showOptionDialog(
                        panel,
                        "Select option type to trade:",
                        "Trade Option",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opts,
                        opts[0]
                    );

                    if (choice >= 0) {
                        JFrame topFrame = (JFrame) javax.swing.SwingUtilities.getWindowAncestor(panel);
                        TradeOrderDialog dialog = new TradeOrderDialog(topFrame, user, selectedStrike, opts[choice]);
                        dialog.setVisible(true);
                    }
                }
            }
        };
    }
}