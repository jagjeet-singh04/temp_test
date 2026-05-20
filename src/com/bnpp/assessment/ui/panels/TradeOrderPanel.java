package com.bnpp.assessment.ui.panels;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.bnpp.assessment.controllers.TradeController;
import com.bnpp.assessment.dao.OptionStrikeDao;
import com.bnpp.assessment.dao.OptionStrikeDaoImpl;
import com.bnpp.assessment.models.OptionStrike;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.util.RefreshScheduler; // Assuming standard framework background manager package

/**
 * Trade panel that can be embedded in any container (CardLayout, SplitPane, ...).
 * * One instance is cached per {@code OptionStrike.id}. Call {@link #showPanel(User, OptionStrike, String)}
 * to obtain (and display) the panel for a particular strike.
 */
public class TradeOrderPanel extends JPanel {

    /* ------------ UI components (instance local) ------------ */
    private final JLabel strikeLabel;
    private final JLabel optionTypeLabel;
    private final JLabel premiumLabel;
    private final JLabel lotSizeLabel;
    private final JLabel totalLabel;
    private final JTextField lotsField; // number of lots entered by the user
    private final JButton buyButton;
    private final JButton sellButton;
    private final JButton cancelButton;

    /* ------------ Business data ------------ */
    private final OptionStrikeDao strikeDao = new OptionStrikeDaoImpl();
    private OptionStrike strike; // the strike we are trading (may be refreshed)
    private final User user;
    private final String optionType; // "CE" or "PE"
    private final TradeController controller; // drives the BUY/SELL actions

    /* ------------ Global cache - one panel per strike id (optional but convenient) ------------ */
    private static final Map<Long, TradeOrderPanel> PANEL_CACHE = new ConcurrentHashMap<>();

    /**
     * Public factory - returns a cached panel (or creates a new one) and
     * guarantees the UI is up-to-date before the caller shows it.
     */
    public static TradeOrderPanel showPanel(User user, OptionStrike strike, String optionType) {
        TradeOrderPanel panel = PANEL_CACHE.compute(strike.getId(),
            (id, existing) -> (existing == null || !existing.isShowing())
                ? new TradeOrderPanel(user, strike, optionType)
                : existing);

        panel.refreshUI(); // ensure we show the latest prices
        return panel;
    }

    /* Private ctor - only the static factory can instantiate the panel. */
    private TradeOrderPanel(User user, OptionStrike strike, String optionType) {
        // 1. Store immutable fields
        this.user = user;
        this.strike = strike;
        this.optionType = optionType;
        this.controller = new TradeController(this, user, strike, optionType);

        // 2. Build UI (GridBagLayout -> nice form)
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;

        // title
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        add(new JLabel("PLACE ORDER - " + optionType), gc);

        // strike
        gc.gridy++; gc.gridwidth = 1;
        add(new JLabel("Strike:"), gc);
        gc.gridx = 1;
        strikeLabel = new JLabel();
        add(strikeLabel, gc);

        // option type (read-only)
        gc.gridx = 0; gc.gridy++;
        add(new JLabel("Option:"), gc);
        gc.gridx = 1;
        optionTypeLabel = new JLabel(optionType);
        add(optionTypeLabel, gc);

        // premium / share
        gc.gridx = 0; gc.gridy++;
        add(new JLabel("Premium/Share:"), gc);
        gc.gridx = 1;
        premiumLabel = new JLabel();
        add(premiumLabel, gc);

        // lot size (read-only)
        gc.gridx = 0; gc.gridy++;
        add(new JLabel("Lot Size:"), gc);
        gc.gridx = 1;
        lotSizeLabel = new JLabel();
        add(lotSizeLabel, gc);

        // lots (editable)
        gc.gridx = 0; gc.gridy++;
        add(new JLabel("Lots:"), gc);
        gc.gridx = 1;
        lotsField = new JTextField("1", 6);
        lotsField.addCaretListener(e -> updateTotal());
        add(lotsField, gc);

        // total
        gc.gridx = 0; gc.gridy++;
        add(new JLabel("Total:"), gc);
        gc.gridx = 1;
        totalLabel = new JLabel("₹ 0.00");
        add(totalLabel, gc);

        // button row
        gc.gridx = 0; gc.gridy++; gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buyButton = new JButton("BUY");
        sellButton = new JButton("SELL");
        cancelButton = new JButton("Cancel");
        btnPanel.add(buyButton);
        btnPanel.add(sellButton);
        btnPanel.add(cancelButton);
        add(btnPanel, gc);

        // 3. Wire actions
        buyButton.addActionListener(e -> controller.executeTrade("BUY"));
        sellButton.addActionListener(e -> controller.executeTrade("SELL"));
        cancelButton.addActionListener(e -> closePanel()); // cancel -> close

        // 4. Auto-refresh (every 50s via global scheduler)
        RefreshScheduler.getInstance().register(this, () -> refreshUI());
        refreshUI(); // immediate first fill
    }

    /*----------------------------------------------------------------------*/
    /* * UI helpers */
    /*----------------------------------------------------------------------*/

    public int getLotSize() {
        String symbol = strike.getOptionChain()
                              .getIndex()
                              .getSymbol()
                              .toUpperCase();

        return switch (symbol) {
            case "NIFTY50" -> 75;
            case "BANKNIFTY" -> 25;
            case "SENSEX" -> 10;
            default -> 1;
        };
    }

    private void refreshUI() {
        OptionStrike latest = strikeDao.findById(strike.getId());
        if (latest != null) {
            strike = latest;
        }

        strikeLabel.setText(String.format("Option Strike Price: ₹ %,.2f", strike.getStrikePrice()));
        premiumLabel.setText(String.format("₹ %,.2f", 
            optionType.equalsIgnoreCase("CE") 
                ? strike.getCallPremium() 
                : strike.getPutPremium()
        ));
        lotSizeLabel.setText(String.valueOf(getLotSize()));
        updateTotal();
    }

    private void updateTotal() {
        try {
            int lots = Integer.parseInt(lotsField.getText().trim());
            if (lots <= 0) {
                totalLabel.setText("₹ 0.00");
                return;
            }

            int lotSize = getLotSize();
            double premium = optionType.equalsIgnoreCase("CE") 
                ? strike.getCallPremium() 
                : strike.getPutPremium();

            double total = lots * lotSize * premium;
            totalLabel.setText("₹ " + String.format("%,.2f", total));
        } catch (NumberFormatException ex) {
            totalLabel.setText("₹ 0.00");
        }
    }

    public int getLots() {
        try {
            return Integer.parseInt(lotsField.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Called when the user clicks **Cancel** (or when the panel is otherwise
     * discarded). It:
     * <ul>
     * <li>Deregisters the auto-refresh</li>
     * <li>Removes the panel from cache</li>
     * <li>Removes the component structurally from parent layouts</li>
     * </ul>
     */
    public void closePanel() {
        // 1. Stop auto-refresh
        RefreshScheduler.getInstance().deregister(this);
        PANEL_CACHE.remove(strike.getId());

        // 2. Remove from UI hierarchy
        java.awt.Container parent = getParent();
        if (parent != null) {
            java.awt.Container grandParent = parent.getParent(); // this is the "tradeCard" (JPanel)
            parent.remove(this); // remove from the OptionChainPanel container
            parent.revalidate();
            parent.repaint();

            // 3. If we are inside an OptionChainPanel, show the chain view again
            if (grandParent instanceof OptionChainPanel ocp) {
                ocp.showCard("CHAIN");
            }
        }
    }

    /*----------------------------------------------------------------------*/
    /* * Helper getters used by the controller / tests */
    /*----------------------------------------------------------------------*/

    public double getCurrentPremium() {
        return optionType.equalsIgnoreCase("CE") 
            ? strike.getCallPremium() 
            : strike.getPutPremium();
    }

    public OptionStrike getStrike() { return strike; }
    public String getOptionType() { return optionType; }
    public User getUser() { return user; }
}