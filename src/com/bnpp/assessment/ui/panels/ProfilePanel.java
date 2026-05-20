package com.bnpp.assessment.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.bnpp.assessment.dto.TradeRow;         // Assuming historical model data transfers
import com.bnpp.assessment.dto.TransactionRow;   // Assuming ledger model mappings
import com.bnpp.assessment.ui.chart.PnlChart;    // Matches import definition from workspace

/**
 * Professional "User Profile" view.
 * * <p>Features:
 * <ul>
 * <li>Account details form (Name, Email, Password, Confirm, Save).</li>
 * <li>Toggle-button navigation (Trades / Transactions / Performance).</li>
 * <li>Searchable, sortable, zebra-striped history table.</li>
 * <li>Performance button opens a modal dialog with a cumulative PnL chart.</li>
 * </ul>
 * * <p>All public getters are preserved, so existing controllers keep working.
 */
public class ProfilePanel extends JPanel {

    /* ----------------------------------------------------------------------
     * 1Header
     * ---------------------------------------------------------------------- */
    private final JLabel titleLabel = new JLabel("User Profile");
    private final JLabel subtitleLabel = new JLabel("View your profile and switch to edit mode when needed.");

    /* ----------------------------------------------------------------------
     * 2Account details form
     * ---------------------------------------------------------------------- */
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField pwdField = new JPasswordField(20);
    private final JPasswordField confirmPwdField = new JPasswordField(20);
    private final JButton editButton = new JButton("Edit Profile");
    private final JButton saveButton = new JButton("Save");
    private final JButton cancelButton = new JButton("Cancel");

    /* ----------------------------------------------------------------------
     * 3Toggle-buttons (navigation)
     * ---------------------------------------------------------------------- */
    private final JToggleButton btnShowTrades = new JToggleButton("Trades");
    private final JToggleButton btnShowTxns = new JToggleButton("Transactions");
    private final JToggleButton btnShowPerformance = new JToggleButton("Performance");
    private final javax.swing.ButtonGroup navigationGroup = new javax.swing.ButtonGroup();

    /* ----------------------------------------------------------------------
     * 4History table + search toolbar
     * ---------------------------------------------------------------------- */
    private JTable historyTable;
    private DefaultTableModel historyModel;
    private final JTextField searchField = new JTextField(12);

    /* ----------------------------------------------------------------------
     * 5Performance chart (lazy - created on first use)
     * ---------------------------------------------------------------------- */
    private final PnlChart pnlChart = new PnlChart(); // reusable chart component

    /* ======================================================================
     * CONSTRUCTOR - build the UI
     * ====================================================================== */
    public ProfilePanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        setBackground(new Color(0xF7F9FC));

        /* ----- 1Header ----- */
        JPanel header = new JPanel();
        header.setLayout(new javax.swing.BoxLayout(header, javax.swing.BoxLayout.Y_AXIS));
        header.setOpaque(false);

        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        titleLabel.setForeground(new Color(0x1F2D3D));

        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setForeground(new Color(0x607080));

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitleLabel);
        add(header, BorderLayout.NORTH);

        /* ----- 2Center - split into form (west) + navigation + table (center) ----- */
        JPanel centre = new JPanel(new BorderLayout(12, 12));
        centre.setOpaque(false);
        centre.add(createAccountFormPanel(), BorderLayout.WEST);
        centre.add(createNavigationPanel(), BorderLayout.NORTH);
        centre.add(createTablePanel(), BorderLayout.CENTER);
        
        add(centre, BorderLayout.CENTER);

        setEditable(false);
    }

    /* ----------------------------------------------------------------------
     * SECTION 2 - Account details form
     * ---------------------------------------------------------------------- */
    private JPanel createAccountFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD5DCE3), 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
            ),
            "Account Details",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            form.getFont().deriveFont(Font.BOLD)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.EAST;

        // Row 0 - Name
        gc.gridx = 0; gc.gridy = 0;
        form.add(new JLabel("Name:"), gc);
        gc.gridx = 1; gc.anchor = GridBagConstraints.WEST;
        form.add(nameField, gc);

        // Row 1 - E-mail
        gc.gridx = 0; gc.gridy = 1; gc.anchor = GridBagConstraints.EAST;
        form.add(new JLabel("E-mail:"), gc);
        gc.gridx = 1; gc.anchor = GridBagConstraints.WEST;
        form.add(emailField, gc);

        // Row 2 - New password
        gc.gridx = 0; gc.gridy = 2; gc.anchor = GridBagConstraints.EAST;
        form.add(new JLabel("New Password:"), gc);
        gc.gridx = 1; gc.anchor = GridBagConstraints.WEST;
        form.add(pwdField, gc);

        // Row 3 - Confirm password
        gc.gridx = 0; gc.gridy = 3; gc.anchor = GridBagConstraints.EAST;
        form.add(new JLabel("Confirm:"), gc);
        gc.gridx = 1; gc.anchor = GridBagConstraints.WEST;
        form.add(confirmPwdField, gc);

        // Row 4 - Save button (centered, spanning two columns)
        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        gc.insets = new Insets(12, 4, 4, 4);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        actionRow.setOpaque(false);
        editButton.setFocusPainted(false);
        saveButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        actionRow.add(editButton);
        actionRow.add(saveButton);
        actionRow.add(cancelButton);
        form.add(actionRow, gc);

        // Keep a reasonable width - the form will not stretch with the window
        form.setPreferredSize(new Dimension(320, 0));
        return form;
    }

    /* ----------------------------------------------------------------------
     * SECTION 3 - Navigation toggle-buttons
     * ---------------------------------------------------------------------- */
    private JPanel createNavigationPanel() {
        // Add buttons to a ButtonGroup so only one can be selected
        navigationGroup.add(btnShowTrades);
        navigationGroup.add(btnShowTxns);
        navigationGroup.add(btnShowPerformance);

        // Select "Trades" by default
        btnShowTrades.setSelected(true);

        // Visual style - flat, same size, small icon placeholders
        for (JToggleButton b : List.of(btnShowTrades, btnShowTxns, btnShowPerformance)) {
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            b.setBackground(new Color(0xECF0F1));
            b.setOpaque(true);
        }

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        nav.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0xBDC3C7), 1),
            "View",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            nav.getFont().deriveFont(Font.BOLD)
        ));

        nav.add(btnShowTrades);
        nav.add(btnShowTxns);
        nav.add(btnShowPerformance);
        return nav;
    }

    /* ----------------------------------------------------------------------
     * SECTION 4 - Table panel (search + table)
     * ---------------------------------------------------------------------- */
    private JPanel createTablePanel() {
        // Table model (initially empty - columns will be swapped later)
        historyModel = new DefaultTableModel(new Object[]{"-"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        historyTable = new JTable(historyModel);
        historyTable.setFillsViewportHeight(true);
        historyTable.setAutoCreateRowSorter(true);
        historyTable.setRowHeight(24);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Zebra striping
        historyTable.setDefaultRenderer(Object.class, new ZebraRenderer());

        JScrollPane scroll = new JScrollPane(historyTable);
        scroll.setBorder(BorderFactory.createTitledBorder("History"));

        // ----- Search toolbar (above the table) -----
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(new JLabel("Search: "));
        toolbar.add(searchField);
        toolbar.addSeparator(new Dimension(12, 0));
        toolbar.add(Box.createHorizontalGlue());

        // Live filter - the controller can also call this method if needed
        searchField.setToolTipText("Filter table rows (case-insensitive)");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String txt = searchField.getText().trim();
                @SuppressWarnings("unchecked")
                TableRowSorter<DefaultTableModel> sorter = 
                    (TableRowSorter<DefaultTableModel>) historyTable.getRowSorter();

                if (txt.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txt));
                }
            }
        });

        // ----- Combine toolbar + scroll into one panel -----
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /* ----------------------------------------------------------------------
     * TABLE RENDERER - simple zebra striping
     * ---------------------------------------------------------------------- */
    private static final class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF5F5F5));
            }
            return c;
        }
    }

    /* ----------------------------------------------------------------------
     * PUBLIC GETTERS - unchanged (used by controllers)
     * ---------------------------------------------------------------------- */
    public JTextField getNameField() { return nameField; }
    public JTextField getEmailField() { return emailField; }
    public JPasswordField getPwdField() { return pwdField; }
    public JPasswordField getConfirmPwdField() { return confirmPwdField; }
    public JButton getSaveButton() { return saveButton; }
    public JButton getEditButton() { return editButton; }
    public JButton getCancelButton() { return cancelButton; }

    public JToggleButton getBtnShowTrades() { return btnShowTrades; }
    public JToggleButton getBtnShowTxns() { return btnShowTxns; }
    public JToggleButton getBtnShowPerformance() { return btnShowPerformance; }

    public void setEditable(boolean editable) {
        nameField.setEditable(editable);
        emailField.setEditable(editable);
        pwdField.setEditable(editable);
        confirmPwdField.setEditable(editable);

        nameField.setBackground(editable ? Color.WHITE : new Color(0xEEF2F6));
        emailField.setBackground(editable ? Color.WHITE : new Color(0xEEF2F6));
        pwdField.setBackground(editable ? Color.WHITE : new Color(0xEEF2F6));
        confirmPwdField.setBackground(editable ? Color.WHITE : new Color(0xEEF2F6));

        editButton.setVisible(!editable);
        saveButton.setVisible(editable);
        cancelButton.setVisible(editable);
        revalidate();
        repaint();
    }

    public void clearPasswordFields() {
        pwdField.setText("");
        confirmPwdField.setText("");
    }

    public DefaultTableModel getHistoryModel() { return historyModel; }
    public JTable getHistoryTable() { return historyTable; }

    /* ----------------------------------------------------------------------
     * TABLE DATA HELPERS (unchanged logic, only a little cleanup)
     * ---------------------------------------------------------------------- */
    public void setTradeHistory(List<TradeRow> rows) {
        String[] cols = {
            "Trade ID", "Index", "Strike", "Opt Type", 
            "Trade Type", "Qty", "Premium", "Total", "Status", "Time"
        };
        historyModel.setColumnIdentifiers(cols);
        historyModel.setRowCount(0);
        for (TradeRow r : rows) {
            historyModel.addRow(new Object[]{
                r.getTradeId(),
                r.getIndexSymbol(),
                r.getStrikePrice(),
                r.getOptionType(),
                r.getTradeType(),
                r.getQuantity(),
                String.format("%.2f", r.getPremium()),
                String.format("%.2f", r.getTotalAmount()),
                r.getStatus(),
                r.getTradeTime()
            });
        }
    }

    public void setTransactionHistory(List<TransactionRow> rows) {
        String[] cols = { "Txn ID", "Type", "Amount", "Date", "Balance After" };
        historyModel.setColumnIdentifiers(cols);
        historyModel.setRowCount(0);
        for (TransactionRow r : rows) {
            historyModel.addRow(new Object[]{
                r.getTxnId(),
                r.getType(),
                String.format("%.2f", r.getAmount()),
                r.getDate(),
                r.getBalanceAfter() == null ? "" : String.format("%.2f", r.getBalanceAfter())
            });
        }
    }

    /* ----------------------------------------------------------------------
     * PERFORMANCE DIALOG - called by the controller when the user clicks the button
     * ---------------------------------------------------------------------- */
    public void showPerformanceDialog() {
        JDialog dlg = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Performance - Cumulative P&L",
            Dialog.ModalityType.APPLICATION_MODAL
        );
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.add(pnlChart.getChartPanel(), BorderLayout.CENTER);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    /** Expose the chart so the controller can push data into it. */
    public PnlChart getPnlChart() {
        return pnlChart;
    }
}