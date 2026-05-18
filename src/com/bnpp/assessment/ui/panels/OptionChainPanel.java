package com.bnpp.assessment.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class OptionChainPanel extends JPanel {

    private JTable optionTable;

    private DefaultTableModel model;

    private JLabel selectedIndexLabel;

    public OptionChainPanel() {

        setLayout(new BorderLayout());

        selectedIndexLabel =
                new JLabel("OPTION CHAIN");

        add(selectedIndexLabel,
                BorderLayout.NORTH);

        String[] columns = {
                "CE PRICE",
                "STRIKE",
                "PE PRICE"
        };

        model = new DefaultTableModel(columns, 0);

        optionTable = new JTable(model);

        optionTable.setDefaultEditor(Object.class, null); 
        JScrollPane scrollPane =
                new JScrollPane(optionTable);

        add(scrollPane, BorderLayout.CENTER);
    }

    public JTable getOptionTable() {
        return optionTable;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    public JLabel getSelectedIndexLabel() {
        return selectedIndexLabel;
    }
}