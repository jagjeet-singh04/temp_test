package com.bnpp.assessment.service;

import com.bnpp.assessment.dao.IndexDao;
import com.bnpp.assessment.dao.IndexDaoImpl;
import com.bnpp.assessment.dao.OptionStrikeDao;
import com.bnpp.assessment.dao.OptionStrikeDaoImpl;
import com.bnpp.assessment.models.Index;
import com.bnpp.assessment.models.OptionChain;
import com.bnpp.assessment.models.OptionDelta;
import com.bnpp.assessment.models.OptionStrike;
import com.bnpp.assessment.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Random;

public class MarketSimulationServiceImpl implements MarketSimulationService {

    private final Random random = new Random();

    @Override
    public List<OptionStrike> getStrikesByIndex(Long indexId) {
        OptionStrikeDao dao = new OptionStrikeDaoImpl();
        return dao.findByIndexId(indexId);
    }

    @Override
    public void simulateMarketMove(Long indexId, double move) {
        // Use one session for all updates
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            // Fetch strikes (they will be managed inside this session)
            List<OptionStrike> strikes = session.createQuery(
                    "FROM OptionStrike s JOIN FETCH s.optionChain WHERE s.optionChain.index.id = :idxId")
                .setParameter("idxId", indexId)
                .list();

            if (strikes.isEmpty()) {
                tx.commit();
                return;
            }

            OptionChain chain = strikes.get(0).getOptionChain();
            Index index = chain.getIndex();          // the actual Index entity

            double oldSpot = chain.getSpotPrice();
            double newSpot = oldSpot + move;

            // 1. Update the chain's spot price
            chain.setSpotPrice(newSpot);

            // 2. Update the index's last price (this is what the UI shows)
            index.setLastPrice(newSpot);

            // Persist both (they are already managed, so just flush)
            session.update(chain);    // or merge if detached, but inside session it's managed
            session.update(index);

            // 3. Update option premiums using delta
            for (OptionStrike strike : strikes) {
                OptionDelta delta = strike.getOptionDelta();
                double callDelta = (delta != null) ? delta.getCallDelta() : 0.5;
                double putDelta  = (delta != null) ? delta.getPutDelta()  : 0.5;

                // Realistic noise (Gaussian, mean 0, std dev 0.5)
                double noise = random.nextGaussian() * 0.5;

                double newCE = strike.getCallPremium() + (callDelta * move) + noise;
                double newPE = strike.getPutPremium() - (putDelta * move) + noise;

                newCE = Math.max(1.0, Math.round(newCE * 100.0) / 100.0);
                newPE = Math.max(1.0, Math.round(newPE * 100.0) / 100.0);

                strike.setCallPremium(newCE);
                strike.setPutPremium(newPE);
                session.update(strike);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}