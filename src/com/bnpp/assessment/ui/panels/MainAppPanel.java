package com.bnpp.assessment.ui.panels;

import java.awt.*;
import javax.swing.*;
import com.bnpp.assessment.controllers.*;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.util.MarketAutoSimulator;
import com.bnpp.assessment.util.MarketDataInitializer;

public class MainAppPanel extends JPanel {

    private MainController mainController;
    private User currentUser;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;

    private JButton dashboardButton, marketWatchButton, tradeButton, walletButton, profileButton;

    private DashboardPanel dashboardPanel;
    private MarketWatchPanel marketWatchPanel;
    private OptionChainPanel optionChainPanel;
    private PositionPanel positionPanel;          // NEW
    private WalletPanel walletPanel;
    private ProfilePanel profilePanel;

    private MarketWatchController marketWatchController;
    private WalletController walletController;
    private PositionController positionController; // NEW

    public MainAppPanel(MainController mainController, User currentUser) {
        this.mainController = mainController;
        this.currentUser = currentUser;
        setLayout(new BorderLayout());

        MarketDataInitializer.initialize();
        MarketAutoSimulator.start();

        initializeComponents();
        initializeControllers();
        setupSidebar();
        setupContentPanel();
        attachListeners();
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        sidebarPanel = new JPanel();

        dashboardButton = new JButton("Dashboard");
        marketWatchButton = new JButton("Market Watch");
        tradeButton = new JButton("Positions");        // renamed
        walletButton = new JButton("Wallet");
        profileButton = new JButton("Profile");

        dashboardPanel = new DashboardPanel();
        marketWatchPanel = new MarketWatchPanel();
        optionChainPanel = new OptionChainPanel();
        positionPanel = new PositionPanel();            // NEW
        walletPanel = new WalletPanel();
        profilePanel = new ProfilePanel();
    }

    private void initializeControllers() {
        marketWatchController = new MarketWatchController(
                marketWatchPanel, optionChainPanel, currentUser);
        walletController = new WalletController(walletPanel, currentUser);
        positionController = new PositionController(positionPanel, currentUser); // NEW
        positionPanel.addPropertyChangeListener("walletRefresh", evt -> {
            walletController.loadWallet();
        });
    }

    private void setupSidebar() {
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setLayout(new GridLayout(10, 1, 10, 10));
        sidebarPanel.add(dashboardButton);
        sidebarPanel.add(marketWatchButton);
        sidebarPanel.add(tradeButton);
        sidebarPanel.add(walletButton);
        sidebarPanel.add(profileButton);
        add(sidebarPanel, BorderLayout.WEST);
    }

    private void setupContentPanel() {
        JSplitPane marketSplitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                marketWatchPanel,
                optionChainPanel);
        marketSplitPane.setDividerLocation(300);

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(marketSplitPane, "MARKET");
        contentPanel.add(positionPanel, "POSITIONS");   // NEW
        contentPanel.add(walletPanel, "WALLET");
        contentPanel.add(profilePanel, "PROFILE");

        add(contentPanel, BorderLayout.CENTER);
    }

    private void attachListeners() {
        dashboardButton.addActionListener(e -> cardLayout.show(contentPanel, "DASHBOARD"));
        marketWatchButton.addActionListener(e -> cardLayout.show(contentPanel, "MARKET"));
        tradeButton.addActionListener(e -> cardLayout.show(contentPanel, "POSITIONS"));
        walletButton.addActionListener(e -> cardLayout.show(contentPanel, "WALLET"));
        profileButton.addActionListener(e -> cardLayout.show(contentPanel, "PROFILE"));
    }
    
}