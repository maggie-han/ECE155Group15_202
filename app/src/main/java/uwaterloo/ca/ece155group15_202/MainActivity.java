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


        TextView motionx = createLabel("The Motion is:");

        motionx.setTextSize(30);
        motionx.setY(1800);
        motionx.setX(400);



        ImageView background = new ImageView(getApplicationContext());
        background.setImageResource(R.drawable.board);
        background.setScaleType(ImageView.ScaleType.FIT_START);
        //l1.getLayoutParams().width = 1440;
        //l1.getLayoutParams().height = 1440;
        l1.addView(background);

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




        Timer myTimer = new Timer();
        GameLoopTask myMainLoop = new GameLoopTask(this,background,block1,motionx);
        myTimer.schedule(myMainLoop,10,10);

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

class GameLoopTask extends TimerTask {
    public Activity myActivity;
    private ImageView myBackground;
    private GameBlock myBlock;


    public GameLoopTask(Activity myACT, ImageView background,GameBlock block,TextView dir)
    {
        myActivity = myACT;
        myBackground = background;
        myBlock = block;
        myBlock.setPosition(0,0);
    }

    public void run (){
        /*if (direction.getText().equals("LEFT")&&myBlock.changedFlag==false)
        {
            Log.d("Lab4","movingLEFT");
            myBlock.moveLeft();
        }
        else if (direction.getText().equals("RIGHT")&&myBlock.changedFlag==false)
        {
            Log.d("Lab4","movingright");

            myBlock.moveRight();
        }*/
        myActivity.runOnUiThread(
              new Runnable() {
                  @Override
                  public void run() {
                      if (myBlock.changedFlag)
                      {
                          if (myBlock.positionXi==myBlock.positionXf&&myBlock.positionYi==myBlock.positionYf)
                          {
                              Log.d("Lab4","STOPPED");
                              myBlock.changedFlag = false;
                              myBlock.stop();
                          }
                          else
                          {
                              myBlock.velocityX+=myBlock.ax;
                              myBlock.velocityY+=myBlock.ay;
                              myBlock.positionXi+=myBlock.velocityX;
                              myBlock.positionYi+=myBlock.velocityY;
                              if (myBlock.positionXi>360*3+myBlock.offsetx) {
                                  myBlock.positionXi = 1012;
                                  myBlock.setX(myBlock.positionXi);
                                  myBlock.stop();
                              }
                              else if (myBlock.positionXi<myBlock.offsetx) {
                                  myBlock.positionXi = -68;
                                  myBlock.setX(myBlock.positionXi);
                                  myBlock.stop();
                              }
                              if (myBlock.positionYi>360*3+myBlock.offsety) {
                                  myBlock.positionYi = 1007;
                                  myBlock.setY(myBlock.positionYi);
                                  myBlock.stop();
                              }
                              else if (myBlock.positionYi<myBlock.offsety) {
                                  myBlock.positionYi = -73;
                                  myBlock.setY(myBlock.positionYi);
                                  myBlock.stop();
                              }
                              myBlock.setX(myBlock.positionXi);
                              myBlock.setY(myBlock.positionYi);
                          }

                      }
                  }
              }
        );
    }

}

class GameBlock extends ImageView{
    ImageView block = new ImageView(getContext());
    public int xi,yi,xf,yf;
    public int velocityX = 0, velocityY = 0;
    public int ax,ay;
    public int positionXi,positionXf, positionYi,positionYf;
    public boolean changedFlag=false;

    public int offsetx = -80;
    public int offsety = -83;

    public GameBlock(Context c){
        super(c);
    }

    public void setPosition(int x, int y){
        xi = x;
        xf = x;
        yi = y;
        yf = y;

        positionXi = xi*360+offsetx;
        positionXf = xf*360+offsetx;

        positionYi = yi*360+offsety;
        positionYf = yf*360+offsety;

        this.setX(positionXi);
        this.setY(positionYi);

        //block.setImageResource(R.drawable.block);
    }

    public void moveLeft(){
        if (xi>0) {
            Log.d("Lab4","FunctionMoveLeft");
            changedFlag=true;
            xf = xi-3;
            positionXf = xf*360+offsetx;
            //velocityX = -30;
            ax = -5;
        }
    }

    public void moveRight(){
        if (xi<3) {
            Log.d("Lab4","Function");
            changedFlag=true;
            xf = xi+3;
            positionXf = xf*360+offsetx;
            //velocityX = 30;
            ax = 5;
        }
    }
    public void moveUp(){
        if (yi>0) {
            changedFlag=true;
            yf = yi-3;
            positionYf = yf*360+offsety;
            //velocityY = -30;
            ay = -5;
        }
    }

    public void moveDown(){
        if (yi<3) {
            changedFlag=true;
            yf = yi+3;
            positionYf = yf*360+offsety;
            //velocityY = 30;
            ay = 5;
        }
    }

    public void stop(){
        velocityX = 0;
        velocityY = 0;
        ax=0;
        ay=0;
        xi = xf;
        yi = yf;
        positionXi = xi*360+offsetx;
        positionYi = yi*360+offsety;
        //positionXi=positionXf;
        //positionYi=positionYf;
        changedFlag=false;
    }
}


