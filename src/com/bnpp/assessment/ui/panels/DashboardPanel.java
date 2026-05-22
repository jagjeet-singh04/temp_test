package com.bnpp.assessment.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class DashboardPanel extends JPanel {

    // // ACCOUNT SUMMARY
    private final JLabel balanceLabel = new JLabel("Balance : ₹ 0.00");
    private final JLabel realisedLabel = new JLabel("Realized P&L : ₹ 0.00");
    private final JLabel unrealisedLabel = new JLabel("Unrealized P&L : ₹ 0.00");

    private final JTable marketTable;
    private final DefaultTableModel marketModel;
    private final JPopupMenu marketPopup;
    private final JMenuItem viewOptionChainItem;

    // // OPEN POSITIONS
    private final JTable positionTable;
    private final DefaultTableModel positionModel;

    // // CONSTRUCTOR
    public DashboardPanel() {

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        /* ************ Summary Cards ************ */
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        summaryPanel.add(makeSummaryCard(balanceLabel, new Color(0x003366)));
        summaryPanel.add(makeSummaryCard(realisedLabel, new Color(0x006633)));
        summaryPanel.add(makeSummaryCard(unrealisedLabel, new Color(0x663300)));

        /* ************ Market Watch Table ************ */
        String[] marketCols = {"Index", "Spot Price"};
        marketModel = new DefaultTableModel(marketCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        marketTable = new JTable(marketModel);
        marketTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        marketTable.setRowHeight(22);
        styleTable(marketTable, new Color(0xE0F7FA));

        marketPopup = new JPopupMenu();
        viewOptionChainItem = new JMenuItem("View Option Chain");
        viewOptionChainItem.setActionCommand("ViewOptionChain");
        marketPopup.add(viewOptionChainItem);
        marketTable.setComponentPopupMenu(marketPopup);

        JScrollPane marketScroll = new JScrollPane(marketTable);
        marketScroll.setBorder(BorderFactory.createTitledBorder("Market Watch"));

        /* ************ Positions Table ************ */
        String[] posCols = {
            "Index", "Option Type", "Strike", "Trade", "Qty", 
            "Entry Premium", "Current Premium", "P&L"
        };
        positionModel = new DefaultTableModel(posCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        positionTable = new JTable(positionModel);
        positionTable.setRowHeight(22);
        styleTable(positionTable, new Color(0xE6F2FF));

        JScrollPane positionScroll = new JScrollPane(positionTable);
        positionScroll.setBorder(BorderFactory.createTitledBorder("Open Positions"));

        /* ************ Split Pane Construction ************ */
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(marketScroll);
        split.setBottomComponent(positionScroll);
        
        split.setResizeWeight(0.45);
        split.setOneTouchExpandable(true);
        split.setBorder(BorderFactory.createEmptyBorder());

        add(summaryPanel, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    /* *
     * Build a coloured card that holds a label + its eye-button.
     */
    private JPanel makeSummaryCard(JLabel valueLabel, Color background) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(background);
        card.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 14f));

        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /* *
     * Styling that gives alternating row colours, a coloured header, etc.
     */
    private void styleTable(JTable table, Color headerBg) {
        table.getTableHeader().setBackground(headerBg);
        table.getTableHeader().setForeground(Color.DARK_GRAY);
        table.setSelectionBackground(new Color(0xB0C4DE));
        table.setSelectionForeground(Color.BLACK);
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF5F5F5));
                }
                return c;
            }
        });
    }

    // // Getters
    public JLabel getBalanceLabel() { return balanceLabel; }
    public JLabel getRealisedLabel() { return realisedLabel; }
    public JLabel getUnrealisedLabel() { return unrealisedLabel; }

    public JTable getMarketTable() { return marketTable; }
    public DefaultTableModel getMarketModel() { return marketModel; }
    public JPopupMenu getMarketPopup() { return marketPopup; }
    public JMenuItem getViewOptionChainMenuItem() { return viewOptionChainItem; }

    public DefaultTableModel getPositionModel() { return positionModel; }

    public void fireOpenOptionChainEvent(String symbol) {
        firePropertyChange("openOptionChain", null, symbol);
    }

    public void updateStrikePrice(String indexSymbol, double newStrike) {
        // column "Strike" is the 3rd column -> index 2
        final int STRIKE_COL = 2;

        DefaultTableModel model = getPositionModel();
        for (int row = 0; row < model.getRowCount(); row++) {
            Object sym = model.getValueAt(row, 0);
            if (sym != null && sym.toString().equals(indexSymbol)) {
                String formatted = String.format("%.2f", newStrike);
                model.setValueAt(formatted, row, STRIKE_COL);
            }
        }
    }
}