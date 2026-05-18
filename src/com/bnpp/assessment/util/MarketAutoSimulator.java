package com.bnpp.assessment.util;

import com.bnpp.assessment.models.Index;
import com.bnpp.assessment.service.MarketSimulationService;
import com.bnpp.assessment.service.MarketSimulationServiceImpl;
import com.bnpp.assessment.dao.IndexDao;
import com.bnpp.assessment.dao.IndexDaoImpl;

import java.util.List;

public class MarketAutoSimulator {

    private static final
    MarketSimulationService service =
            new MarketSimulationServiceImpl();

    private static final
    IndexDao indexDao =
            new IndexDaoImpl();

    public static void start() {

        Thread thread = new Thread(() -> {

            while (true) {

                try {

                    List<Index> indices =
                            indexDao.findAll();

                    for (Index index : indices) {

                        double move =
                                SimulationEngine
                                        .generateMove();

                        service.simulateMarketMove(
                                index.getId(),
                                move
                        );

                        System.out.println(
                                index.getSymbol()
                                + " moved by "
                                + move
                        );
                    }

                    Thread.sleep(2000);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}