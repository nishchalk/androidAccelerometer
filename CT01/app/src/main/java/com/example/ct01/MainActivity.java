package com.example.ct01;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Arrays;
import java.util.List;

public class MainActivity<axis> extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor ambientTemperature, light, pressure, relativeHumidity, temperature;
    private Sensor acceleromter;

    private LineChart chart;
    private Thread thread;
    private boolean plotData = true;

    public int DELAY = 100000; // to set delay of 1 second
    public float ax, ay, az;

    TextView axText, ayText, azText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        axText = findViewById(R.id.axText);
        ayText = findViewById(R.id.ayText);
        azText = findViewById(R.id.azText);

        chart = (LineChart) findViewById(R.id.chart1);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        acceleromter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        // check other enviromental sensors but they didn't work. Light Sensor is working but I am not sure what exactly is measured.
        ambientTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        relativeHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        temperature = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);


        if(acceleromter != null){
            sensorManager.registerListener(this, acceleromter, DELAY);
        }

        if(ambientTemperature != null){
            sensorManager.registerListener(this, ambientTemperature, sensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "ambientTemp works!");
        }else{
            Log.d(TAG, "ambientTemp does NOT works!");
        }
        if(light != null){
            sensorManager.registerListener(this, ambientTemperature, sensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "light works!");
        }else{
            Log.d(TAG, "light does NOT works!");
        }
        if(pressure != null){
            sensorManager.registerListener(this, ambientTemperature, sensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "pressure works!");
        }else{
            Log.d(TAG, "pressure does NOT works!");
        }
        if(relativeHumidity != null){
            sensorManager.registerListener(this, ambientTemperature, sensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "hum works!");
        }else{
            Log.d(TAG, "hum does NOT works!");
        }
        if(temperature != null){
            sensorManager.registerListener(this, ambientTemperature, sensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "temperature works!");
        }else{
            Log.d(TAG, "temperature does NOT works!");
        }

        chart.getDescription().setEnabled(true);
        chart.getDescription().setText("Real Time Acceleration Plot");

        YAxis leftaxis = chart.getAxisLeft();
        leftaxis.setAxisMaximum(20f);
        leftaxis.setAxisMinimum(-20f);

        YAxis rightaxis =chart.getAxisRight();
        rightaxis.setEnabled(false);

        chart.setDrawBorders(true);

        LineData data = new LineData();
        chart.setData(data);
        chart.invalidate();
        feedMultiple();


    }

    private final int[] colors = new int[] {
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
            ColorTemplate.VORDIPLOM_COLORS[2]
    };

    List<String> axis = Arrays.asList("X-axis", "Y-axis", "Z-axis");

    private void addEntry(SensorEvent sensorEvent){
        LineData data = chart.getData();

        if(data != null){
            for(int i=0; i<3; i++){
                ILineDataSet set = data.getDataSetByIndex(i);

                if (set == null){
                    set = createSet(i);
                    data.addDataSet(set);
                }

                data.addEntry(new Entry(set.getEntryCount(), sensorEvent.values[i]), i);
                data.notifyDataChanged();

                // let the chart know it's data has changed
                chart.notifyDataSetChanged();

                // limit the number of visible entries
                chart.setVisibleXRangeMaximum(150);
                // mChart.setVisibleYRange(30, AxisDependency.LEFT);

                // move to the latest entry
                chart.moveViewToX(data.getEntryCount());
            }
        }
    }

    private LineDataSet createSet(int i) {

        LineDataSet set = new LineDataSet(null, axis.get(i));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);

        int color = colors[i % colors.length];
        set.setColor(color);
        set.setCircleColor(color);

        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            ax = sensorEvent.values[0];
            ay = sensorEvent.values[1];
            az = sensorEvent.values[2];

            axText.setText(String.format("%.2f m/s^2", ax));
            ayText.setText(String.format("%.2f m/s^2", ay));
            azText.setText(String.format("%.2f m/s^2", az));

            if(plotData){
                addEntry(sensorEvent);
                plotData = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        sensorManager.unregisterListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, acceleromter, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDestroy() {
        sensorManager.unregisterListener(MainActivity.this);
        thread.interrupt();
        super.onDestroy();
    }

}
