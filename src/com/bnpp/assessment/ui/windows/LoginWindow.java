package com.bnpp.assessment.ui.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

import com.bnpp.assessment.ui.panels.LoginPanel;

public class LoginWindow extends JFrame {

    public LoginWindow(LoginPanel loginPanel) {
        
        //TITLE
        super("Virtual Option Broker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(750, 750));
        setLayout(new BorderLayout());
        setSize(900, 800);
        
        //ADDING LOGIN PANEL TO THE FRAME
        add(loginPanel, BorderLayout.CENTER);
        
        setLocationRelativeTo(null);
        
        setVisible(true);
        
    }
}