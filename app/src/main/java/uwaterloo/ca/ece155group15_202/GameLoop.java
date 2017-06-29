package uwaterloo.ca.ece155group15_202;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.TimerTask;

/**
 * Created by jack on 2017-06-26.
 */


//create timer task
class GameLoopTask extends TimerTask {
    public Activity myActivity;
    private ImageView myBackground;
    private GameBlock myBlock;
    public enum gameDirection{UP,DOWN,LEFT,RIGHT};
    public gameDirection currentGameDirection;

    //GameLoopTask Constructor
    public GameLoopTask(Activity myACT, ImageView background,GameBlock block,TextView dir)
    {
        myActivity = myACT;
        myBackground = background;
        myBlock = block;
        myBlock.setPosition(0,0);
    }

    public void run (){
        myActivity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        myBlock.move();
                    }
                }
        );
    }

}