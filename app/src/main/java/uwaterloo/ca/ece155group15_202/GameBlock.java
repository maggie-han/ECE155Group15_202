package uwaterloo.ca.ece155group15_202;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by jack on 2017-06-26.
 */

//create class for gameblock
class GameBlock extends ImageView {
    ImageView block = new ImageView(getContext());

    /*create grid (example if left move is called positions could be
    _ _0_ _1_ _2_ _3_ _X
    |  |   |   |   |
    0------------------
    |  |   |   |   |
    1------------------
    |  |   |   |   |
    2------------------
    |  |   |   |   |
    3-xf,yf--<---xi,yi-
    |
    Y
    */

    public int xi,yi,xf,yf;

    //integers for velocity in x an y directions
    public int velocityX = 0, velocityY = 0;
    //acceleration values
    public int ax,ay;
    //actual pixel values = grid position * pixels between positions on th grid
    public int positionXi,positionXf, positionYi,positionYf;

    //boolean TRUE if block is moving; FALSE if stationary (prevents input when block is already moving)
    public boolean changedFlag=false;


    //set offset for gameblock due to scaling to keep it within the game grid
    public int offsetx = -80;
    public int offsety = -83;

    public GameBlock(Context c){
        super(c);
    }

    public void setPosition(int x, int y){
        //set initial position values
        xi = x;
        xf = x;
        yi = y;
        yf = y;

        //set pixel values of the position 360 is the number of pixels between each square
        positionXi = xi*360+offsetx;
        positionXf = xf*360+offsetx;

        positionYi = yi*360+offsety;
        positionYf = yf*360+offsety;

        this.setX(positionXi);
        this.setY(positionYi);

        //block.setImageResource(R.drawable.block);
    }

    //create functions for moving in directions
    public void moveLeft(){
        //ensure the block is not going to move outside of the board
        if (xi>0) {
            Log.d("Lab4","FunctionMoveLeft");
            changedFlag=true;
            xf = xi-3; //block moves blocks in the direction indicated as long as it stays on the gameboard
            positionXf = xf*360+offsetx;
            //velocityX = -30;
            //set initial acceleration of the block
            ax = -5;
        }
    }

    public void moveRight(){
        if (xi<3) {
            Log.d("Lab4","Function");
            changedFlag=true;
            xf = xi+3;
            positionXf = xf*360+offsetx;
            //velocityX = 30;
            ax = 5;
        }
    }
    public void moveUp(){
        if (yi>0) {
            changedFlag=true;
            yf = yi-3;
            positionYf = yf*360+offsety;
            //velocityY = -30;
            ay = -5;
        }
    }

    public void moveDown(){
        if (yi<3) {
            changedFlag=true;
            yf = yi+3;
            positionYf = yf*360+offsety;
            //velocityY = 30;
            ay = 5;
        }
    }
    //stop function, set acceleration to 0 and velocity to 0
    public void stop(){
        velocityX = 0;
        velocityY = 0;
        ax=0;
        ay=0;
        xi = xf;
        yi = yf;
        positionXi = xi*360+offsetx;
        positionYi = yi*360+offsety;
        //positionXi=positionXf;
        //positionYi=positionYf;
        changedFlag=false;
    }
}


