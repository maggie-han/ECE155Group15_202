package uwaterloo.ca.ece155group15_202;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by jack on 2017-06-26.
 */

//create class for gameblock
class GameBlock extends GameBlockTemplate {
    ImageView block = new ImageView(getContext());

    RelativeLayout mylayout;
    TextView blockText = new TextView(getContext());
    public int BlockValue = 0;

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

    public int offsetx = -70;
    public int offsety = -80;


    //set offset for textview
    public int ValOffsetX = 200;
    public int ValOffsetY = 150;

    //distance between spaces on the grid
    public int GridBlockSize = 360;



    public GameBlock(Context c, RelativeLayout l1){
        super(c);
        mylayout = l1;
        blockText.setText(String.valueOf(BlockValue));
        //blockText.setTextSize(16);
        blockText.setX(positionXi + ValOffsetX);
        blockText.setY(positionYi + ValOffsetY);
        blockText.bringToFront();
        blockText.setTextSize(40);
        blockText.setTextColor(Color.RED);
        l1.addView(blockText);


    }

    public void setDestination(){

    }

    //@param value: value you wish the blocks textview to be set to.
    public void setValue(int value){
        this.BlockValue = value;
        this.blockText.setText(String.valueOf(value));
        this.blockText.bringToFront();

    }
    //same as set value but creates random value
    public void setValue(){
        int randVal = 2 * (int)(Math.random()*2+1);
        this.BlockValue = randVal;
        Log.d("TV", "setting random value");

        this.blockText.setText(String.valueOf(randVal));
        this.blockText.bringToFront();
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


        this.blockText.setX(positionXi+ValOffsetX);
        this.blockText.setY(positionYi+ValOffsetY);
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
                //this.velocityX+=this.ax;
                //this.velocityY+=this.ay;

                //move the block a distance equivalent to the velocity
                this.positionXi+=this.velocityX;
                this.positionYi+=this.velocityY;


                //stop the block if it exceeds the game boarders
                //do the same for each direction
                /*if (this.positionXi>359*3+this.offsetx) {
                    this.positionXi = 1009;
                    this.setX(this.positionXi);
                    this.blockText.setX(this.positionXi + ValOffsetX);
                    this.stop();
                }
                else if (this.positionXi<this.offsetx) {
                    this.positionXi = -68;
                    this.setX(this.positionXi);
                    this.blockText.setX(this.positionXi + ValOffsetX);
                    this.stop();
                }
                if (this.positionYi>359*3+this.offsety) {
                    this.positionYi = 1004;
                    this.setY(this.positionYi);
                    this.blockText.setY(this.positionYi + ValOffsetY);
                    this.stop();
                }
                else if (this.positionYi<this.offsety) {
                    this.positionYi = -73;
                    this.setY(this.positionYi);
                    this.blockText.setY(this.positionYi + ValOffsetY);
                    this.stop();
                }*/
                this.setX(this.positionXi);
                this.blockText.setX(this.positionXi + ValOffsetX);
                this.setY(this.positionYi);
                this.blockText.setY(this.positionYi + ValOffsetY);
            }

        }
    }
    public int getBlockValue(){
        return this.BlockValue;
    }

    public int getFinalPositionX(){

        return this.xf;
    }
    public int getFinalPOsitionY(){
        return this.yf;
    }



    //create functions for moving in directions (is the same for all move(direction) functions, assume identical operation
    public void moveLeft(int occupied){
        //ensure the block is not going to move outside of the board
        if (xi>0) {
            Log.d("Lab4","FunctionMoveLeft");
            changedFlag=true;
            xf = 0+occupied; //block moves blocks in the direction indicated as long as it stays on the gameboard, takes into account number of occupied blocks between it asn the gameboarder
            positionXf = xf*GridBlockSize+offsetx; // set the final position by multiplying block pixel width by the number of positions it needs to move.
            velocityX = -10;
            //set initial acceleration of the block
            ax = -5;
        }
    }

    public void moveRight(int occupied){
        if (xi<3) {
            Log.d("Lab4","Function");
            changedFlag=true;
            xf = 3-occupied;
            positionXf = xf*GridBlockSize+offsetx;
            velocityX = 10;
            ax = 5;
        }
    }
    public void moveUp(int occupied){
        if (yi>0) {
            changedFlag=true;
            yf = 0+occupied;
            positionYf = yf*GridBlockSize+offsety;
            velocityY = -10;
            ay = -5;
        }
    }

    public void moveDown(int occupied){
        if (yi<3) {
            changedFlag=true;
            yf = 3-occupied;
            positionYf = yf*GridBlockSize+offsety;
            velocityY = 10;
            ay = 5;
        }
    }
    //stop function, set acceleration to 0 and velocity to 0
    public void stop(){
        velocityX = 0;
        velocityY = 0;
        ax=0;
        ay=0;
        xi = xf; //set final x and y to be the current x and y
        yi = yf;
        positionXi = xi*GridBlockSize+offsetx;
        positionYi = yi*GridBlockSize+offsety;
        //positionXi=positionXf;
        //positionYi=positionYf;
        changedFlag=false; //flag to tell whether the blocks are moving to prevent inputs while the blocks are already in motion.
    }
}


