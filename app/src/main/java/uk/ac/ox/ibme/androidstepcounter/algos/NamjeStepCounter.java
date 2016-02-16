package uk.ac.ox.ibme.androidstepcounter.algos;

import android.hardware.SensorManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;

import uk.ac.ox.ibme.androidstepcounter.IStepCounter;

/**
 * An implementation of the Namje step counter.
 * This step counter using a evolving threshold to estimate step counts.
 * At the moment the step duration is based on samples and 100 Hz, we could redesign this
 * to take into account time passed rather than samples in order to take into account
 * frequency variations in the data collection.
 * Created by Johanna Ernst
 */
public class NamjeStepCounter implements IStepCounter {

    private BufferedWriter log;

    private int clock = 1;
    private int step = 0;
    private double alpha = 0.0003 * SensorManager.STANDARD_GRAVITY ;
    private double beta = 0.65 * SensorManager.STANDARD_GRAVITY ;
    private double max_val = 0;
    private double step_dur = 0;

    // Define limits for step length for 100 Hz signal
    private int step_max = 200;
    private int step_min = 20;

    // Create buffers to store vec_mag information over time
    //private static final int LONG = 1000;
    //private static final int SHORT = 50;
    //private RingBuffer shortBuffer = new RingBuffer(SHORT);
    //private RingBuffer longBuffer = new RingBuffer(LONG);

    public NamjeStepCounter(BufferedWriter log){
        this.log = log;
    }

    @Override
    public int getSteps(long timestamp, float accx, float accy, float accz)  {
        double vec_mag = Math.sqrt(Math.pow(accx, 2)+Math.pow(accy, 2)+Math.pow(accz, 2));
        double thresh = alpha/clock + beta;

        // This loops checks whether there has been step like behavior, and if so, whether the
        // step duration is within the 'normal' step limits defined above
        if ((max_val - vec_mag) >= thresh){
            step ++;
            if (step_dur > step_max){
                step = step-1;
                step_dur = 0;
            }else if (step_dur < step_min){
                step = step-1;
            }else{
                clock = 1;
                max_val = vec_mag;
            }
        } else if (vec_mag > max_val){
            max_val = vec_mag;
        }
        try {
            log.write(System.currentTimeMillis() + ", STEP DETECTED" + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        step_dur ++;
        clock ++;

        return step;
    }

    @Override
    public void reset() {
        clock = 1;
        step = 0;
        max_val = 0;
        step_dur = 0;
    }
}

