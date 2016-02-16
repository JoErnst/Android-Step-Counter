package uk.ac.ox.ibme.androidstepcounter.algos;

import android.hardware.SensorManager;

import uk.ac.ox.ibme.androidstepcounter.IStepCounter;

/**
 * An implementation of the Namje step counter.
 * This step counter using a evolving threshold to estimate step counts.
 * At the moment the step duration is based on samples and 100 Hz, we could redesign this
 * to take into account time passed rather than samples in order to take into account
 * frequency variations in the data collection.
 * Created by Johanna Ernst
 */
public class NamjeStepCounterWalk implements IStepCounter {
    
    private int clock = 1; // counter to reduce threshold requirements depending on length of step
    private int step = 0; // step counter
    private double alpha = 0.0003 * SensorManager.STANDARD_GRAVITY; // theshold influencer
    private double beta = 0.35 * SensorManager.STANDARD_GRAVITY; // theshold influencer
    private double max_val = 0; // current maximum vector magnitude value
    private double step_dur = 0; // counter to identify length of step
    private double std_thresh = 0.06 * SensorManager.STANDARD_GRAVITY; // threshold for walk detection
    private int walk = 0;
    
    // Define limits for step length for 100 Hz signal
    private int step_max = 200;
    private int step_min = 20;
    
    // Create buffers to store vec_mag information over time
    private static final int len = 81;
    private RingBuffer vmag_buffer = new RingBuffer(len);
    
    @Override
    public int getSteps(long timestamp, float accx, float accy, float accz) {
        float vec_mag = (float) Math.sqrt(Math.pow(accx, 2)+Math.pow(accy, 2)+Math.pow(accz, 2));
        float thresh = (float) (alpha/clock + beta);
        
        // Store vector magnitude in buffer
        vmag_buffer.put(vec_mag);
        
        // Check whether std indicates person walkig
        float vmag_std = vmag_buffer.getStd();
        if (vmag_std >= std_thresh){
            walk = 1;
        }
        else{
            walk = 0;
        }
        
        // This loops checks whether there has been step like behavior, and if so, whether the
        // step duration is within the 'normal' step limits defined above
        if (walk == 1){
            if ((max_val - vec_mag) >= thresh){
                step ++;
                if (step_dur > step_max){
                    step = step-1;
                    step_dur = 0;
                }else if (step_dur < step_min){
                    step = step-1;
                }else{
                    clock = 0;
                    max_val = vec_mag;
                }
            } else if (vec_mag > max_val){
                max_val = vec_mag;
            }
            step_dur ++;
            clock ++;
        }
        
        return step;
    }
    
    @Override
    public void reset() {
        clock = 1;
        step = 0;
        max_val = 0;
        step_dur = 0;
        RingBuffer vmag_buffer = new RingBuffer(len);
    }

}
