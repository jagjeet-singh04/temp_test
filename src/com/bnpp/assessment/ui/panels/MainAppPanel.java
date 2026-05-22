//package com.bnpp.assessment.ui.panels;
//
//import java.awt.*;
//import javax.swing.*;
//import com.bnpp.assessment.controllers.*;
//import com.bnpp.assessment.models.User;
//import com.bnpp.assessment.util.MarketAutoSimulator;
//import com.bnpp.assessment.util.MarketDataInitializer;
//
//public class MainAppPanel extends JPanel {
//
//    private MainController mainController;
//    private User currentUser;
//
//    private CardLayout cardLayout;
//    private JPanel contentPanel;
//    private JPanel sidebarPanel;
//
//    private JButton dashboardButton, marketWatchButton, tradeButton, walletButton, profileButton;
//
//    private DashboardPanel dashboardPanel;
//    private MarketWatchPanel marketWatchPanel;
//    private OptionChainPanel optionChainPanel;
//    private PositionPanel positionPanel;          // NEW
//    private WalletPanel walletPanel;
//    private ProfilePanel profilePanel;
//
//    private MarketWatchController marketWatchController;
//    private WalletController walletController;
//    private PositionController positionController; // NEW
//
//    public MainAppPanel(MainController mainController, User currentUser) {
//        this.mainController = mainController;
//        this.currentUser = currentUser;
//        setLayout(new BorderLayout());
//
//        MarketDataInitializer.initialize();
//        MarketAutoSimulator.start();
//
//        initializeComponents();
//        initializeControllers();
//        setupSidebar();
//        setupContentPanel();
//        attachListeners();
//    }
//
//    private void initializeComponents() {
//        cardLayout = new CardLayout();
//        contentPanel = new JPanel(cardLayout);
//        sidebarPanel = new JPanel();
//
//        dashboardButton = new JButton("Dashboard");
//        marketWatchButton = new JButton("Market Watch");
//        tradeButton = new JButton("Positions");        // renamed
//        walletButton = new JButton("Wallet");
//        profileButton = new JButton("Profile");
//
//        dashboardPanel = new DashboardPanel();
//        marketWatchPanel = new MarketWatchPanel();
//        optionChainPanel = new OptionChainPanel();
//        positionPanel = new PositionPanel();            // NEW
//        walletPanel = new WalletPanel();
//        profilePanel = new ProfilePanel();
//    }
//
//    private void initializeControllers() {
//        marketWatchController = new MarketWatchController(
//                marketWatchPanel, optionChainPanel, currentUser);
//        walletController = new WalletController(walletPanel, currentUser);
//        positionController = new PositionController(positionPanel, currentUser); // NEW
//        positionPanel.addPropertyChangeListener("walletRefresh", evt -> {
//            walletController.loadWallet();
//        });
//    }
//
//    private void setupSidebar() {
//        sidebarPanel.setPreferredSize(new Dimension(220, 0));
//        sidebarPanel.setLayout(new GridLayout(10, 1, 10, 10));
//        sidebarPanel.add(dashboardButton);
//        sidebarPanel.add(marketWatchButton);
//        sidebarPanel.add(tradeButton);
//        sidebarPanel.add(walletButton);
//        sidebarPanel.add(profileButton);
//        add(sidebarPanel, BorderLayout.WEST);
//    }
//
//    private void setupContentPanel() {
//        JSplitPane marketSplitPane = new JSplitPane(
//                JSplitPane.HORIZONTAL_SPLIT,
//                marketWatchPanel,
//                optionChainPanel);
//        marketSplitPane.setDividerLocation(300);
//
//        contentPanel.add(dashboardPanel, "DASHBOARD");
//        contentPanel.add(marketSplitPane, "MARKET");
//        contentPanel.add(positionPanel, "POSITIONS");   // NEW
//        contentPanel.add(walletPanel, "WALLET");
//        contentPanel.add(profilePanel, "PROFILE");
//
//        add(contentPanel, BorderLayout.CENTER);
//    }
//
//    private void attachListeners() {
//        dashboardButton.addActionListener(e -> cardLayout.show(contentPanel, "DASHBOARD"));
//        marketWatchButton.addActionListener(e -> cardLayout.show(contentPanel, "MARKET"));
//        tradeButton.addActionListener(e -> cardLayout.show(contentPanel, "POSITIONS"));
//        walletButton.addActionListener(e -> cardLayout.show(contentPanel, "WALLET"));
//        profileButton.addActionListener(e -> cardLayout.show(contentPanel, "PROFILE"));
//    }
//    
//}



package com.bnpp.assessment.ui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.bnpp.assessment.controllers.DashboardController;
import com.bnpp.assessment.controllers.MainController;
import com.bnpp.assessment.controllers.MarketWatchController;
import com.bnpp.assessment.controllers.PositionController;
import com.bnpp.assessment.controllers.WalletController;
import com.bnpp.assessment.dao.IndexDaoImpl;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.util.MarketAutoSimulator;
import com.bnpp.assessment.util.MarketDataInitializer;

/**
 * Top-level container for the whole application.
 * * <p>Improvements over the previous version:
 * <ul>
 * <li>Vertical side navigation bar with toggle-buttons (selected state is highlighted).</li>
 * <li>Header bar showing the app title and the logged-in user (plus a Logout button).</li>
 * <li>Back-button on the {@link OptionChainPanel} is wired again.</li>
 * <li>Utility method {@link #selectNavButton(String)} keeps the UI state in sync when cards are switched programmatically.</li>
 * </ul>
 */
public class MainAppPanel extends JPanel {

    private final MainController mainController;
    private final User currentUser;

    private CardLayout cardLayout;
    private JPanel contentPanel;  // centre area that holds the cards
    private JPanel sideNavBar;    // vertical navigation bar (west)
    private JPanel headerBar;     // top bar with title & user info

    /* ----------------------------------------------------------------------
     * Navigation buttons (vertical)
     * ---------------------------------------------------------------------- */
    private static final String[] BUTTONS = {
        "Dashboard", "Market Watch", "Positions", "Wallet", "Profile"
    };

    /** Map of identifier -> toggle button (used for selection handling). */
    private final Map<String, JToggleButton> navButtons = new HashMap<>();
    private final ButtonGroup buttonGroup = new ButtonGroup();

    /* ----------------------------------------------------------------------
     * Panels - visual "pages"
     * ---------------------------------------------------------------------- */
    private DashboardPanel dashboardPanel;
    private MarketWatchPanel marketWatchPanel; // contains Spot & Chain cards
    private PositionPanel positionPanel;
    private WalletPanel walletPanel;
    private ProfilePanel profilePanel;

    private DashboardController dashboardController;
    private MarketWatchController marketWatchController;
    private WalletController walletController;
    private PositionController positionController;

    /* ----------------------------------------------------------------------
     * Constructor
     * ---------------------------------------------------------------------- */
    public MainAppPanel(MainController mainController, User currentUser) {
        this.mainController = mainController;
        this.currentUser = currentUser;

        setLayout(new BorderLayout());

        // Initialize static market data (once per application)
        MarketDataInitializer.initialize();
        MarketAutoSimulator.start();

        // Build UI components, wire controllers and layout everything
        initComponents();
        initControllers();
        layoutContent();
        attachListeners();
    }

    /* ----------------------------------------------------------------------
     * 1 UI component creation
     * ---------------------------------------------------------------------- */
    private void initComponents() {
        // ----- Card infrastructure (centre) -----
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // ----- Header (north) -----
        headerBar = createHeaderBar();

        // ----- Side navigation (west) -----
        sideNavBar = createSideNavBar();

        // ----- Individual page panels -----
        dashboardPanel = new DashboardPanel();
        marketWatchPanel = new MarketWatchPanel(); // Spot + Chain cards
        positionPanel = new PositionPanel();
        walletPanel = new WalletPanel();
        profilePanel = new ProfilePanel();
    }

    /**
     * Builds a simple header with a title on the left and userInfo on the right.
     */
    private JPanel createHeaderBar() {
        JPanel hb = new JPanel(new BorderLayout());
        hb.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        hb.setBackground(new Color(0x2C3E50)); // dark blueish

        // Title (left)
        JLabel title = new JLabel("BNPP Broker Simulator");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        hb.add(title, BorderLayout.WEST);

        // User info (right)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        userPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Logged in: " + currentUser.getUsername());
        userLabel.setForeground(Color.WHITE);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setMargin(new Insets(2, 8, 2, 8));
        logoutBtn.addActionListener(e -> mainController.doLogout());
        
        userPanel.add(userLabel);
        userPanel.add(logoutBtn);
        hb.add(userPanel, BorderLayout.EAST);

        return hb;
    }

    /**
     * Builds the vertical navigation bar using {@link JToggleButton}s.
     */
    private JPanel createSideNavBar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 6));
        sb.setBackground(new Color(0xECF0F1)); // lightGrey

        for (String txt : BUTTONS) {
            // Card name that we use later with CardLayout
            // (exactly the same string you used when you added the panels)
            String cardName = txt.toUpperCase().replace(" ", ""); // "Dashboard" -> "DASHBOARD"

            JToggleButton tb = new JToggleButton(txt);
            tb.setName(cardName); // <-- store the *card* name
            tb.setAlignmentX(Component.LEFT_ALIGNMENT);
            tb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            tb.setFocusPainted(false);
            tb.setBackground(Color.WHITE);
            tb.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

            // optional generic icon
            Icon icon = UIManager.getIcon("FileView.directoryIcon");
            tb.setIcon(icon);
            tb.setHorizontalAlignment(SwingConstants.LEFT);
            tb.setHorizontalTextPosition(SwingConstants.RIGHT);

            buttonGroup.add(tb);
            
            // keep the button in the map keyed by its *card* name (lowercase for lookup)
            navButtons.put(cardName.toLowerCase(), tb);
            sb.add(tb);
            sb.add(Box.createVerticalStrut(4));
        }

        // select the first button (Dashboard) by default
        selectNavButton("dashboard");
        return sb;
    }

    /* ----------------------------------------------------------------------
     * 2 Controllers wiring
     * ---------------------------------------------------------------------- */
    private void initControllers() {
        dashboardController = new DashboardController(dashboardPanel, currentUser);

        /*
         * MarketWatch needs a callback that tells the UI to show
         * the option chain inside the MarketWatchPanel.
         */
        marketWatchController = new MarketWatchController(marketWatchPanel, currentUser);

        walletController = new WalletController(walletPanel, currentUser);
        positionController = new PositionController(positionPanel, currentUser);

        // ----- Refresh wallet & dashboard when a position closes -----
        positionPanel.addPropertyChangeListener("walletRefresh", evt -> {
            walletController.loadWallet();
        });

        // ----- Dashboard can also request an optionChain -----
        dashboardPanel.addPropertyChangeListener("openOptionChain", evt -> {
            String symbol = (String) evt.getNewValue();
            IndexDaoImpl idxDao = new IndexDaoImpl();
            
            var idx = idxDao.findAll()
                            .stream()
                            .filter(i -> i.getSymbol().equals(symbol))
                            .findFirst()
                            .orElse(null);

            if (idx != null) {
                cardLayout.show(contentPanel, "MARKET");
                marketWatchController.showOptionChain(idx);
                selectNavButton("marketwatch"); // highlight proper nav button
            }
        });

    }

    /* ----------------------------------------------------------------------
     * 3 Layout of the two main zones (header + sideNav + centre cards)
     * ---------------------------------------------------------------------- */
    private void layoutContent() {
        // top header
        add(headerBar, BorderLayout.NORTH);

        // left side navigation
        add(sideNavBar, BorderLayout.WEST);

        // centre cards
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(marketWatchPanel, "MARKET");
        contentPanel.add(positionPanel, "POSITIONS");
        contentPanel.add(walletPanel, "WALLET");
        contentPanel.add(profilePanel, "PROFILE");

        add(contentPanel, BorderLayout.CENTER);
    }

    /* ----------------------------------------------------------------------
     * 4 Button -> card wiring
     * ---------------------------------------------------------------------- */
    private void attachListeners() {
        navButtons.forEach((id, btn) -> {
            btn.addActionListener(e -> {
                String card = mapIdToCardName(id);
                showCard(card);
                selectNavButton(id);
            });
        });
    }

    /** Translate the side nav ID to the name used in CardLayout. */
    private String mapIdToCardName(String id) {
        return switch (id) {
            case "dashboard" -> "DASHBOARD";
            case "marketwatch" -> "MARKET";
            case "positions" -> "POSITIONS";
            case "wallet" -> "WALLET";
            case "profile" -> "PROFILE";
            default -> id.toUpperCase();
        };
    }

    /** Switches the centre CardLayout and performs view-specific work. */
    public void showCard(String cardName) {
        cardLayout.show(contentPanel, cardName);

        if ("MARKET".equals(cardName)) {
            // Ensure the Spot card is displayed
            marketWatchPanel.showSpotView();
        }
    }

    /** Marks the navigation button with the given id as selected. */
    private void selectNavButton(String id) {
        JToggleButton btn = navButtons.get(id);
        if (btn != null) {
            btn.setSelected(true);
        }
    }

    /** Returns the top-level window that contains this panel (or null). */
    public Window getOwningWindow() {
        return SwingUtilities.getWindowAncestor(this);
    }

    /* ----------------------------------------------------------------------
     * Public getters (optional, for tests or future extensions)
     * ---------------------------------------------------------------------- */
    public DashboardPanel getDashboardPanel() { return dashboardPanel; }
    public MarketWatchPanel getMarketWatchPanel() { return marketWatchPanel; }
    public PositionPanel getPositionPanel() { return positionPanel; }
    public WalletPanel getWalletPanel() { return walletPanel; }
    public ProfilePanel getProfilePanel() { return profilePanel; }

    public CardLayout getCardLayout() { return cardLayout; }
    public JPanel getContentPanel() { return contentPanel; }
}