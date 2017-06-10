package uwaterloo.ca.ece155group15_202;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import org.w3c.dom.Text;
import java.util.ArrayList;
import ca.uwaterloo.sensortoy.LineGraphView;

class AccelerometerEventListener implements SensorEventListener {
    // initializing variables
    private TextView curr_output, max_output;
    private float max_x=0, max_y=0, max_z=0;
    private float x=0, y=0, z=0, fx=0, fy=0, fz=0;  // x, y, z are raw readings, fx, fy, fz are scaled
    private int C = 10;  // Constant determining scaling
    private LineGraphView graph;
    private ArrayList<String> readingOutput = new ArrayList <String>();  // String accelerometer readings
    private TextView motionx, motiony;  // for the display of the direction
    private myFSMX FSM_X;  // the finite state machine responsible for X direction
    private myFSMY FSM_Y;  // for Y direction


    public AccelerometerEventListener(TextView curr, TextView max, TextView mox, TextView moy, LineGraphView g, ArrayList<String> r){
        curr_output = curr;
        max_output = max;
        graph = g;
        readingOutput = r;
        motionx = mox;
        motiony = moy;
        FSM_X = new myFSMX(motionx);
        FSM_Y = new myFSMY(motiony);
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
            FSM_Y.activateFSM(fy);
            FSM_X.activateFSM(fx);

            // updating maxes
            if (x > max_x)
            {
                max_x = x;
            }
            if (y > max_y)
            {
                max_y = y;
            }
            if (z > max_z)
            {
                max_z = z;
            }

            // format data for output
            String s = String.format("(%.1f,%.1f,%.1f)", fx, fy, fz);
            String m = String.format("(%.1f,%.1f,%.1f)", max_x, max_y, max_z);
            curr_output.setText(s);
            max_output.setText(m);

            // add points to graph
            float array [] =  {fx, fy, fz};
            graph.addPoint(array);

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

    // sets maxes back to 0
    public void resetMax(){
        readingOutput.clear();
        max_x=0;
        max_y=0;
        max_z=0;
    }

}
