package com.bnpp.assessment.ui.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import com.bnpp.assessment.ui.panels.MainAppPanel;

public class MainWindow extends JFrame {

    public MainWindow(MainAppPanel mainAppPanel) {

        super("Virtual Option Broker");

        setLayout(new BorderLayout());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(1400, 850));

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        add(mainAppPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }
}