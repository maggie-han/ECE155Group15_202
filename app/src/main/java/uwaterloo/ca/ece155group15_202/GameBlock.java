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
    public int offsetx = -74;
    public int offsety = -83;
    //distance between spaces on the grid
    public int GridBlockSize = 359;

    public GameBlock(Context c){
        super(c);
    }

    public void setPosition(int x, int y){
        Log.d("CreateBlock","settingPosition");
        //set initial position values
        xi = x;
        xf = x;
        yi = y;
        yf = y;

        //set pixel values of the position 360 is the number of pixels between each square
        positionXi = xi*GridBlockSize+offsetx;
        positionXf = xf*GridBlockSize+offsetx;

        positionYi = yi*GridBlockSize+offsety;
        positionYf = yf*GridBlockSize+offsety;

        this.setX(positionXi);
        this.setY(positionYi);
        Log.d("CreateBlock","PositionSet");
        //block.setImageResource(R.drawable.block);
    }

    public void move(){
        if (this.changedFlag)
        {
            if (this.positionXi==this.positionXf&&this.positionYi==this.positionYf)
            {
                //stop the block when it reaches the final x and y coords
                Log.d("Lab4","STOPPED");
                //set the changed falg of the block to false indicating it has stopped moving
                this.changedFlag = false;
                this.stop();
            }
            else
            {
                //increase velocity as it traverses the board
                this.velocityX+=this.ax;
                this.velocityY+=this.ay;

                //move the block a distance equivalent to the velocity
                this.positionXi+=this.velocityX;
                this.positionYi+=this.velocityY;

                //stop the block if it exceeds the game boarders
                //do the same for each direction
                if (this.positionXi>359*3+this.offsetx) {
                    this.positionXi = 1009;
                    this.setX(this.positionXi);
                    this.stop();
                }
                else if (this.positionXi<this.offsetx) {
                    this.positionXi = -68;
                    this.setX(this.positionXi);
                    this.stop();
                }
                if (this.positionYi>359*3+this.offsety) {
                    this.positionYi = 1004;
                    this.setY(this.positionYi);
                    this.stop();
                }
                else if (this.positionYi<this.offsety) {
                    this.positionYi = -73;
                    this.setY(this.positionYi);
                    this.stop();
                }
                this.setX(this.positionXi);
                this.setY(this.positionYi);
            }

        }
    }

    //create functions for moving in directions
    public void moveLeft(){
        //ensure the block is not going to move outside of the board
        if (xi>0) {
            Log.d("Lab4","FunctionMoveLeft");
            changedFlag=true;
            xf = 0; //block moves blocks in the direction indicated as long as it stays on the gameboard
            positionXf = xf*GridBlockSize+offsetx;
            //velocityX = -30;
            //set initial acceleration of the block
            ax = -5;
        }
    }

    public void moveRight(){
        if (xi<3) {
            Log.d("Lab4","Function");
            changedFlag=true;
            xf = 3;
            positionXf = xf*GridBlockSize+offsetx;
            //velocityX = 30;
            ax = 5;
        }
    }
    public void moveUp(){
        if (yi>0) {
            changedFlag=true;
            yf = 0;
            positionYf = yf*GridBlockSize+offsety;
            //velocityY = -30;
            ay = -5;
        }
    }

    public void moveDown(){
        if (yi<3) {
            changedFlag=true;
            yf = 3;
            positionYf = yf*GridBlockSize+offsety;
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
        positionXi = xi*GridBlockSize+offsetx;
        positionYi = yi*GridBlockSize+offsety;
        //positionXi=positionXf;
        //positionYi=positionYf;
        changedFlag=false;
    }
}


