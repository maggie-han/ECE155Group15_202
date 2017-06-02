package uwaterloo.ca.ece155group15_202;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import ca.uwaterloo.sensortoy.LineGraphView;

/**
 * Created by Maggie on 2017-06-02.
 */

class AccelerometerEventListener implements SensorEventListener {
    private TextView curr_output,max_output;
    private float max_x=0,max_y=0,max_z=0;
    private float x=0,y=0,z=0,fx=0,fy=0,fz=0;
    private int C = 5;
    private LineGraphView graph;
    private ArrayList<String> readingOutput = new ArrayList <String>();         //arraylist of string accelerometer readings
    private TextView motion1,motion2;
    private myFSMX FSM_X;
    private myFSMY FSM_Y;


    public AccelerometerEventListener(TextView curr, TextView max, TextView mo1,TextView mo2, LineGraphView g, ArrayList<String> r){
        curr_output = curr;
        max_output = max;
        graph = g;
        readingOutput = r;
        motion1 = mo1;
        motion2 = mo2;
        FSM_X = new myFSMX(motion1);
        FSM_Y = new myFSMY(motion2);

    }



    public void onAccuracyChanged(Sensor s, int i){}
    public void onSensorChanged (SensorEvent se){
        //max_output.setText("changed here");
        if (se.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION)
        {
            //max_output.setText("It changes");
            x = se.values[0];
            y = se.values[1];
            z = se.values[2];

            fx+=(x-fx)/C;
            fy+=(y-fy)/C;
            fz+=(z-fz)/C;

            FSM_X.activateFSM(fx);
            FSM_Y.activateFSM(fy);

            String s = String.format("(%.1f,%.1f,%.1f)",fx,fy,fz);
            curr_output.setText(s);
            float array [] =  {fx,fy,fz};
            //graph.purge();
            graph.addPoint(array);
            if (x>max_x)
            {
                max_x = x;
            }
            if (y>max_y)
            {
                max_y = y;
            }
            if (z>max_z)
            {
                max_z = z;
            }

            String m = String.format("(%.1f,%.1f,%.1f)",max_x,max_y,max_z);
            max_output.setText(m);

            String output = String.format("%.1f,%.1f,%.1f",fx,fy,fz);
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
