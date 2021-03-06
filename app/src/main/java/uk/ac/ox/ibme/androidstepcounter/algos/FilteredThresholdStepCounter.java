package uk.ac.ox.ibme.androidstepcounter.algos;

import android.hardware.SensorManager;

import uk.ac.ox.ibme.androidstepcounter.IStepCounter;

/**
 * Filtered threshold acceleration.
 * Adapted from: https://github.com/gulizseray/StepCounter
 *
 */
public class FilteredThresholdStepCounter implements IStepCounter {

    private static final float ALPHA = (float) 0.7;
    private static final float THRESHOLD = (SensorManager.STANDARD_GRAVITY * 1.15F);

    float lastModule = 0;
    private long lastStepCountTime = 0;

    private int counter = 0;

    @Override
    public int getSteps(long timestamp, float accx, float accy, float accz) {
        long currTime = System.currentTimeMillis();

        float module = (float)(Math.sqrt(Math.pow(accx, 2) + Math.pow(accy, 2) + Math.pow(accz, 2)));

        // Low-pass filter to remove noise
        module = (1-ALPHA) * lastModule + ALPHA * module;

        // Peak is substantial enough to be correlated to a step
        // && The condition must not be sustained (an inversion in trend)
        // && There needs to be at least 300ms between two peaks, otherwise it isn't a step.
        if((module > THRESHOLD) && (lastModule < THRESHOLD) && (currTime - lastStepCountTime > 300)){
            counter++;
            lastStepCountTime = currTime;
        }
        lastModule = module;

        return counter;
    }

    @Override
    public void reset() {
        lastModule = 0;
        lastStepCountTime = 0;
        counter = 0;
    }
}
