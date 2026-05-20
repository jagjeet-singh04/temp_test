//package com.bnpp.assessment.ui.panels;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//
//public class MarketWatchPanel extends JPanel {
//
//    private JTable indexTable;
//
//    private DefaultTableModel model;
//
//    public MarketWatchPanel() {
//
//        setLayout(new BorderLayout());
//
//        String[] columns = {
//                "Index",
//                "Spot Price"
//        };
//
//        model = new DefaultTableModel(columns, 0);
//
//        indexTable = new JTable(model);
//
//        JScrollPane scrollPane =
//                new JScrollPane(indexTable);
//
//        add(scrollPane, BorderLayout.CENTER);
//    }
//
//    public JTable getIndexTable() {
//        return indexTable;
//    }
//
//    public DefaultTableModel getModel() {
//        return model;
//    }
//}


package com.bnpp.assessment.ui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.bnpp.assessment.models.Index;

/**
 * Broker-style MarketWatch panel.
 * * <p>Two cards are displayed inside a nested {@link CardLayout} panel:
 * <ul>
 * <li><b>Spot</b> - a table that shows index name + spot price.</li>
 * <li><b>Chain</b> - the option chain view ({@link OptionChainPanel}).</li>
 * </ul>
 * * A small toolbar (search) sits above the cards.
 */
public class MarketWatchPanel extends JPanel {

    /* ----------------------------------------------------------------------
     * Card names
     * ---------------------------------------------------------------------- */
    private static final String CARD_SPOT = "Spot";
    private static final String CARD_CHAIN = "Chain";

    /* ----------------------------------------------------------------------
     * Layout helpers
     * ---------------------------------------------------------------------- */
    /** Outer layout - holds the toolbar (NORTH) and the card panel (CENTER). */
    private final BorderLayout outerLayout = new BorderLayout();

    /** Inner panel that actually flips between Spot & Chain. */
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    /* ----------------------------------------------------------------------
     * Spot card components
     * ---------------------------------------------------------------------- */
    private final JTable spotTable;
    private final DefaultTableModel spotModel;
    private static final String[] SPOT_COLUMNS = {"Index", "Spot Price"};

    private JPopupMenu spotPopup;
    private JMenuItem viewOptionChainItem;
    private final JLabel statusLabel;

    /* ----------------------------------------------------------------------
     * Chain card component
     * ---------------------------------------------------------------------- */
    private final OptionChainPanel chainPanel;

    /* ----------------------------------------------------------------------
     * Constructor
     * ---------------------------------------------------------------------- */
    public MarketWatchPanel() {
        /* ----- outer panel (this) ----- */
        setLayout(outerLayout);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        /* ----- build Spot card ----- */
        spotModel = new DefaultTableModel(SPOT_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        spotTable = new JTable(spotModel);
        configureSpotTable();

        statusLabel = new JLabel("Rows: 0", SwingConstants.RIGHT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 4));

        JPanel spotCard = buildSpotCard();

        /* ----- build Chain card ----- */
        chainPanel = new OptionChainPanel(); // already a JPanel

        /* ----- add cards to the inner CardLayout panel ----- */
        cardPanel.add(spotCard, CARD_SPOT);
        cardPanel.add(chainPanel, CARD_CHAIN);

        /* ----- toolbar (search) ----- */
        JToolBar toolbar = createToolbar();

        /* ----- assemble outer layout ----- */
        add(toolbar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        /* ----- default view ----- */
        showSpotView();
    }

    /* ----------------------------------------------------------------------
     * Spot table configuration
     * ---------------------------------------------------------------------- */
    private void configureSpotTable() {
        spotTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spotTable.setRowHeight(26);
        spotTable.setFillsViewportHeight(true);
        spotTable.setAutoCreateRowSorter(true); // sortable columns

        // column configuration
        TableColumnModel cm = spotTable.getColumnModel();

        TableColumn idxCol = cm.getColumn(0);
        idxCol.setPreferredWidth(180);
        idxCol.setHeaderValue("Index");

        TableColumn priceCol = cm.getColumn(1);
        priceCol.setPreferredWidth(150);
        priceCol.setHeaderValue("Spot Price");
        priceCol.setCellRenderer(new SpotPriceRenderer());

        // zebra striping + header tooltip
        spotTable.setDefaultRenderer(Object.class, new ZebraRenderer());
        spotTable.getTableHeader().setToolTipText("Right-click a row -> View Option Chain");

        // context menu
        spotPopup = new JPopupMenu();
        viewOptionChainItem = new JMenuItem("View Option Chain");
        viewOptionChainItem.setActionCommand("viewOptionChain");
        spotPopup.add(viewOptionChainItem);

        spotTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { maybeShowPopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }
        });
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int row = spotTable.rowAtPoint(e.getPoint());
            if (row != -1) {
                spotTable.setRowSelectionInterval(row, row);
            }
            spotPopup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /* ----------------------------------------------------------------------
     * Build the Spot card (table + status bar)
     * ---------------------------------------------------------------------- */
    private JPanel buildSpotCard() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JScrollPane scroll = new JScrollPane(spotTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Market Watch"));

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
    }

    /* ----------------------------------------------------------------------
     * Toolbar - simple search field that filters the spot table.
     * ---------------------------------------------------------------------- */
    private JToolBar createToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JTextField search = new JTextField(12);
        search.setMaximumSize(search.getPreferredSize());
        search.setToolTipText("Filter by index name");
        search.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String txt = search.getText().trim();
                @SuppressWarnings("unchecked")
                TableRowSorter<DefaultTableModel> sorter = 
                    (TableRowSorter<DefaultTableModel>) spotTable.getRowSorter();

                if (txt.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txt, 0));
                }
            }
        });

        tb.add(new JLabel("Search: "));
        tb.add(search);
        return tb;
    }

    /* ----------------------------------------------------------------------
     * Public getters - used by the controller
     * ---------------------------------------------------------------------- */
    public JTable getSpotTable() { return spotTable; }
    public DefaultTableModel getSpotModel() { return spotModel; }
    public JMenuItem getViewOptionChainItem() { return viewOptionChainItem; }
    public OptionChainPanel getOptionChainPanel() { return chainPanel; }

    /** Update the tiny status bar (called after each market refresh). */
    public void updateRowCount() {
        statusLabel.setText("Rows: " + spotModel.getRowCount());
    }

    /** Switch to the Spot view (called from {@link MainAppPanel}). */
    public void showSpotView() {
        cardLayout.show(cardPanel, CARD_SPOT);
    }

    /** Switch to the OptionChain view (called from the popup menu). */
    public void showChainView() {
        cardLayout.show(cardPanel, CARD_CHAIN);
    }

    /* ----------------------------------------------------------------------
     * Private renderers
     * ---------------------------------------------------------------------- */
    private static final class SpotPriceRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat df = new DecimalFormat("₹ #,##0.00");
        private double previous = Double.NaN;

        @Override
        public void setValue(Object value) {
            if (value instanceof Number) {
                double cur = ((Number) value).doubleValue();
                setText(df.format(cur));

                if (!Double.isNaN(previous)) {
                    if (cur > previous) {
                        setForeground(new Color(0, 140, 0)); // up - green
                    } else if (cur < previous) {
                        setForeground(new Color(200, 0, 0)); // down - red
                    } else {
                        setForeground(Color.BLACK);
                    }
                }
                previous = cur;
                setHorizontalAlignment(RIGHT);
            } else {
                super.setValue(value);
            }
        }
    }

    private static final class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF5F5F5));
            }
            return c;
        }
    }
}