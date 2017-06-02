package uwaterloo.ca.ece155group15_202;

import android.util.Log;

/**
 * Created by Maggie on 2017-06-01.
 */

public class myFSM {

    enum FSMStates {WAIT, RISE, FALL, STABLE, DETERMINED};
    private FSMStates myStates;

    enum Signatures{LEFT, RIGHT, UNDETERMINED};
    private Signatures mySig;

    private final float [] THRESHOLD_RIGHT = {1.0f, 1.5f, 0.2f};

    private int sampleCounter;
    private final int SAMPLE_COUNTER_DEFAULT = 30;

    private float prevReading;

    public myFSM(){
        myStates = FSMStates.WAIT;
        mySig = Signatures.UNDETERMINED;
        prevReading=0;
        sampleCounter=SAMPLE_COUNTER_DEFAULT;
    }

    public void resetFSM(){
        myStates = FSMStates.WAIT;
        mySig = Signatures.UNDETERMINED;
        prevReading=0;
        sampleCounter=SAMPLE_COUNTER_DEFAULT;
    }

    public void activateFSM(float accInput){
        float accSlope = accInput-prevReading;
        switch(myStates) {
           case WAIT:
               Log.d("myfsm",String.format("wait on slope %f",accSlope));
               if (accSlope >= THRESHOLD_RIGHT[0])
               {
                   myStates = FSMStates.RISE;
               }
               break;
           case RISE:
               Log.d("myfsm","CASE RISE");
               if (prevReading >= THRESHOLD_RIGHT[1])
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
               Log.d("myfsm","STABILIZING");
               sampleCounter--;
               if (sampleCounter==0)
               {
                   myStates = FSMStates.DETERMINED;
                   if (Math.abs(accInput) < THRESHOLD_RIGHT[2])
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
                   Log.d("myfsm", "UNDETERMINED");
               }
               //set textview
               // call resetFSM();
               break;
           default:
               resetFSM();
               break;
       }
       prevReading = accInput;
    }
}

