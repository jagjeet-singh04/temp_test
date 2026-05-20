package com.bnpp.assessment.controllers;

import java.util.List;
import java.util.function.Consumer;
import javax.swing.table.DefaultTableModel;

import com.bnpp.assessment.dao.IndexDao;
import com.bnpp.assessment.dao.IndexDaoImpl;
import com.bnpp.assessment.models.Index;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.ui.panels.MarketWatchPanel;
import com.bnpp.assessment.util.RefreshScheduler;

/**
 * Controller for the {@link MarketWatchPanel}.
 * * The only UI action that this controller handles now is **"View Option Chain"**.
 * Trading (CE/PE) is performed **exclusively** in the OptionChain view, which
 * is handled by {@link OptionChainController}.
 */
public class MarketWatchController {

    private final MarketWatchPanel marketPanel;
    private final IndexDao indexDao = new IndexDaoImpl();
    private final User currentUser;
    private final Consumer<Index> openOptionChainCallback;

    private OptionChainController currentOptionChainController;

    public MarketWatchController(MarketWatchPanel marketPanel, Consumer<Index> openOptionChainCallback, User currentUser) {
        this.marketPanel = marketPanel;
        this.openOptionChainCallback = openOptionChainCallback;
        this.currentUser = currentUser;

        // 1. Register the refresh method with the global scheduler
        RefreshScheduler.getInstance().register(this::loadIndices);
        attachListeners();
    }

    /**
     * Load / refresh the Spot price table - called by RefreshScheduler.
     */
    private void loadIndices() {
        DefaultTableModel model = marketPanel.getSpotModel();
        List<Index> indices = indexDao.findAll();

        int selView = marketPanel.getSpotTable().getSelectedRow();

        model.setRowCount(0);
        for (Index idx : indices) {
            model.addRow(new Object[]{ idx.getSymbol(), idx.getLastPrice() });
        }

        if (selView != -1 && selView < marketPanel.getSpotTable().getRowCount()) {
            marketPanel.getSpotTable().setRowSelectionInterval(selView, selView);
        }

        marketPanel.updateRowCount();
    }

    /* ----------------------------------------------------------------------
     * UI LISTENERS (only "View Option Chain" now)
     * ---------------------------------------------------------------------- */
    private void attachListeners() {
        // Context menu -> View Option Chain
        marketPanel.getViewOptionChainItem().addActionListener(e -> handleViewOptionChain());

        // Back button on the option chain panel (inside MarketWatchPanel)
        marketPanel.getOptionChainPanel()
                   .getBackButton()
                   .addActionListener(e -> marketPanel.showSpotView());
    }

    /**
     * Context menu handler - fires the callback that shows the option chain card.
     */
    private void handleViewOptionChain() {
        int viewRow = marketPanel.getSpotTable().getSelectedRow();
        if (viewRow == -1) return;

        int modelRow = marketPanel.getSpotTable().convertRowIndexToModel(viewRow);
        Index selected = indexDao.findAll().get(modelRow);

        showOptionChain(selected);
    }

    /**
     * Open the option chain for the supplied index and rebuild the trade listener state.
     */
    public void showOptionChain(Index selected) {

        // Stop any previous chain refresh timer
        if (currentOptionChainController != null) {
            currentOptionChainController.stopAutoRefresh();
        }

        // Create a fresh controller for the newly selected index
        currentOptionChainController = new OptionChainController(
            marketPanel.getOptionChainPanel(),
            selected,
            currentUser
        );
        currentOptionChainController.loadOptionChain(); // immediate load

        // Tell the outer UI (MainAppPanel) to show the Option Chain card
        openOptionChainCallback.accept(selected);
    }

    /**
     * Called from the "Back" button in the Option Chain panel.
     */
    public void goBackToSpot() {
        if (currentOptionChainController != null) {
            currentOptionChainController.stopAutoRefresh();
            currentOptionChainController = null;
        }
    }
}