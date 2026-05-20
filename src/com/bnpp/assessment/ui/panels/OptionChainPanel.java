//package com.bnpp.assessment.ui.panels;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//
//public class OptionChainPanel extends JPanel {
//
//    private JTable optionTable;
//
//    private DefaultTableModel model;
//
//    private JLabel selectedIndexLabel;
//
//    public OptionChainPanel() {
//
//        setLayout(new BorderLayout());
//
//        selectedIndexLabel =
//                new JLabel("OPTION CHAIN");
//
//        add(selectedIndexLabel,
//                BorderLayout.NORTH);
//
//        String[] columns = {
//                "CE PRICE",
//                "STRIKE",
//                "PE PRICE"
//        };
//
//        model = new DefaultTableModel(columns, 0);
//
//        optionTable = new JTable(model);
//
//        optionTable.setDefaultEditor(Object.class, null); 
//        JScrollPane scrollPane =
//                new JScrollPane(optionTable);
//
//        add(scrollPane, BorderLayout.CENTER);
//    }
//
//    public JTable getOptionTable() {
//        return optionTable;
//    }
//
//    public DefaultTableModel getModel() {
//        return model;
//    }
//
//    public JLabel getSelectedIndexLabel() {
//        return selectedIndexLabel;
//    }
//}


package com.bnpp.assessment.ui.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.bnpp.assessment.models.Index;
import com.bnpp.assessment.models.OptionStrike;

/**
 * Panel that shows the CE/PE chain for a single index.
 */
public class OptionChainPanel extends JPanel {

    private final JTable optionTable;
    private final DefaultTableModel model;
    private final JLabel selectedIndexLabel;
    private final JButton backButton;

    private Index index;

    public OptionChainPanel() {
        setLayout(new BorderLayout());
        backButton = new JButton("Back");

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(backButton);

        selectedIndexLabel = new JLabel("OPTION CHAIN", SwingConstants.CENTER);
        selectedIndexLabel.setFont(selectedIndexLabel.getFont().deriveFont(Font.BOLD, 14f));
        headerPanel.add(selectedIndexLabel);

        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"CE PRICE", "STRIKE", "PE PRICE"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        optionTable = new JTable(model);
        optionTable.setDefaultEditor(Object.class, null);
        add(new JScrollPane(optionTable), BorderLayout.CENTER);
    }

    public JTable getOptionTable() { return optionTable; }
    public DefaultTableModel getModel() { return model; }
    public JLabel getSelectedIndexLabel() { return selectedIndexLabel; }

    public void setSelectedIndex(Index idx) {
        this.index = idx;
        refreshOptionChain();
    }

    private void refreshOptionChain() {
        if (index == null) {
            model.setRowCount(0);
            selectedIndexLabel.setText("OPTION CHAIN");
            return;
        }

        selectedIndexLabel.setText("Option Chain - " + index.getSymbol());

        List<OptionStrike> strikes = index.getOptionChain().getStrikes();
        model.setRowCount(0);
        for (OptionStrike s : strikes) {
            model.addRow(new Object[] {
                String.format("%.2f", s.getCallPremium()),
                String.format("%.2f", s.getStrikePrice()),
                String.format("%.2f", s.getPutPremium())
            });
        }
    }

    public JButton getBackButton() {
        return backButton;
    }
}