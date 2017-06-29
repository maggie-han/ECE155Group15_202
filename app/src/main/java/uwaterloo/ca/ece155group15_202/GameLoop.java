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

    //GameLoopTask Constructor
    public GameLoopTask(Activity myACT, ImageView background,GameBlock block,TextView dir)
    {
        myActivity = myACT;
        myBackground = background;
        myBlock = block;
        myBlock.setPosition(0,0);
    }

    public void run (){
        /*if (direction.getText().equals("LEFT")&&myBlock.changedFlag==false)
        {
            Log.d("Lab4","movingLEFT");
            myBlock.moveLeft();
        }
        else if (direction.getText().equals("RIGHT")&&myBlock.changedFlag==false)
        {
            Log.d("Lab4","movingright");

            myBlock.moveRight();
        }*/
        myActivity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (myBlock.changedFlag)
                        {
                            if (myBlock.positionXi==myBlock.positionXf&&myBlock.positionYi==myBlock.positionYf)
                            {
                                //stop the block when it reaches the final x and y coords
                                Log.d("Lab4","STOPPED");
                                //set the changed falg of the block to false indicating it has stopped moving
                                myBlock.changedFlag = false;
                                myBlock.stop();
                            }
                            else
                            {
                                //increase velocity as it traverses the board
                                myBlock.velocityX+=myBlock.ax;
                                myBlock.velocityY+=myBlock.ay;

                                //move the block a distance equivalent to the velocity
                                myBlock.positionXi+=myBlock.velocityX;
                                myBlock.positionYi+=myBlock.velocityY;

                                //stop the block if it exceeds the game boarders
                                //do the same for each direction
                                if (myBlock.positionXi>359*3+myBlock.offsetx) {
                                    myBlock.positionXi = 1009;
                                    myBlock.setX(myBlock.positionXi);
                                    myBlock.stop();
                                }
                                else if (myBlock.positionXi<myBlock.offsetx) {
                                    myBlock.positionXi = -68;
                                    myBlock.setX(myBlock.positionXi);
                                    myBlock.stop();
                                }
                                if (myBlock.positionYi>359*3+myBlock.offsety) {
                                    myBlock.positionYi = 1004;
                                    myBlock.setY(myBlock.positionYi);
                                    myBlock.stop();
                                }
                                else if (myBlock.positionYi<myBlock.offsety) {
                                    myBlock.positionYi = -73;
                                    myBlock.setY(myBlock.positionYi);
                                    myBlock.stop();
                                }
                                myBlock.setX(myBlock.positionXi);
                                myBlock.setY(myBlock.positionYi);
                            }

                        }
                    }
                }
        );
    }

}