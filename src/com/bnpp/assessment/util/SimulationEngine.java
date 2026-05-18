package com.bnpp.assessment.util;

import java.util.Random;

public class SimulationEngine {

    private static final Random random =
            new Random();

    public static double generateMove() {

        // Generates random number between
        // -15 and +15

        return random.nextInt(31) - 15;
    }
}