package uk.ac.ox.ibme.androidstepcounter.algos;

import android.hardware.SensorManager;

import uk.ac.ox.ibme.androidstepcounter.IStepCounter;

/**
 * A very simple step counter that uses a threshold.
 * Needs to be improved!!!
 * Created by Dario Salvi
 */
public class ThresholdStepCounter implements IStepCounter {

    private int counter = 0;
    private double lastmodule = 0;

    @Override
    public int getSteps(long timestamp, float accx, float accy, float accz) {
        //get module
        double module = Math.sqrt(Math.pow(accx, 2)+Math.pow(accy, 2)+Math.pow(accz, 2));
        float thre = (SensorManager.STANDARD_GRAVITY * 1.3F);
        if(module > thre && lastmodule <thre){
            counter ++;
        }
        lastmodule = module;

        return counter;
    }

    @Override
    public void reset() {
        counter = 0;
        lastmodule = 0;
    }
}
