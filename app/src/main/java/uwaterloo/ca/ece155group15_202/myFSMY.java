package uwaterloo.ca.ece155group15_202;

import android.util.Log;
import android.widget.TextView;

/**
 * Created by Maggie on 2017-06-02.
 */

public class myFSMY {

    enum FSMStates {WAIT,RISE,FALL,STABLE,DETERMINED};
    private myFSMY.FSMStates myStates;

    enum Signatures{UP,DOWN,UNDETERMINED};
    private myFSMY.Signatures mySig;

    private final float [] THRESHOLD_UP = {1.0f,1.5f,0.2f};

    private int sampleCounter;
    private final int SAMPLE_COUNTER_DEFAULT = 30;

    private TextView myTV;

    private float prevReading;

    public myFSMY(TextView tv){
        myStates = myFSMY.FSMStates.WAIT;
        mySig = myFSMY.Signatures.UNDETERMINED;
        prevReading=0;
        sampleCounter=SAMPLE_COUNTER_DEFAULT;
        myTV = tv;
    }

    public void resetFSM(){
        myStates = myFSMY.FSMStates.WAIT;
        mySig = myFSMY.Signatures.UNDETERMINED;
        prevReading=0;
        sampleCounter=SAMPLE_COUNTER_DEFAULT;

    }

    public void activateFSM(float accInput)
    {
        float accSlope = accInput-prevReading;
        switch(myStates) {
            case WAIT:
                //myTV.setText(String.format("wait on slope %f",accSlope));
                if (accSlope >=THRESHOLD_UP[0])
                {
                    myStates = myFSMY.FSMStates.RISE;
                }
                break;
            case RISE:
                //myTV.setText("RISE");
                if (prevReading>=THRESHOLD_UP[1])
                {
                    myStates = myFSMY.FSMStates.STABLE;
                }
                else
                {
                    myStates = myFSMY.FSMStates.FALL;
                    mySig = myFSMY.Signatures.UNDETERMINED;
                }
                break;
            case STABLE:
                //Log.d("myfsm","STABILIZING");
                //myTV.setText("Stable");
                sampleCounter--;
                if (sampleCounter==0)
                {
                    myStates = myFSMY.FSMStates.DETERMINED;
                    if (Math.abs(accInput)<THRESHOLD_UP[2])
                    {
                        mySig = myFSMY.Signatures.UP;
                    }
                    else
                        mySig = myFSMY.Signatures.UNDETERMINED;
                }
                break;
            case DETERMINED:
                if (mySig == myFSMY.Signatures.UNDETERMINED)
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
