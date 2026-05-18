package com.bnpp.assessment.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MarketWatchPanel extends JPanel {

    private JTable indexTable;

    private DefaultTableModel model;

    public MarketWatchPanel() {

        setLayout(new BorderLayout());

        String[] columns = {
                "Index",
                "Spot Price"
        };

        model = new DefaultTableModel(columns, 0);

        indexTable = new JTable(model);

        JScrollPane scrollPane =
                new JScrollPane(indexTable);

        add(scrollPane, BorderLayout.CENTER);
    }

    public JTable getIndexTable() {
        return indexTable;
    }

    public DefaultTableModel getModel() {
        return model;
    }
}