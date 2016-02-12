package uk.ac.ox.ibme.androidstepcounter.uk.ac.ox.ibme.androidstepcounter.algos;

import uk.ac.ox.ibme.androidstepcounter.IStepCounter;

/**
 * A dummy step counter, to show the concept
 * Created by Dario Salvi on 19/01/2016.
 */
public class DummyStepCounter implements IStepCounter {

    private int steps = 0;
    private long laststepts = -1;

    @Override
    public int getSteps(long timestamp, float accx, float accy, float accz) {
        if(laststepts == -1) laststepts = timestamp;
        if((timestamp-laststepts) > 1e+9){
            laststepts = timestamp;
            steps ++;
        }
        return steps;
    }

    @Override
    public void reset() {
        steps = 0;
        laststepts = -1;
    }
}
