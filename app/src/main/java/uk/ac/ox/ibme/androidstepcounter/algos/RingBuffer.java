package uk.ac.ox.ibme.androidstepcounter.algos;

/**
 * Created by engs1397 on 16/02/2016.
 */
public class RingBuffer {
    private float[] buffer;
    private int capacity;
    private int current = 0;
    private int count = 0;

    public int getCount() {
        return count;
    }

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        buffer = new float[capacity];
    }

    public void put(float f) {
        buffer[current] = f;
        current++;
        count++;
        current = current % capacity;
    }

    public float getAverage() {
        float avg = 0;
        for (float f : buffer) {
            avg += f;
        }
        if (count > capacity) {
            return avg / capacity;
        } else {
            return avg / count;
        }
    }

    public float getStd() {
        float std = 0;
        float sum = 0;
        float avg = 0;
        float sum_var = 0;
        float var = 0;

        // find average
        for (float f : buffer) {
            sum += f;
        }
        if (count > capacity) {
            avg = sum / capacity;
        } else {
            avg = sum / count;
        }
        // find standard deviation
        for (float f : buffer) {
            sum_var += Math.pow((f-avg), 2);
        }

        if (count > capacity) {
            var = avg / capacity;
        } else {
            var =  avg / count;
        }
        return (float) Math.sqrt(var);
    }
}
