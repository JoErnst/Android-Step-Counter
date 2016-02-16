package uk.ac.ox.ibme.androidstepcounter.algos;

import uk.ac.ox.ibme.androidstepcounter.IStepCounter;

/**
 * This step counter was adapted from:
 * https://github.com/appquest-arcanum/StepCounter/blob/dev/app/src/main/java/com/appquest/arcanum/stepcounter/StepCounter.java
 */
public class ArcanumStepCounter implements IStepCounter {

    private static final int LONG = 1000;
    private static final int SHORT = 50;

    private boolean accelerating = false;
    private int counter;

    private RingBuffer shortBuffer = new RingBuffer(SHORT);
    private RingBuffer longBuffer = new RingBuffer(LONG);

    @Override
    public int getSteps(long timestamp, float accx, float accy, float accz) {
        float magnitude = (float) (Math.pow(accx, 2) + Math.pow(accy, 2) + Math.pow(accz, 2));

        shortBuffer.put(magnitude);
        longBuffer.put(magnitude);

        float shortAverage = shortBuffer.getAverage();
        float longAverage = longBuffer.getAverage();

        if (!accelerating && (shortAverage > longAverage * 1.1)) {
            accelerating = true;
            counter++;
        }

        if ((accelerating && shortAverage < longAverage * 0.9)) {
            accelerating = false;
        }

        return counter;
    }

    @Override
    public void reset() {
        counter = 0;
        shortBuffer = new RingBuffer(SHORT);
        longBuffer = new RingBuffer(LONG);
    }

    private class RingBuffer {
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
    }
}
