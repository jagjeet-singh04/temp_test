package com.bnpp.assessment.controllers;

import com.bnpp.assessment.dao.IndexDao;
import com.bnpp.assessment.dao.IndexDaoImpl;
import com.bnpp.assessment.models.Index;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.ui.panels.MarketWatchPanel;
import com.bnpp.assessment.ui.panels.OptionChainPanel;

import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import java.util.List;

public class MarketWatchController {

	 private MarketWatchPanel marketPanel;
	    private OptionChainPanel optionPanel;
	    private IndexDao indexDao = new IndexDaoImpl();
	    private User currentUser;                              // NEW
	    private OptionChainController currentOptionChainController;

	    public MarketWatchController(MarketWatchPanel marketPanel, OptionChainPanel optionPanel, User currentUser) {
	        this.marketPanel = marketPanel;
	        this.optionPanel = optionPanel;
	        this.currentUser = currentUser;
	        loadIndices();
	        attachListeners();
	        startAutoRefresh();
	    }

    private void startAutoRefresh() {
        Timer timer = new Timer(2000, e -> loadIndices());
        timer.start();
    }

    private void loadIndices() {
        DefaultTableModel model = marketPanel.getModel();
        List<Index> indices = indexDao.findAll();

        // Preserve selection if possible
        int selectedRow = marketPanel.getIndexTable().getSelectedRow();

        model.setRowCount(0);
        for (Index index : indices) {
            model.addRow(new Object[]{ index.getSymbol(), index.getLastPrice() });
        }

        // Restore selection if still valid
        if (selectedRow >= 0 && selectedRow < model.getRowCount()) {
            marketPanel.getIndexTable().setRowSelectionInterval(selectedRow, selectedRow);
        }
    }
    
    

    private void attachListeners() {
       
        
        marketPanel.getIndexTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = marketPanel.getIndexTable().getSelectedRow();
                if (row == -1) return;

                List<Index> indices = indexDao.findAll();
                Index selectedIndex = indices.get(row);

                if (currentOptionChainController != null) {
                    currentOptionChainController.stopAutoRefresh();
                }

                // Pass the user to the option chain controller
                currentOptionChainController = new OptionChainController(optionPanel, selectedIndex, currentUser);
                currentOptionChainController.loadOptionChain();
            }
        });
    }
}