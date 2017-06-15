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

    LinearLayout l1;  // layout used
    LineGraphView graph;  // graph displaying output
    ArrayList<String> readingOutput = new ArrayList <>();  // String accelerometer readings
    File file = null;  // initialize file to write to
    PrintWriter prt = null;  // initialize writer
    Button myButton;  // myButton records accelerometer readings
    Button resetButton;
    TextView arn = null;  // accelerometer record number
    TextView an = null;  // accelerometer current number

    int filenum = 0; // updated for every file created - unique names
    Sensor accelerometer;
    SensorManager sensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l1 = (LinearLayout)findViewById(R.id.layout);
        l1.setOrientation(LinearLayout.VERTICAL);

        // create new instance of reset button
        resetButton = new Button (getApplicationContext());

        l1.addView(resetButton);

        // set up textviews for the various sensor readings
        // some default values for test to see if getting readings

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


        TextView motionx = createLabel("The Motion is:");
        motionx.setTextSize(30);
        TextView motiony = createLabel("");
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        AccelerometerEventListener ael = new AccelerometerEventListener(an,arn,motionx,motiony,graph,readingOutput);

        sensorManager.registerListener(ael,accelerometer,SensorManager.SENSOR_DELAY_GAME);


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
            String name = String.format("accel %d.csv",filenum);
            file = new File(getExternalFilesDir("Game Data"), name);
            filenum++;
            prt = new PrintWriter(file);
        }
        catch (IOException e)
        {
            Log.e("Game Data","Printwriter or file creation failed");
        }

        try{
            for (int i = 0; i < 100; i++)
                prt.println(readingOutput.get(i));

            readingOutput.clear();
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

