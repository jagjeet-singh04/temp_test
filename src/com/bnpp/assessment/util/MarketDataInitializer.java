package com.bnpp.assessment.util;

import com.bnpp.assessment.models.Index;
import com.bnpp.assessment.models.OptionChain;
import com.bnpp.assessment.models.OptionDelta;
import com.bnpp.assessment.models.OptionStrike;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class MarketDataInitializer {

    public static void initialize() {

        Session session =
                HibernateUtil
                        .getSessionFactory()
                        .openSession();

        Transaction tx =
                session.beginTransaction();

        // =========================
        // CHECK IF DATA EXISTS
        // =========================

        Long count = (Long) session.createQuery(
                "SELECT COUNT(i) FROM Index i"
                
        ).uniqueResult();

        if (count != null && count > 0) {

            session.close();

            return;
        }

        // =========================
        // CREATE INDICES
        // =========================

        createIndex(
                session,
                "NIFTY50",
                15000.0
        );

        createIndex(
                session,
                "BANKNIFTY",
                40000.0
        );

        createIndex(
                session,
                "SENSEX",
                70000.0
        );

        tx.commit();

        session.close();

        System.out.println(
                "Market data initialized successfully."
        );
    }

    // =========================
    // CREATE INDEX
    // =========================

    private static void createIndex(
            Session session,
            String symbol,
            double spotPrice
    ) {

        // =========================
        // INDEX
        // =========================

        Index index = new Index();

        index.setSymbol(symbol);

        index.setLastPrice(spotPrice);

        session.save(index);

        // =========================
        // OPTION CHAIN
        // =========================

        OptionChain chain =
                new OptionChain();

        chain.setIndex(index);

        chain.setSpotPrice(spotPrice);

        session.save(chain);

        // =========================
        // STRIKES
        // =========================

        double[] strikes = {

                spotPrice - 150,
                spotPrice - 100,
                spotPrice - 50,

                spotPrice,

                spotPrice + 50,
                spotPrice + 100,
                spotPrice + 150
        };

        for (double strikePrice : strikes) {

            OptionStrike strike =
                    new OptionStrike();

            strike.setOptionChain(chain);

            strike.setStrikePrice(
                    strikePrice
            );

            // =========================
            // PREMIUM LOGIC
            // =========================

            double distance =
                    Math.abs(
                            strikePrice
                                    - spotPrice
                    );

            double premium =
                    Math.max(
                            100 - (distance / 10),
                            10
                    );

            strike.setCallPremium(
                    premium
            );

            strike.setPutPremium(
                    premium
            );

            session.save(strike);

            // =========================
            // DELTA
            // =========================

            OptionDelta delta =
                    new OptionDelta();

            delta.setOptionStrike(strike);

            // ATM highest delta
            if (distance == 0) {

                delta.setCallDelta(0.5);

                delta.setPutDelta(0.5);

            }

            // NEAR ATM
            else if (distance == 50) {

                delta.setCallDelta(0.4);

                delta.setPutDelta(0.4);

            }

            // FAR
            else {

                delta.setCallDelta(0.2);

                delta.setPutDelta(0.2);
            }

            session.save(delta);

            strike.setOptionDelta(delta);
        }
    }
}