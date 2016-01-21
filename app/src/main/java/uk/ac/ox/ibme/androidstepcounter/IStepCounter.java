package uk.ac.ox.ibme.androidstepcounter;

/**
 * Interface that has to be implemented by all step counters
 * Created by Dario Salvi on 19/01/2016.
 */
public interface IStepCounter {

    /**
     * Returns the current number of steps measured
     * @param timestamp time in nanosecond
     * @param accx
     * @param accy
     * @param accz
     * @return the number of steps
     */
    int getSteps(long timestamp, float accx, float accy, float accz);

    /**
     * Resets the algorithm, steps will start from 0
     */
    void reset();
}

