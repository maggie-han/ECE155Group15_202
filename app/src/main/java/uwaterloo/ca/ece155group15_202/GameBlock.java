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
    TextView blockText = new TextView(getContext()); //textview of the number
    public int BlockValue = 0;  //contains the current block value
    public int futureValue = 0; //value to be updated after all animation finish

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

    //position according to above grid
    public int xi,yi,xf,yf;

    //integers for velocity in x an y directions
    public int velocityX = 0, velocityY = 0;
    //acceleration values
    public int ax,ay;
    //actual pixel values = grid position * pixels between positions on th grid
    public int positionXi,positionXf, positionYi,positionYf;

    //boolean TRUE if block is moving; FALSE if stationary (prevents input when block is already moving)
    public boolean changedFlag=false;

    public boolean toRemove = false;

    //set offset for gameblock due to scaling to keep it within the game grid

    public int offsetx = -70;
    public int offsety = -80;


    //set offset for textview
    public int ValOffsetX = 200;
    public int ValOffsetY = 150;

    //distance between spaces on the grid
    public int GridBlockSize = 360;

    //constructor of the bloock, set up most values used
    public GameBlock(Context c, RelativeLayout l1){
        super(c);
        mylayout = l1;
        blockText.setText(String.valueOf(BlockValue));
        futureValue=BlockValue;
        blockText.setX(positionXi + ValOffsetX); //position include offset from the grid
        blockText.setY(positionYi + ValOffsetY);
        blockText.bringToFront();               //need to be the front most
        blockText.setTextSize(40);
        blockText.setTextColor(Color.GREEN);
        l1.addView(blockText);                  //add the textview

    }

    public void setDestination(){//inherited from the gameBlockTemplate

    }

    //@param value: value you wish the blocks textview to be set to.
    public void setValue(int value){
        this.BlockValue = value;
        this.futureValue = value;
        if(value >= 10 && value  <100){
            this.ValOffsetX = 165;
            blockText.setX(positionXi + ValOffsetX);
        }

        else if(value >=100){
            this.ValOffsetX = 125;
            blockText.setX(positionXi + ValOffsetX);
        }

        this.blockText.setText(String.valueOf(value));
        this.blockText.bringToFront();

    }
    //same as set value but creates random value
    public void setValue(){
        int randVal = 2 * (int)(Math.random()*2+1);
        this.BlockValue = randVal;
        this.futureValue = randVal;
        Log.d("TV", "setting random value");
        this.blockText.setText(String.valueOf(randVal));
        this.blockText.bringToFront();
    }
    //given parameter,sets position of block
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

        //the textView will have the same position as the block excepet with different offset
        this.blockText.setX(positionXi+ValOffsetX);
        this.blockText.setY(positionYi+ValOffsetY);
        this.setX(positionXi);
        this.setY(positionYi);
        Log.d("CreateBlock","PositionSet");
        //block.setImageResource(R.drawable.block);
    }

    public void move(){
        if (this.changedFlag) //if it receives a notice to change position
        {
            if (this.positionXi==this.positionXf&&this.positionYi==this.positionYf)
            {
                //stop the block when it reaches the final x and y coords
                //set the changed flag of the block to false indicating it has stopped moving
                this.changedFlag = false;
                this.stop();
            }
            else
            {

                //move the block a distance equivalent to the velocity
                this.positionXi+=this.velocityX;
                this.positionYi+=this.velocityY;

                //set the position for the block and the corresponding textview
                this.setX(this.positionXi);
                this.blockText.setX(this.positionXi + ValOffsetX);
                this.setY(this.positionYi);
                this.blockText.setY(this.positionYi + ValOffsetY);
            }

        }
    }

    //functions for easily retrieving values
    public int getBlockValue(){
        return this.BlockValue;
    }

    public void setFutureValue(int v){
        this.futureValue=v;
    }

    public int getFinalPositionX(){
        return this.xf;
    }
    public int getFinalPOsitionY(){
        return this.yf;
    }

    //create functions for moving in directions (is the same for all move(direction) functions, assume identical operation
    public void moveLeft(int occupied,int merged){
        //ensure the block is not going to move outside of the board
        if (xi>0) {
            Log.d("Lab4","FunctionMoveLeft");
            changedFlag=true;
            xf = 0+occupied-merged; //block moves blocks in the direction indicated as long as it stays on the gameboard, takes into account number of occupied blocks between it asn the gameboarder
            positionXf = xf*GridBlockSize+offsetx; // set the final position by multiplying block pixel width by the number of positions it needs to move.
            velocityX = -10;
            //set initial acceleration of the block
            ax = -5;
            Log.d("Lab4","FunctionFinishLeft");
        }
    }

    public void moveRight(int occupied,int merged){
        if (xi<3) {//ensure the block is not going to move outside of the board
            Log.d("Lab4","Function");
            changedFlag=true;
            xf = 3-occupied+merged; //blocks moves in the corresponding direction, taking consideration how many are occupied and how many spots merged
            positionXf = xf*GridBlockSize+offsetx; //calculate the pixel value for position
            velocityX = 10;
            ax = 5;
        }
    }
    public void moveUp(int occupied,int merged){
        if (yi>0) {//ensure the block is not going to move outside of the board
            changedFlag=true;
            yf = 0+occupied-merged;//blocks moves in the corresponding direction, taking consideration how many are occupied and how many spots merged
            positionYf = yf*GridBlockSize+offsety;//calculate the pixel value for position
            velocityY = -10;
            ay = -5;
        }
    }

    public void moveDown(int occupied,int merged){
        if (yi<3) {//ensure the block is not going to move outside of the board
            changedFlag=true;
            yf = 3-occupied+merged;//blocks moves in the corresponding direction, taking consideration how many are occupied and how many spots merged
            positionYf = yf*GridBlockSize+offsety;//calculate the pixel value for position
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


