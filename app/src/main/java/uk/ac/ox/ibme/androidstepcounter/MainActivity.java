package uk.ac.ox.ibme.androidstepcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import uk.ac.ox.ibme.androidstepcounter.algos.NamjeStepCounter;
import uk.ac.ox.ibme.androidstepcounter.algos.NamjeStepCounterWalk;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Button button;
    private TextView text;
    private boolean measuring = false;
    private Sensor accelerometer;
    BufferedWriter sensorlog;
    BufferedWriter algolog;
    private IStepCounter stepcounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) this.findViewById(R.id.largetext);
        text.setText("stopped");

        button = (Button) this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!measuring){
                    startListener();
                    button.setText("Stop");
                } else {
                    stopListener();
                    text.setText("stopped");
                    button.setText("Start");

                    try {
                        sensorlog.flush();
                        algolog.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        File root = Environment.getExternalStorageDirectory();
        try {
            File f = new File(root, "sensorlog.csv");
            f.createNewFile();
            sensorlog = new BufferedWriter(new FileWriter(f));
            f = new File(root, "algolog.csv");
            f.createNewFile();
            algolog = new BufferedWriter(new FileWriter(f));

            stepcounter = new NamjeStepCounter(algolog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startListener(){
        // Get accelerometer from sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        // If found, then register as listener
        if ((list != null) && (list.size() > 0)) {
            accelerometer = list.get(0);//get the first one
            boolean status = sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            if(!status) {
                text.setText("cannot start sensor");
            }
        } else {
            text.setText("no sensor found");
        }
    }

    private void stopListener(){
        measuring = false;
        sensorManager.unregisterListener(this);
        stepcounter.reset();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // Only look at accelerometers events
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        if(!measuring) measuring = true;

        int steps = stepcounter.getSteps(event.timestamp, event.values[0], event.values[1], event.values[2]);
        String line = System.currentTimeMillis()+ ", " + event.values[0]+ ", " + event.values[1] + ", " + event.values[2] + "\n";
        try {
            sensorlog.write(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
        text.setText("" + steps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop(){
        super.onStop();

        try {
            sensorlog.flush();
            algolog.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
