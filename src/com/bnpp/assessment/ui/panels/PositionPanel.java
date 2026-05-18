package com.bnpp.assessment.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PositionPanel extends JPanel {

    private JTable positionTable;
    private DefaultTableModel model;

    public PositionPanel() {
        setLayout(new BorderLayout());

        String[] columns = {
                "Index",
                "Option Type",
                "Strike",
                "Trade",
                "Qty",
                "Entry Premium",
                "Current Premium",
                "P&L"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   // read only
            }
        };

        positionTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(positionTable);
        add(scrollPane, BorderLayout.CENTER);
     // In PositionPanel constructor, add:
        positionTable.setDefaultEditor(Object.class, null); // no editing
        positionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = positionTable.getSelectedRow();
                    if (row != -1) {
                        firePropertyChange("closePosition", null, row);  // custom signal
                    }
                }
            }
        });
    }

    public JTable getPositionTable() {
        return positionTable;
    }

    public DefaultTableModel getModel() {
        return model;
    }
}