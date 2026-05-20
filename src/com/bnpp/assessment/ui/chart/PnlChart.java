package com.bnpp.assessment.ui.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.bnpp.assessment.models.Trade;

/**
 * * Simple line chart that draws **cumulative P&L** over time.
 */
public final class PnlChart {

    private final TimeSeries cumSeries = new TimeSeries("Cumulative-PnL");
    private final ChartPanel chartPanel;

    public PnlChart() {
        TimeSeriesCollection dataset = new TimeSeriesCollection(cumSeries);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Performance",       // title (will be hidden by the dialog)
                "Time",              // X-axis
                "PnL (₹)",           // Y-axis
                dataset,
                false,               // legend?
                true,                // tooltips?
                false                // URLs?
        );

        XYPlot plot = chart.getXYPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // show hour:minute:second on the bottom axis
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
        axis.setTickMarkPaint(Color.DARK_GRAY);

        chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    /**
     * Build the whole series from a *closed* trade list (used on startup).
     */
    public void setTrades(List<? extends Trade> closedTrades) {
        cumSeries.clear();

        // 1. Sort the trades by their closing time
        List<? extends Trade> sorted = closedTrades.stream()
                .sorted(Comparator.comparing(com.bnpp.assessment.models.Trade::getTradeTime))
                .collect(Collectors.toList()); // Java 16+; replace with .toList() for older JDKs

        // 2. Accumulate the P&L and add a point for each trade
        double cumulative = 0.0;
        for (Trade t : sorted) {
            Double pnl = t.getPremium(); // Assuming target mapping or update getter wrapper handles logic
            if (pnl == null) {
                pnl = 0.0; // t.getPremium() calculation block placeholder fallbacks
            }
            cumulative += pnl;
            cumSeries.addOrUpdate(new Millisecond(t.getTradeTime()), cumulative);
        }
    }

    /**
     * Add a *single* newly closed trade - updates the series in place.
     */
    public void addTrade(com.bnpp.assessment.models.Trade trade) {
        if (trade == null || trade.getPremium() == null) return;

        double last = cumSeries.isEmpty() ? 0.0
                : cumSeries.getDataItem(cumSeries.getItemCount() - 1).getValue().doubleValue();

        cumSeries.addOrUpdate(new Millisecond(trade.getTradeTime()), last + trade.getPremium());
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }
}