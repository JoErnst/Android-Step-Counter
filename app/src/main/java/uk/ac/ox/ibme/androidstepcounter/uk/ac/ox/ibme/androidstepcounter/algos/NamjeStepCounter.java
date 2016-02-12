package uk.ac.ox.ibme.androidstepcounter.uk.ac.ox.ibme.androidstepcounter.algos;

import java.util.ArrayList;
import java.util.Queue;

import uk.ac.ox.ibme.androidstepcounter.IStepCounter;

/**
 * Created by engs1397 on 21/01/2016.
 */
public class NamjeStepCounter implements IStepCounter {
    float[] array_of_floats = new float[100];
    ArrayList<Float> array_list_of_floats = new ArrayList<>();


    @Override
    public int getSteps(long timestamp, float accx, float accy, float accz) {
        array_of_floats[1] = 89;
        int l = array_of_floats.length;

        array_list_of_floats.add(89F);
        float f = array_list_of_floats.get(1);
        array_list_of_floats.set(1, 89F);
        array_list_of_floats.remove(1);

        return 0;
    }

    @Override
    public void reset() {

    }
}
