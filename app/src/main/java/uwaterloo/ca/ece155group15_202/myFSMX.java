package uwaterloo.ca.ece155group15_202;

import android.util.Log;
import android.widget.TextView;

/**
 * Created by Maggie on 2017-06-01.
 */

public class myFSMX {

    // here we initialize the different possible states
    enum FSMStates {WAIT,RISE,FALL,STABLE,DETERMINED};
    private FSMStates myStates;
    // these are the possible outcomes
    enum Signatures{LEFT,RIGHT,UNDETERMINED};
    private Signatures mySig;
    // defining the threshold
    private final float [] THRESHOLD_RIGHT = {0.5f,2.0f,0.2f};

    private int sampleCounter;
    private final int SAMPLE_COUNTER_DEFAULT = 30;
    private int waitTime = 0;
    private TextView myTV;
    // the reading before the current one
    private float prevReading;

    // the textview for the output motion
    public myFSMX(TextView tv){
        myStates = FSMStates.WAIT;
        mySig = Signatures.UNDETERMINED;
        prevReading=0;
        sampleCounter=SAMPLE_COUNTER_DEFAULT;
        myTV = tv;
    }

    // resetting to the default state, and setting the previous reading to zero
    public void resetFSM(){
        myStates = FSMStates.WAIT;
        mySig = Signatures.UNDETERMINED;
        prevReading=0;
        sampleCounter=SAMPLE_COUNTER_DEFAULT;
        waitTime = 0;

    }


    public void activateFSM(float accInput,GameBlock block)
    {
        float accSlope = accInput-prevReading;
        switch(myStates) {
            // from the WAIT state to either RISE or FALL dependent on the accSlope and accInput
            case WAIT:
                // myTV.setText(String.format("wait on slope %f",accSlope));
                // myTV.setText("wait");
                if (accSlope >=THRESHOLD_RIGHT[0] && accInput >=THRESHOLD_RIGHT[1])
                {
                    myStates = myFSMX.FSMStates.RISE;
                }
                else if (accSlope <=-THRESHOLD_RIGHT[0]&&accInput<=-THRESHOLD_RIGHT[1])
                {
                    myStates = myFSMX.FSMStates.FALL;
                }
                break;
            // from the RISE state to either STABLE or back to UNDETERMINED dependent on accInput
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
            // from the FALL state to either STABLE or back to UNDETERMINED dependent on accInput
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
            // from the STABLE state to either DETERMINED or to UNDETERMINED dependent sampleCounter
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
            // once in the DETERMINED state it will either be LEFT, RIGHT, or
            case DETERMINED:
                // it will be be set as UNDETERMINED unless a state carried through (LEFT or RIGHT)
                if (mySig!=myFSMX.Signatures.UNDETERMINED) {
                    myTV.setText(mySig.toString());
                    if (block.changedFlag==false) {
                        if (mySig == Signatures.LEFT) {
                            block.moveLeft();
                        } else if (mySig == Signatures.RIGHT) {
                            block.moveRight();
                        }
                    }
                }
                // resetting the FSM once the state has been determined
                resetFSM();
                break;
            default:
                myTV.setText("reset");
                resetFSM();
                break;
        }
        prevReading = accInput;
    }

}

