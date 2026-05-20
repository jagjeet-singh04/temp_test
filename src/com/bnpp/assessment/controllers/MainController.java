package com.bnpp.assessment.controllers;

import java.awt.Frame;
import java.awt.Window;
import javax.swing.SwingUtilities;

import com.bnpp.assessment.ui.windows.LoginWindow;
import com.bnpp.assessment.ui.panels.LoginPanel;

public class MainController {

    private Long currentUser;

    public Long getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Long currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Logout the current user, close the main application window and show the
     * login screen again.
     */
    public void doLogout() {
        // 1. Clear the logged-in user reference
        this.currentUser = null;

        // 2. Find the *visible* top-level frame (the main window) and close it
        Window window = null;
        for (Frame f : Frame.getFrames()) {
            // The main UI is the first visible Frame; helper dialogs are
            // usually invisible or owned by the main frame.
            if (f.isVisible()) {
                window = f;
                break;
            }
        }

        if (window != null) {
            window.dispose(); // close the main UI
        }

        // 3. Re-open the login screen on the EDT
        SwingUtilities.invokeLater(() -> {
            LoginPanel loginPanel = new LoginPanel();
            LoginWindow loginWindow = new LoginWindow(loginPanel);
            loginWindow.setLocationRelativeTo(null);
            loginWindow.setVisible(true);

            new LoginController(loginPanel, this);
        });
    }
}