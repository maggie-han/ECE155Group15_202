package uwaterloo.ca.ece155group15_202;

import android.util.Log;
import android.widget.TextView;

/**
 * Created by Maggie on 2017-06-01.
 */

public class myFSMX {

    enum FSMStates {WAIT,RISE,FALL,STABLE,DETERMINED};
    private FSMStates myStates;

    enum Signatures{LEFT,RIGHT,UNDETERMINED};
    private Signatures mySig;

    private final float [] THRESHOLD_RIGHT = {0.4f,-0.5f,0.2f};

    private int sampleCounter;
    private final int SAMPLE_COUNTER_DEFAULT = 30;

    private TextView myTV;

    private float prevReading;

    public myFSMX(TextView tv){
        myStates = FSMStates.WAIT;
        mySig = Signatures.UNDETERMINED;
        prevReading=0;
        sampleCounter=SAMPLE_COUNTER_DEFAULT;
        myTV = tv;
    }

    public void resetFSM(){
        myStates = FSMStates.WAIT;
        mySig = Signatures.UNDETERMINED;
        prevReading=0;
        sampleCounter=SAMPLE_COUNTER_DEFAULT;

    }

    public void activateFSM(float accInput)
    {
        float accSlope = accInput-prevReading;
        switch(myStates) {
           case WAIT:
               myTV.setText(String.format("wait on slope %f",accSlope));
               if (accSlope >=THRESHOLD_RIGHT[0])
               {
                   myStates = FSMStates.RISE;
               }
               break;
           case RISE:
               myTV.setText("RISE");
               if (accSlope<=THRESHOLD_RIGHT[1])
               {
                   myStates = FSMStates.STABLE;
               }
               else
               {
                   myStates = FSMStates.FALL;
                   mySig = Signatures.UNDETERMINED;
               }
               break;
           case STABLE:
               //Log.d("myfsm","STABILIZING");
               myTV.setText("Stable");
               sampleCounter--;
               if (sampleCounter==0)
               {
                   myStates = FSMStates.DETERMINED;
                   if (Math.abs(accInput)<THRESHOLD_RIGHT[2])
                   {
                       mySig = Signatures.RIGHT;
                   }
                   else
                       mySig = Signatures.UNDETERMINED;
               }
               break;
           case DETERMINED:
               if (mySig == Signatures.UNDETERMINED)
               {
                   Log.d("myfsm","UNDERTERMINED");
               }
               //set textview
               myTV.setText(mySig.toString());
               resetFSM();
               // call resetFSM();
               break;
           default:
               resetFSM();
               break;
       }

       prevReading = accInput;
    }
}

