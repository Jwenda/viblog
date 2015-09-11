package cz.ondrejbilek.viblog;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Main extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGravity;
    private Sensor mAcceleration;

    private TextView vibX, vibY, vibZ, accX, accY, accZ, graX, graY, graZ;
    private ToggleButton tgl;
    private CheckBox save;

    private OutputStreamWriter ostream;
    private FileOutputStream fostream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepare();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            vibX.setText(Float.toString(sensorEvent.values[0]));
            vibY.setText(Float.toString(sensorEvent.values[1]));
            vibZ.setText(Float.toString(sensorEvent.values[2]));
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
            graX.setText(Float.toString(sensorEvent.values[0]));
            graY.setText(Float.toString(sensorEvent.values[1]));
            graZ.setText(Float.toString(sensorEvent.values[2]));
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            accX.setText(Float.toString(sensorEvent.values[0]));
            accY.setText(Float.toString(sensorEvent.values[1]));
            accZ.setText(Float.toString(sensorEvent.values[2]));
            if (save.isChecked())
                writeLine(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void prepare() {
        prepareLayout();
        prepareSensors();
    }

    public void writeLine(SensorEvent sensor){
        String x = Float.toString(sensor.values[0]);
        String y = Float.toString(sensor.values[1]);
        String z = Float.toString(sensor.values[2]);

        try {
            ostream.write(x + ";" + y + ";" + z + System.getProperty("line.separator"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareFile(){
        long timestamp = System.currentTimeMillis();
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Long.toString(timestamp) + ".txt");
            file.createNewFile();
            fostream = new FileOutputStream(file);
            ostream = new OutputStreamWriter(fostream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeFile(){
        try {
            ostream.close();
            fostream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareSensors(){
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public void prepareLayout(){
        vibX = (TextView)findViewById(R.id.vibX);
        vibY = (TextView)findViewById(R.id.vibY);
        vibZ = (TextView)findViewById(R.id.vibZ);
        graX = (TextView)findViewById(R.id.graX);
        graY = (TextView)findViewById(R.id.graY);
        graZ = (TextView)findViewById(R.id.graZ);
        accX = (TextView)findViewById(R.id.accX);
        accY = (TextView)findViewById(R.id.accY);
        accZ = (TextView)findViewById(R.id.accZ);

        tgl = (ToggleButton)findViewById(R.id.start);
        save = (CheckBox)findViewById(R.id.log);
    }

    public void tglClick(View v){
        if(tgl.isChecked()) {
            if (save.isChecked())
                prepareFile();

            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, mAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
            save.setEnabled(false);
        } else {
            if (save.isChecked())
                closeFile();

            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mGravity);
            mSensorManager.unregisterListener(this, mAcceleration);
            save.setEnabled(true);
        }
    }
}
