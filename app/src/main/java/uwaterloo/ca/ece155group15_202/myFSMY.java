package uwaterloo.ca.ece155group15_202;

import android.widget.TextView;

/**
 * Created by Maggie on 2017-06-02.
 */

public class myFSMY {

    enum FSMStates {WAIT, RISE, FALL, STABLE, DETERMINED};
    private myFSMY.FSMStates myStates;

    enum Signatures{UP,DOWN,UNDETERMINED};
    private myFSMY.Signatures mySig;

    private final float [] THRESHOLD_UP = {0.3f,2,0.2f};
    private final float [] THRESHOLD_DOWN = {-0.4f,-1.5f,0.2f};

    private int sampleCounter;
    private final int SAMPLE_COUNTER_DEFAULT = 30;

    private int waitTime = 0;
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
        waitTime = 0;
        sampleCounter=SAMPLE_COUNTER_DEFAULT;
    }

    public void activateFSM(float accInput)
    {
        float accSlope = accInput-prevReading;
        switch(myStates) {
            case WAIT:
                //myTV.setText(String.format("wait on slope %f",accSlope));
                //myTV.setText("wait");
                if (accSlope >=THRESHOLD_UP[0]&&accInput>=THRESHOLD_UP[1])
                {
                    myStates = myFSMY.FSMStates.RISE;
                }
                else if (accSlope <=THRESHOLD_DOWN[0]&&accInput<=THRESHOLD_DOWN[1])
                {
                    myStates = myFSMY.FSMStates.FALL;
                }
                break;
            case RISE:
                //myTV.setText("RISE");
                if (accInput<prevReading)
                {
                    myStates = myFSMY.FSMStates.STABLE;
                    mySig =  myFSMY.Signatures.UP;
                }
                else
                {
                    waitTime++;
                    if (waitTime>20) {
                        mySig = Signatures.UNDETERMINED;
                        myStates = FSMStates.DETERMINED;
                    }

                }
                break;
            case FALL:
                if (accInput>prevReading)
                {
                    myStates = myFSMY.FSMStates.STABLE;
                    mySig = myFSMY.Signatures.DOWN;
                }
                else
                {
                    waitTime++;
                    if (waitTime>20) {
                        mySig = Signatures.UNDETERMINED;
                        myStates = FSMStates.DETERMINED;
                    }
                }
                break;
            case STABLE:
                //Log.d("myfsm","STABILIZING");
                //myTV.setText("Stable");
                sampleCounter--;
                if (sampleCounter==0)
                {
                    myStates = myFSMY.FSMStates.DETERMINED;
                    if (Math.abs(accSlope)<THRESHOLD_UP[2])
                    {

                    }
                    else
                        mySig = myFSMY.Signatures.UNDETERMINED;
                }
                break;
            case DETERMINED:
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
