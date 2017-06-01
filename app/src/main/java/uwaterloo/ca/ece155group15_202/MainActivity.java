package uwaterloo.ca.ece155group15_202;


import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.uwaterloo.sensortoy.LineGraphView;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    LinearLayout l1;            //layout used
    LineGraphView graph;        //graph displaying output
    ArrayList<String> readingOutput = new ArrayList <String>();         //arraylist of string accelerometer readings
    File file = null;           //initialize file to write to
    PrintWriter prt = null;
    Button myButton;            //myButton records accelerometer readings
    Button resetButton;
    TextView arn = null;        //accelerometer record number
    TextView an = null;         //accelerometer current number

    Sensor accelerometer;
    SensorManager sensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l1 = (LinearLayout)findViewById(R.id.layout);
        l1.setOrientation(LinearLayout.VERTICAL);

        //create new instance of reset button
        resetButton = new Button (getApplicationContext());

        l1.addView(resetButton);

        //set up textviews for the various sensor readings
        //some default values for test to see if getting readings

        TextView accel = createLabel("The Accelerometer Reading is: ");
        an = createLabel("0.1");
        TextView accelRecord = createLabel("The Record-High Accelerometer Reading is: ");
        arn = createLabel("1.1");

        //display the accelerometer readings
        graph = new LineGraphView(getApplicationContext(),100, Arrays.asList("x","y","x"));
        l1.addView(graph);
        graph.setVisibility(View.VISIBLE);

        //******************************SENSOR STUFF***********************************


        //sensor, sensor manager, their respective listeners
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        AccelerometerEventListener ael = new AccelerometerEventListener(an,arn,graph,readingOutput);

        sensorManager.registerListener(ael,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);


        //record data button
        myButton = new Button (getApplicationContext());
        myButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                recordData(prt,readingOutput);
            }
        });


        l1.addView(myButton);
        myButton.setText("write csv values");

        MyOnClickListener resetListener = new MyOnClickListener(ael);
        resetButton.setOnClickListener(resetListener);


        resetButton.setText("Reset Record Maximums");

        resetButton.setBackgroundColor(Color.BLUE);
        resetButton.setTextColor(Color.WHITE);
    }


    public TextView createLabel (String labelName){
        TextView tv1 = new TextView(getApplicationContext());
        tv1.setText(labelName);
        tv1.setTextColor(Color.WHITE);
        tv1.setTextSize(16);
        l1.addView(tv1);
        return tv1;
    }


    public void recordData (PrintWriter prt, ArrayList<String> readingOutput)
    {
        //file writing game data
        try {
            file = new File(getExternalFilesDir("Game Data"), "accel.csv");
            prt = new PrintWriter(file);
        }
        catch (IOException e)
        {
            Log.e("Game Data","Printwriter or file creation failed");
        }

        try{
            for (int i = 0; i < 100; i++)
                prt.println(readingOutput.get(i));
        }
        catch(Exception e)
        {
            Log.e("Game Data","Writing data failed");
        }
        finally
        {
            if (prt!=null)
            {
                prt.close();
            }
            Log.d("Game Data","File write ended");
        }
    }
}


class MyOnClickListener implements View.OnClickListener
{
    AccelerometerEventListener sel;
    public MyOnClickListener(AccelerometerEventListener s) {
        sel=s;
    }

    @Override
    public void onClick(View v)
    {
        sel.resetMax();
        //read your lovely variable
    }

};

class AccelerometerEventListener implements SensorEventListener {
    TextView curr_output,max_output;
    float max_x=0,max_y=0,max_z=0;
    LineGraphView graph;
    ArrayList<String> readingOutput = new ArrayList <String>();         //arraylist of string accelerometer readings



    public AccelerometerEventListener(TextView curr, TextView max, LineGraphView g, ArrayList<String> r){
        curr_output = curr;
        max_output = max;
        graph = g;
        readingOutput = r;
    }



    public void onAccuracyChanged(Sensor s, int i){}
    public void onSensorChanged (SensorEvent se){
        if (se.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            String s = String.format("(%.1f,%.1f,%.1f)",se.values[0],se.values[1],se.values[2]);
            curr_output.setText(s);
            float array [] =  {se.values[0],se.values[1],se.values[2]};
            //graph.purge();
            graph.addPoint(array);
            if (se.values[0]>max_x)
            {
                max_x = se.values[0];
            }
            if (se.values[1]>max_y)
            {
                max_y = se.values[1];
            }
            if (se.values[2]>max_z)
            {
                max_z = se.values[2];
            }

            String m = String.format("(%.1f,%.1f,%.1f)",max_x,max_y,max_z);
            max_output.setText(m);

            String output = String.format("%.1f,%.1f,%.1f",se.values[0],se.values[1],se.values[2]);
            if (readingOutput.size()>100)
            {
                readingOutput.remove(0);
                readingOutput.add(99,output);
            }
            else
            {
                readingOutput.add(output);
            }

            //se.values[0] contains the value i want
            //probably should store this
        }
    }

    public void resetMax(){
        readingOutput.clear();
        max_x=0;
        max_y=0;
        max_z=0;
    }


}


class LightSensorEventListener implements SensorEventListener{
    TextView output,outputMax;
    float maxReading = 0;

    public LightSensorEventListener(TextView outputView,TextView max){
        output = outputView;
        outputMax = max;
    }

    public void onAccuracyChanged(Sensor s, int i){}
    public void onSensorChanged (SensorEvent se){
        if (se.sensor.getType()==Sensor.TYPE_LIGHT)
        {
            String s = String.format("%.1f",se.values[0]);
            output.setText(s);

            if (se.values[0]>maxReading) {
                maxReading = se.values[0];
                outputMax.setText(String.format("%.1f",se.values[0]));
            }


            //se.values[0] contains the value i want
            //probably should store this
        }
    }
}


class MagneticEventListener implements SensorEventListener{
    TextView curr_output,max_output;
    float max_x=0,max_y=0,max_z=0;



    public MagneticEventListener(TextView curr, TextView max){
        curr_output = curr;
        max_output = max;
    }

    public void onAccuracyChanged(Sensor s, int i){
    }
    public void onSensorChanged (SensorEvent se){
        if (se.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD)
        {
            String s = String.format("(%.1f,%.1f,%.1f)",se.values[0],se.values[1],se.values[2]);
            curr_output.setText(s);

            if (se.values[0]>max_x)
            {
                max_x = se.values[0];
            }
            if (se.values[1]>max_y)
            {
                max_y = se.values[1];
            }
            if (se.values[2]>max_z)
            {
                max_z = se.values[2];
            }

            String m = String.format("(%.1f,%.1f,%.1f)",max_x,max_y,max_z);
            max_output.setText(m);

            //se.values[0] contains the value i want
            //probably should store this
        }
    }
}

class RotationEventListener implements SensorEventListener {
    private TextView curr_output, max_output;
    private float max_x = 0, max_y = 0, max_z = 0;

    public RotationEventListener(TextView curr, TextView max) {
        curr_output = curr;
        max_output = max;
    }
    public void resetMax(){
        max_output.setText("0");
    }
    public void onAccuracyChanged(Sensor s, int i) {
    }

    public void onSensorChanged(SensorEvent se) {
        max_output.setText("i rot");
        if (se.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            String s = String.format("(%.1f,%.1f,%.1f)", se.values[0], se.values[1], se.values[2]);
            curr_output.setText(s);

            if (se.values[0] > max_x) {
                max_x = se.values[0];
            }
            if (se.values[1] > max_y) {
                max_y = se.values[1];
            }
            if (se.values[2] > max_z) {
                max_z = se.values[2];
            }

            String m = String.format("(%.1f,%.1f,%.1f)", max_x, max_y, max_z);
            max_output.setText(m);


            //se.values[0] contains the value i want
            //probably should store this
        }
    }
}

