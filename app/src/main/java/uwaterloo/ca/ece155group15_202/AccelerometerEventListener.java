package uwaterloo.ca.ece155group15_202;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import java.util.ArrayList;

class AccelerometerEventListener implements SensorEventListener {
    // initializing variables
    private float max_x=0, max_y=0, max_z=0;
    private float x=0, y=0, z=0, fx=0, fy=0, fz=0;  // x, y, z are raw readings, fx, fy, fz are scaled
    private int C = 10;  // Constant determining scaling
    private ArrayList<String> readingOutput = new ArrayList <String>();  // String accelerometer readings
    private TextView motionx;  // for the display of the direction
    private myFSMX FSM_X;  // the finite state machine responsible for X direction
    private myFSMY FSM_Y;  // for Y direction
    private GameBlock block;


    public AccelerometerEventListener(TextView mox, ArrayList<String> r,GameBlock b){

        readingOutput = r;
        motionx = mox;
        FSM_X = new myFSMX(motionx);
        FSM_Y = new myFSMY(motionx);
        block = b;
    }


    public void onAccuracyChanged(Sensor s, int i){}


    public void onSensorChanged (SensorEvent se){
        if (se.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION)
        {
            // raw readings
            x = se.values[0];
            y = se.values[1];
            z = se.values[2];

            // scaling the raw readings in order to smoothen graph
            fx+=(x-fx)/C;
            fy+=(y-fy)/C;
            fz+=(z-fz)/C;

            // fz is not relevant to the movements, we only look at x and y
            FSM_Y.activateFSM(fy,block);
            FSM_X.activateFSM(fx,block);

            // format data for output
            String s = String.format("(%.1f,%.1f,%.1f)", fx, fy, fz);


            // add point to CSV
            if (readingOutput.size() >= 100)
            {
                readingOutput.remove(0);
                readingOutput.add(99, s);
            }
            else
            {
                readingOutput.add(s);
            }
        }
    }


}
