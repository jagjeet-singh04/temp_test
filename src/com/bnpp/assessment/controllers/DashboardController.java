package com.bnpp.assessment.controllers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import com.bnpp.assessment.dto.PositionRow;
import com.bnpp.assessment.models.Index;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.models.Wallet;
import com.bnpp.assessment.dao.IndexDao;
import com.bnpp.assessment.dao.IndexDaoImpl;
import com.bnpp.assessment.service.PositionService;
import com.bnpp.assessment.service.PositionServiceImpl;
import com.bnpp.assessment.service.WalletService;
import com.bnpp.assessment.service.WalletServiceImpl;
import com.bnpp.assessment.ui.panels.DashboardPanel;

public class DashboardController {

	private final DashboardPanel panel;
	private final User user;
	private final IndexDao indexDao = new IndexDaoImpl();
	private final WalletService walletService = new WalletServiceImpl();
	private final PositionService positionService = new PositionServiceImpl();

	private List<Index> currentIndices = Collections.emptyList();
	private Timer timer;

	public DashboardController(DashboardPanel panel, User user) {
		this.panel = panel;
		this.user = user;

		attachListeners();
		refresh();
		startAutoRefresh();
	}

	public void refresh() {
		loadWalletSummary();
		loadMarketWatch();
		loadPositions();
	}

	private void startAutoRefresh() {
		timer = new Timer(5000, e -> refresh());
		timer.start();
	}

	private void loadWalletSummary() {
		Wallet wallet = walletService.getWallet(user.getUserId());

		double unrealised = positionService.getPositions(user.getUserId())
				.stream()
				.mapToDouble(PositionRow::getPnl)
				.sum();

		wallet.setUnrealisedPnl(unrealised);

		panel.getBalanceLabel().setText(String.format("Balance : ₹ %,.2f", wallet.getCashBalance()));
		panel.getRealisedLabel().setText(String.format("Realized P&L : ₹ %,.2f", wallet.getRealisedPnl()));
		panel.getUnrealisedLabel().setText(String.format("Unrealized P&L : ₹ %,.2f", wallet.getUnrealisedPnl()));
	}

	private void loadMarketWatch() {
		currentIndices = indexDao.findAll();

		DefaultTableModel model = panel.getMarketModel();
		model.setRowCount(0);

		for (Index idx : currentIndices) {
			model.addRow(new Object[] {
				idx.getSymbol(),
				String.format("%.2f", idx.getLastPrice())
			});
		}
	}

	private void loadPositions() {
		List<PositionRow> rows = positionService.getPositions(user.getUserId());
		DefaultTableModel model = panel.getPositionModel();

		model.setRowCount(0);
		for (PositionRow row : rows) {
			model.addRow(new Object[] {
				row.getIndexSymbol(),
				row.getOptionType(),
				row.getStrike(),
				row.getTradeType(),
				row.getQuantity(),
				String.format("%.2f", row.getEntryPremium()),
				String.format("%.2f", row.getCurrentPremium()),
				String.format("%.2f", row.getPnl())
			});
		}
	}

	private void attachListeners() {
		panel.getViewOptionChainMenuItem().addActionListener(e -> openSelectedOptionChain());

		panel.getMarketTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				selectRowForPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				selectRowForPopup(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					openSelectedOptionChain();
				}
			}

			private void selectRowForPopup(MouseEvent e) {
				if (!e.isPopupTrigger()) {
					return;
				}

				int row = panel.getMarketTable().rowAtPoint(e.getPoint());
				if (row >= 0) {
					panel.getMarketTable().setRowSelectionInterval(row, row);
				}
			}
		});
	}

	private void openSelectedOptionChain() {
		int viewRow = panel.getMarketTable().getSelectedRow();
		if (viewRow < 0) {
			return;
		}

		int modelRow = panel.getMarketTable().convertRowIndexToModel(viewRow);
		if (modelRow < 0 || modelRow >= currentIndices.size()) {
			return;
		}

		Index selected = currentIndices.get(modelRow);
		panel.fireOpenOptionChainEvent(selected.getSymbol());
	}
}
