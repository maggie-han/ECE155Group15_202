package uwaterloo.ca.ece155group15_202;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    RelativeLayout l1;  // layout used
    ArrayList<String> readingOutput = new ArrayList <>();  // String accelerometer readings
    File file = null;  // initialize file to write to
    PrintWriter prt = null;  // initialize writer
    Button myButton;  // myButton records accelerometer readings

    int filenum = 0; // updated for every file created - unique names
    Sensor accelerometer;
    SensorManager sensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        l1 = (RelativeLayout)findViewById(R.id.layout);
        //l1.setOrientation(LinearLayout.VERTICAL);

        // set up textviews for the various sensor readings
        // some default values for test to see if getting readings

        //******************************SENSOR STUFF***********************************


        //sensor, sensor manager, their respective listeners
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Create the textview for the motion display
        TextView motionx = createLabel("The Motion is:");
        motionx.setTextSize(30);
        motionx.setY(1800);
        motionx.setX(400);


        //create imageview for game grid
        ImageView background = new ImageView(getApplicationContext());
        background.setImageResource(R.drawable.board);
        background.setScaleType(ImageView.ScaleType.FIT_START);
        //l1.getLayoutParams().width = 1440;
        //l1.getLayoutParams().height = 1440;
        l1.addView(background);


        //create imageview and instanciate a new gameblock
        GameBlock block1 = new GameBlock(getApplicationContext());
        block1.setPosition(2,2);
        block1.setImageResource(R.drawable.block);
        block1.setScaleX(0.66f);
        block1.setScaleY(0.65f);
        l1.addView (block1);

        //l1.addView(motionx);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        AccelerometerEventListener ael = new AccelerometerEventListener(motionx,readingOutput,block1);

        sensorManager.registerListener(ael,accelerometer,SensorManager.SENSOR_DELAY_GAME);

        //record data button
        myButton = new Button (getApplicationContext());
        myButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                recordData(prt,readingOutput);
            }
        });
        myButton.setText("write csv values");
        myButton.setX(400);
        myButton.setY(2000);
        l1.addView(myButton);



        //create new timer for animations
        Timer myTimer = new Timer();
        GameLoopTask myMainLoop = new GameLoopTask(this,background,block1,motionx);
        myTimer.schedule(myMainLoop,10,10); //schedule 1 move every 10ms (100fps)

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


