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

    private final float [] THRESHOLD_RIGHT = {0.5f,2.0f,0.2f};

    private int sampleCounter;
    private final int SAMPLE_COUNTER_DEFAULT = 30;
    private int waitTime = 0;
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
        waitTime = 0;

    }

    public void activateFSM(float accInput)
    {
        float accSlope = accInput-prevReading;
        switch(myStates) {
            case WAIT:
                //myTV.setText(String.format("wait on slope %f",accSlope));
                //myTV.setText("wait");
                if (accSlope >=THRESHOLD_RIGHT[0] && accInput >=THRESHOLD_RIGHT[1])
                {
                    myStates = myFSMX.FSMStates.RISE;
                }
                else if (accSlope <=-THRESHOLD_RIGHT[0]&&accInput<=-THRESHOLD_RIGHT[1])
                {
                    myStates = myFSMX.FSMStates.FALL;
                }
                break;
            case RISE:
                //myTV.setText("RISE");
                if (accInput<prevReading)
                {
                    myStates = myFSMX.FSMStates.STABLE;
                    mySig =  myFSMX.Signatures.RIGHT;
                }
                else
                {
                    waitTime++;
                    if (waitTime>20) {
                        mySig = myFSMX.Signatures.UNDETERMINED;
                        myStates = myFSMX.FSMStates.DETERMINED;
                    }

                }
                break;
            case FALL:
                if (accInput>prevReading)
                {
                    myStates = myFSMX.FSMStates.STABLE;
                    mySig = myFSMX.Signatures.LEFT;
                }
                else
                {
                    waitTime++;
                    if (waitTime>20) {
                        mySig = myFSMX.Signatures.UNDETERMINED;
                        myStates = myFSMX.FSMStates.DETERMINED;
                    }
                }
                break;
            case STABLE:
                //Log.d("myfsm","STABILIZING");
                //myTV.setText("Stable");
                sampleCounter--;
                if (sampleCounter==0)
                {
                    myStates = myFSMX.FSMStates.DETERMINED;
                    if (Math.abs(accSlope)<THRESHOLD_RIGHT[2])
                    {

                    }
                    else
                        mySig = myFSMX.Signatures.UNDETERMINED;
                }
                break;
            case DETERMINED:
                if (mySig!=myFSMX.Signatures.UNDETERMINED)
                    myTV.setText(mySig.toString());
                resetFSM();
                // call resetFSM();
                break;
            default:
                myTV.setText("reset");
                resetFSM();
                break;
        }

        prevReading = accInput;
    }

}

