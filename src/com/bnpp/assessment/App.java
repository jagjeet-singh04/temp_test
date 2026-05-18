package com.bnpp.assessment;

import javax.swing.SwingUtilities;

import com.bnpp.assessment.controllers.LoginController;
import com.bnpp.assessment.controllers.MainController;
import com.bnpp.assessment.ui.panels.LoginPanel;
import com.bnpp.assessment.ui.windows.LoginWindow;
import com.bnpp.assessment.util.MarketAutoSimulator;
import com.bnpp.assessment.util.MarketDataInitializer;
public class App {

    public static void main(String[] args) {

        MarketDataInitializer.initialize();

        MarketAutoSimulator.start();

        SwingUtilities.invokeLater(() -> {

            MainController mainController =
                    new MainController();

            LoginPanel loginPanel =
                    new LoginPanel();

            new LoginController(
                    loginPanel,
                    mainController
            );

            new LoginWindow(loginPanel);
        });
    }
}