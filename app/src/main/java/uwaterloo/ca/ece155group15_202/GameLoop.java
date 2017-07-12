package uwaterloo.ca.ece155group15_202;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.TimerTask;

/**
 * Created by jack on 2017-06-26.
 */


//create timer task
class GameLoopTask extends TimerTask {
    public Activity myActivity;
    private ImageView myBackground;
    private GameBlock myBlock;
    public LinkedList<GameBlock>  BlockList = new LinkedList<>();
    private int endGameState=0; //-1 : fail, 0: playing, 1: game finished
    private TextView endGame; //textview for displaying game end conditions
    public int BlockCount = 0; //keeps track of the number of blocks in total

    Context myContext;
    RelativeLayout myLayout;

    //Keeps track of which place on board is currently occupied and will be occupied
    public boolean [][]isOccupied=  {{false,false,false,false}, {false,false,false,false},{false,false,false,false},{false,false,false,false}};
    public boolean [][]willBeOccupied=  {{false,false,false,false}, {false,false,false,false},{false,false,false,false},{false,false,false,false}};

    //GameLoopTask Constructor
    public GameLoopTask(Activity myACT, ImageView background, Context c, RelativeLayout l,TextView e)
    {
        endGame = e;
        myActivity = myACT;
        myBackground = background;
        myContext = c;
        myLayout = l;
        Log.d("CreateBlock", "Creating GameLoopTask");
    }

    public void isGameFinished(){
        for (GameBlock block:BlockList) //navigate through lift
        {
            if (block.BlockValue==256) //if any block reach the success value
            {
                endGameState =1;  //end game state is WIN
            }
        }
        if (endGameState==0)    //if the flag says the game keeps playing
        {
            if (BlockCount==16) //check the number of blocks on board. if it is full
            {
                int mergeCount = 0;
                for (int i = 0; i < 4; i++) //determine if there are possible moves
                {
                    mergeCount+=numMergesCheck(0,i,1); //check if possible right merge
                    mergeCount+=numMergesCheck(i,0,2); //check if possible down merge
                }
                if (mergeCount==0)  //no possible moves, fail
                    endGameState =-1;
            }
        }

        if (endGameState==-1) //display textview for failure
        {
            endGame.setText(":'( YOU FAILED");
            endGame.setTextColor(Color.RED);
            endGame.setTextSize(36);
        }
        else if (endGameState==1) //display textview for success
        {
            endGame.setText("YOU WIN!");
            endGame.setTextColor(Color.GREEN);
            endGame.setTextSize(36);
        }
        else    //keep the textview empty
        {
            endGame.setText("");
            endGame.setTextColor(Color.GREEN);
            endGame.setTextSize(36);
        }
    }


    public void createBlock(){
        updateFutureOccupancy();        //check of the places are occupied, update the occupancy arrays

        int spots = 0;
        int spotsLeft [][] = new int [16][2];
        for (int i = 0;i < 4; i++) {
            for (int j = 0; j < 4; j++) { //compute the number of spots left and populate them in an array for random generation
                if (willBeOccupied[i][j] == false) {
                    spotsLeft[spots][0] = i;
                    spotsLeft[spots][1] = j;
                    spots++;
                }
            }
        }
        if (spots!=(16-BlockCount)) //disreptancy in number of blocks: error check
        {
            String msg = String.format("Spots: %d, BlockCount: %d",spots,BlockCount);
            Log.d("counting",msg);
        }
        if (spots==0)   //no spots means no more block creation, return now before any new blocks are created
            return;
        GameBlock block1 = new GameBlock(myContext, myLayout);

        int tempSpot = (int)(Math.random()*(spots-1)); //random generate block spot

        block1.setPosition(spotsLeft[tempSpot][0],spotsLeft[tempSpot][1]);//match the RNG result with position
        block1.setImageResource(R.drawable.gameblockblueborder);    //image resource for given block

        //rescale the images
        block1.setScaleX(0.64f);
        block1.setScaleY(0.65f);
        Log.d("TV", "setting random value gameloop");

        //randomly generate a block value
        block1.setValue();

        //add the block to the layout
        Log.d("CreateBlock", "Before adding");
        myLayout.addView (block1);
        Log.d("CreateBlock", "added to view");

        //add the block to the linked list
        if(BlockCount == 0)
            BlockList.addFirst(block1);
        else
            BlockList.add(block1);

        //increment the blockcount
        BlockCount += 1;

        Log.d("CreateBlock", "Creating new block");
        updateOccupancy(); //update where the blocks are
    }

    public void updateOccupancy(){
        for (int i = 0; i<4;i++)
        {
            for (int j = 0; j < 4; j++)
            {
                isOccupied[i][j]=false; //first set all spots to false
            }
        }

        for (GameBlock Block: BlockList) //whenever there's a block, change occupancy to true
        {
            isOccupied[Block.xi][Block.yi]=true;
        }
    }

    public void updateFutureOccupancy(){ //flag according to the future occupancy
        for (int i = 0; i<4;i++)
        {
            for (int j = 0; j < 4; j++)
            {
                willBeOccupied[i][j]=false; //initialize all spots to false
            }
        }
        for (GameBlock Block: BlockList)
        {
            willBeOccupied[Block.xf][Block.yf]=true; //set where the blocks will be in the future
        }
    }

    public boolean allDoneMoving(){ //returns true if all blocks are in their final positions and they have finished animation
        for (GameBlock Block: BlockList)
        {
            if (Block.changedFlag)
                return false; //if any is not finished, return false
        }
        return true;
    }

    public void setDirection(int direction){//accepts direction, 0 = up, 1=right, 2 = down, 3 = left
        if (allDoneMoving()) { //only accepts set direction if all blocks have finished their previous animation
            switch (direction) {
                case 0:
                    for (GameBlock Block : BlockList) {
                        String msg = String.format("Block :" + Block.xi + " " + Block.yi + " UP " + numOccupied(Block.xi, Block.yi, 0));
                        Log.d("moving", msg); //debugging message
                        //move all blocks by their direction, passing in the number of occupied spots before them, and the number of merges before them
                        Block.moveUp(numOccupied(Block.xi, Block.yi, 0),numMerges(Block.xi,Block.yi,0));
                    }
                    break;
                case 1:
                    for (GameBlock Block : BlockList) {
                        String msg = String.format("Block :" + Block.xi + " " + Block.yi + " RIGHT " + numOccupied(Block.xi, Block.yi, 1));
                        Log.d("moving", msg);//debugging message
                        //move all blocks by their direction, passing in the number of occupied spots before them, and the number of merges before them
                        Block.moveRight(numOccupied(Block.xi, Block.yi, 1),numMerges(Block.xi,Block.yi,1));
                    }
                    break;
                case 2:
                    for (GameBlock Block : BlockList) {
                        String msg = String.format("Block :" + Block.xi + " " + Block.yi + " DOWN " + numOccupied(Block.xi, Block.yi, 2));
                        Log.d("moving", msg);//debugging message
                        //move all blocks by their direction, passing in the number of occupied spots before them, and the number of merges before them
                        Block.moveDown(numOccupied(Block.xi, Block.yi, 2),numMerges(Block.xi,Block.yi,2));
                    }
                    break;
                case 3:
                    for (GameBlock Block : BlockList) {
                        String msg = String.format("Block :" + Block.xi + " " + Block.yi + " LEFT " + numOccupied(Block.xi, Block.yi, 3));
                        Log.d("moving", msg);//debugging message
                        //move all blocks by their direction, passing in the number of occupied spots before them, and the number of merges before them
                        Block.moveLeft(numOccupied(Block.xi, Block.yi, 3),numMerges(Block.xi,Block.yi,3));
                    }
                    break;
            }
            updateOccupancy(); //after moving all blocks, update their corresponding position
            Log.d("LEFT", "Updated Occupancy");
            if (BlockCount < 16) { //if we have spots for new blocks, every move create a new block
                createBlock();
                BlockList.getLast().setVisibility(View.INVISIBLE);  //set invisible until all other animation finish
                BlockList.getLast().blockText.setVisibility(View.INVISIBLE); //set textview to invisible as well
            }
        }
    }

    public int numOccupied(int x, int y, int direction) //given the position and the direction, compute the number of blocks in the way to help offset the position
    {
        updateOccupancy(); //first update where the blocks are occupied
        int num = 0; //assume none, update accordingly
        switch(direction){
            case 0: //up
                for (int i = 0; i<y; i++)
                {
                    if (isOccupied[x][i])
                        num++;//for each block in the direction, increment counter
                }
                break;
            case 1: //right
                for (int i = 3 ; i > x; i--)
                {
                    if (isOccupied[i][y])
                        num++;//for each block in the direction, increment counter
                }
                break;
            case 2: //down
                for (int i = 3; i>y; i--)
                    if (isOccupied[x][i])
                        num++;//for each block in the direction, increment counter
                break;
            case 3: //left
                for (int i = 0 ; i < x; i++)
                {
                    if (isOccupied[i][y])
                        num++;//for each block in the direction, increment counter
                }
                break;
        }
        return num;

    }

    public int numMerges(int x, int y, int direction) //given the position and the direction, compute the number of merges in the way to help offset the position
    {
        updateOccupancy();
        int num = 0;
        switch(direction){
            case 0: //up
                if (y==1) //determine where our block is located
                {
                    if (valueAtPosition(x,0)==valueAtPosition(x,1))
                    {
                        blockAtPosition(x,0).futureValue=blockAtPosition(x,0).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,1).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                }
                else if (y==2)//determine where our block is located
                {
                    if (valueAtPosition(x,0)==valueAtPosition(x,1)&&isOccupied[x][1])
                    {
                        blockAtPosition(x,0).futureValue=blockAtPosition(x,0).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,1).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(x,1)==valueAtPosition(x,2))
                    {
                        blockAtPosition(x,1).futureValue=blockAtPosition(x,1).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,2).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(x,0)==valueAtPosition(x,2)&&(isOccupied[x][1]==false))
                    {
                        blockAtPosition(x,0).futureValue=blockAtPosition(x,0).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,2).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                }
                else if (y==3)//determine where our block is located
                {
                    if (valueAtPosition(x,0)==valueAtPosition(x,1)&&isOccupied[x][1])
                    {
                        blockAtPosition(x,0).futureValue=blockAtPosition(x,0).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,1).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                        if (valueAtPosition(x,2)==valueAtPosition(x,3)&&isOccupied[x][2])
                        {
                            blockAtPosition(x,2).futureValue=blockAtPosition(x,2).BlockValue*2;//increment the overlapping value
                            num++;
                            blockAtPosition(x,3).toRemove=true;                                //flag the repeated block to be deleted
                        }
                    }
                    else if (valueAtPosition(x,1)==valueAtPosition(x,2)&&isOccupied[x][2])
                    {
                        blockAtPosition(x,1).futureValue=blockAtPosition(x,1).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,2).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(x,2)==valueAtPosition(x,3)&&isOccupied[x][3])
                    {
                        blockAtPosition(x,2).futureValue=blockAtPosition(x,2).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,3).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(x,0)==valueAtPosition(x,2)&&(isOccupied[x][1]==false)&&isOccupied[x][2])
                    {
                        blockAtPosition(x,0).futureValue=blockAtPosition(x,0).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,2).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(x,1)==valueAtPosition(x,3)&&(isOccupied[x][2]==false)&&isOccupied[x][3])
                    {
                        blockAtPosition(x,1).futureValue=blockAtPosition(x,1).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,3).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(x,0)==valueAtPosition(x,3)&&(isOccupied[x][1]==false)&&(isOccupied[x][2]==false)&&isOccupied[x][3])
                    {
                        blockAtPosition(x,0).futureValue=blockAtPosition(x,0).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,3).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                }
                break;
            case 1: //right
                if (x==2)//determine where our block is located
                {
                    if (valueAtPosition(3,y)==valueAtPosition(2,y))
                    {
                        blockAtPosition(3,y).futureValue=blockAtPosition(3,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(2,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                }
                else if (x==1)//determine where our block is located
                {
                    if (valueAtPosition(3,y)==valueAtPosition(2,y)&&isOccupied[2][y])
                    {
                        blockAtPosition(3,y).futureValue=blockAtPosition(3,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(2,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(2,y)==valueAtPosition(1,y))
                    {
                        blockAtPosition(2,y).futureValue=blockAtPosition(2,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(1,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(3,y)==valueAtPosition(1,y)&&(isOccupied[2][y]==false))
                    {
                        blockAtPosition(3,y).futureValue=blockAtPosition(3,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(1,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                }
                else if (x==0)
                {//determine where our block is located
                    if (valueAtPosition(3,y)==valueAtPosition(2,y)&&isOccupied[2][y])
                    {
                        blockAtPosition(3,y).futureValue=blockAtPosition(3,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(2,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                        if (valueAtPosition(1,y)==valueAtPosition(0,y)&&isOccupied[1][y])
                        {
                            blockAtPosition(1,y).futureValue=blockAtPosition(1,y).BlockValue*2;//increment the overlapping value
                            num++;
                            blockAtPosition(0,y).toRemove=true;                                //flag the repeated block to be deleted
                        }
                    }
                    else if (valueAtPosition(2,y)==valueAtPosition(1,y)&&isOccupied[1][y])
                    {
                        blockAtPosition(2,y).futureValue=blockAtPosition(2,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(1,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(1,y)==valueAtPosition(0,y)&&isOccupied[0][y])
                    {
                        blockAtPosition(1,y).futureValue=blockAtPosition(1,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(0,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(3,y)==valueAtPosition(2,y)&&(isOccupied[1][y]==false)&&isOccupied[1][y])
                    {
                        blockAtPosition(3,y).futureValue=blockAtPosition(3,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(1,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(2,y)==valueAtPosition(0,y)&&(isOccupied[1][y]==false)&&isOccupied[0][y])
                    {
                        blockAtPosition(2,y).futureValue=blockAtPosition(2,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(0,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(3,y)==valueAtPosition(0,y)&&(isOccupied[2][y]==false)&&(isOccupied[1][y]==false)&&isOccupied[0][y])
                    {
                        blockAtPosition(3,y).futureValue=blockAtPosition(3,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(0,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                }
                break;
                //break;
            case 2: //down
                if (y==2)//determine where our block is located
                {
                    if (valueAtPosition(x,3)==valueAtPosition(x,2))
                    {
                        blockAtPosition(x,3).futureValue=blockAtPosition(x,3).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,2).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                }
                else if (y==1)//determine where our block is located
                {
                    if (valueAtPosition(x,3)==valueAtPosition(x,2)&&isOccupied[x][2])
                    {
                        blockAtPosition(x,3).futureValue=blockAtPosition(x,3).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,2).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(x,2)==valueAtPosition(x,1))
                    {
                        blockAtPosition(x,2).futureValue=blockAtPosition(x,2).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,1).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(x,3)==valueAtPosition(x,1)&&(isOccupied[x][2]==false))
                    {
                        blockAtPosition(x,3).futureValue=blockAtPosition(x,3).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,1).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                }
                else if (y==0)//determine where our block is located
                {
                    if (valueAtPosition(x,3)==valueAtPosition(x,2)&&isOccupied[x][2])
                    {
                        blockAtPosition(x,3).futureValue=blockAtPosition(x,3).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,2).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                        if (valueAtPosition(x,1)==valueAtPosition(x,0)&&isOccupied[x][1])
                        {
                            blockAtPosition(x,1).futureValue=blockAtPosition(x,1).BlockValue*2;//increment the overlapping value
                            num++;
                            blockAtPosition(x,0).toRemove=true;                                //flag the repeated block to be deleted
                        }
                    }
                    else if (valueAtPosition(x,2)==valueAtPosition(x,1)&&isOccupied[x][1])
                    {
                        blockAtPosition(x,2).futureValue=blockAtPosition(x,2).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,1).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(x,1)==valueAtPosition(x,0)&&isOccupied[x][0])
                    {
                        blockAtPosition(x,1).futureValue=blockAtPosition(x,1).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,0).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(x,3)==valueAtPosition(x,1)&&(isOccupied[x][2]==false)&&isOccupied[x][1])
                    {
                        blockAtPosition(x,3).futureValue=blockAtPosition(x,3).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,1).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(x,2)==valueAtPosition(x,0)&&(isOccupied[x][1]==false)&&isOccupied[x][0])
                    {
                        blockAtPosition(x,2).futureValue=blockAtPosition(x,2).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,0).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(x,3)==valueAtPosition(x,0)&&(isOccupied[x][2]==false)&&(isOccupied[x][1]==false)&&isOccupied[x][0])
                    {
                        blockAtPosition(x,3).futureValue=blockAtPosition(x,3).BlockValue*2;//increment the overlapping value
                        blockAtPosition(x,0).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                }
                break;
            case 3: //left
                if (x==1)//determine where our block is located
                {
                    if (valueAtPosition(0,y)==valueAtPosition(1,y))
                    {
                        blockAtPosition(0,y).futureValue=blockAtPosition(0,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(1,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                }
                else if (x==2)//determine where our block is located
                {
                    if (valueAtPosition(0,y)==valueAtPosition(1,y)&&isOccupied[1][y])
                    {
                        blockAtPosition(0,y).futureValue=blockAtPosition(0,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(1,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(1,y)==valueAtPosition(2,y))
                    {
                        blockAtPosition(1,y).futureValue=blockAtPosition(1,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(2,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(0,y)==valueAtPosition(2,y)&&(isOccupied[1][y]==false))
                    {
                        blockAtPosition(0,y).futureValue=blockAtPosition(0,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(2,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                }
                else if (x==3)//determine where our block is located
                {
                    if (valueAtPosition(0,y)==valueAtPosition(1,y)&&isOccupied[1][y])
                    {
                        blockAtPosition(0,y).futureValue=blockAtPosition(0,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(1,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                        if (valueAtPosition(2,y)==valueAtPosition(3,y)&&isOccupied[2][y])
                        {
                            blockAtPosition(2,y).futureValue=blockAtPosition(2,y).BlockValue*2;//increment the overlapping value
                            num++;
                            blockAtPosition(3,y).toRemove=true;                                //flag the repeated block to be deleted
                        }
                    }
                    else if (valueAtPosition(1,y)==valueAtPosition(2,y)&&isOccupied[2][y])
                    {
                        blockAtPosition(1,y).futureValue=blockAtPosition(1,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(2,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(2,y)==valueAtPosition(3,y)&&isOccupied[3][y])
                    {
                        blockAtPosition(2,y).futureValue=blockAtPosition(2,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(3,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(0,y)==valueAtPosition(2,y)&&(isOccupied[1][y]==false)&&isOccupied[2][y])
                    {
                        blockAtPosition(0,y).futureValue=blockAtPosition(0,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(2,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(1,y)==valueAtPosition(3,y)&&(isOccupied[2][y]==false)&&isOccupied[3][y])
                    {
                        blockAtPosition(1,y).futureValue=blockAtPosition(1,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(3,y).toRemove=true;                                //flag the repeated block to be deleted
                        num++;
                    }
                    else if (valueAtPosition(0,y)==valueAtPosition(3,y)&&(isOccupied[1][y]==false)&&(isOccupied[2][y]==false)&&isOccupied[3][y])
                    {
                        blockAtPosition(0,y).futureValue=blockAtPosition(0,y).BlockValue*2;//increment the overlapping value
                        blockAtPosition(3,y).toRemove=true;                                 //flag the repeated block to be deleted
                        num++;
                    }
                }
                break;
        }
        return num;//return the number of merges ahead
    }

    public int numMergesCheck(int x, int y, int direction) //check for the number of merges, but does not actually flag anything to be merged or edit their values
    {
        updateOccupancy();
        int num = 0;
        switch(direction){
            case 0: //up
                if (y==1)
                {
                    if (valueAtPosition(x,0)==valueAtPosition(x,1)) //first merge
                    {
                        num++;
                    }
                }
                else if (y==2)
                {
                    if (valueAtPosition(x,0)==valueAtPosition(x,1)&&isOccupied[x][1]) //first merge
                    {
                        num++;
                    }
                    else if (valueAtPosition(x,1)==valueAtPosition(x,2)) //second merge only if first is not possible
                    {
                        num++;
                    }
                    else if (valueAtPosition(x,0)==valueAtPosition(x,2)&&(isOccupied[x][1]==false)) //need to make sure blank square do not get merged
                    {
                        num++;
                    }
                }
                else if (y==3)
                {
                    if (valueAtPosition(x,0)==valueAtPosition(x,1)&&isOccupied[x][1])
                    {
                        num++;
                        if (valueAtPosition(x,2)==valueAtPosition(x,3)&&isOccupied[x][2])
                        {
                            num++;
                        }
                    }
                    else if (valueAtPosition(x,1)==valueAtPosition(x,2)&&isOccupied[x][2])
                    {
                        num++;
                    }
                    else if (valueAtPosition(x,2)==valueAtPosition(x,3)&&isOccupied[x][3])
                    {
                        num++;
                    }
                    else if (valueAtPosition(x,0)==valueAtPosition(x,2)&&(isOccupied[x][1]==false)&&isOccupied[x][2])
                    {
                        num++;
                    }
                    else if (valueAtPosition(x,1)==valueAtPosition(x,3)&&(isOccupied[x][2]==false)&&isOccupied[x][3])
                    {
                        num++;
                    }
                    else if (valueAtPosition(x,0)==valueAtPosition(x,3)&&(isOccupied[x][1]==false)&&(isOccupied[x][2]==false)&&isOccupied[x][3])
                    {
                        num++;
                    }
                }
                break;
            case 1: //right
                if (x==2)
                {
                    if (valueAtPosition(3,y)==valueAtPosition(2,y))
                    {
                        num++;
                    }
                }
                else if (x==1)
                {
                    if (valueAtPosition(3,y)==valueAtPosition(2,y)&&isOccupied[2][y])
                    {
                        num++;
                    }
                    else if (valueAtPosition(2,y)==valueAtPosition(1,y))
                    {
                        num++;
                    }
                    else if (valueAtPosition(3,y)==valueAtPosition(1,y)&&(isOccupied[2][y]==false))
                    {
                        num++;
                    }
                }
                else if (x==0)
                {
                    if (valueAtPosition(3,y)==valueAtPosition(2,y)&&isOccupied[2][y])
                    {
                        num++;
                        if (valueAtPosition(1,y)==valueAtPosition(0,y)&&isOccupied[1][y])
                        {
                            num++;
                        }
                    }
                    else if (valueAtPosition(2,y)==valueAtPosition(1,y)&&isOccupied[1][y])
                    {
                        num++;
                    }
                    else if (valueAtPosition(1,y)==valueAtPosition(0,y)&&isOccupied[0][y])
                    {
                        num++;
                    }
                    else if (valueAtPosition(3,y)==valueAtPosition(2,y)&&(isOccupied[1][y]==false)&&isOccupied[1][y])
                    {
                        num++;
                    }
                    else if (valueAtPosition(2,y)==valueAtPosition(0,y)&&(isOccupied[1][y]==false)&&isOccupied[0][y])
                    {
                        num++;
                    }
                    else if (valueAtPosition(3,y)==valueAtPosition(0,y)&&(isOccupied[2][y]==false)&&(isOccupied[1][y]==false)&&isOccupied[0][y])
                    {
                        num++;
                    }
                }
                break;
            //break;
            case 2: //down
                if (y==2)
                {
                    if (valueAtPosition(x,3)==valueAtPosition(x,2))
                    {
                        num++;
                    }
                }
                else if (y==1)
                {
                    if (valueAtPosition(x,3)==valueAtPosition(x,2)&&isOccupied[x][2])
                    {
                        num++;
                    }
                    else if (valueAtPosition(x,2)==valueAtPosition(x,1))
                    {
                        num++;
                    }
                    else if (valueAtPosition(x,3)==valueAtPosition(x,1)&&(isOccupied[x][2]==false))
                    {
                        num++;
                    }
                }
                else if (y==0)
                {
                    if (valueAtPosition(x,3)==valueAtPosition(x,2)&&isOccupied[x][2])
                    {
                        num++;
                        if (valueAtPosition(x,1)==valueAtPosition(x,0)&&isOccupied[x][1])
                        {
                            num++;
                        }
                    }
                    else if (valueAtPosition(x,2)==valueAtPosition(x,1)&&isOccupied[x][1])
                    {
                        num++;
                    }
                    else if (valueAtPosition(x,1)==valueAtPosition(x,0)&&isOccupied[x][0])
                    {
                        num++;
                    }
                    else if (valueAtPosition(x,3)==valueAtPosition(x,1)&&(isOccupied[x][2]==false)&&isOccupied[x][1])
                    {
                        num++;
                    }
                    else if (valueAtPosition(x,2)==valueAtPosition(x,0)&&(isOccupied[x][1]==false)&&isOccupied[x][0])
                    {
                        num++;
                    }
                    else if (valueAtPosition(x,3)==valueAtPosition(x,0)&&(isOccupied[x][2]==false)&&(isOccupied[x][1]==false)&&isOccupied[x][0])
                    {
                        num++;
                    }
                }
                break;
            case 3: //left
                if (x==1)
                {
                    if (valueAtPosition(0,y)==valueAtPosition(1,y))
                    {
                        num++;
                    }
                }
                else if (x==2)
                {
                    if (valueAtPosition(0,y)==valueAtPosition(1,y)&&isOccupied[1][y])
                    {
                        num++;
                    }
                    else if (valueAtPosition(1,y)==valueAtPosition(2,y))
                    {
                        num++;
                    }
                    else if (valueAtPosition(0,y)==valueAtPosition(2,y)&&(isOccupied[1][y]==false))
                    {
                        num++;
                    }
                }
                else if (x==3)
                {
                    if (valueAtPosition(0,y)==valueAtPosition(1,y)&&isOccupied[1][y])
                    {
                        num++;
                        if (valueAtPosition(2,y)==valueAtPosition(3,y)&&isOccupied[2][y])
                        {
                            num++;
                        }
                    }
                    else if (valueAtPosition(1,y)==valueAtPosition(2,y)&&isOccupied[2][y])
                    {
                        num++;
                    }
                    else if (valueAtPosition(2,y)==valueAtPosition(3,y)&&isOccupied[3][y])
                    {
                        num++;
                    }
                    else if (valueAtPosition(0,y)==valueAtPosition(2,y)&&(isOccupied[1][y]==false)&&isOccupied[2][y])
                    {
                        num++;
                    }
                    else if (valueAtPosition(1,y)==valueAtPosition(3,y)&&(isOccupied[2][y]==false)&&isOccupied[3][y])
                    {
                        num++;
                    }
                    else if (valueAtPosition(0,y)==valueAtPosition(3,y)&&(isOccupied[1][y]==false)&&(isOccupied[2][y]==false)&&isOccupied[3][y])
                    {
                        num++;
                    }
                }
                break;
        }
        return num;
    }

    public int valueAtPosition(int x, int y) //returns the value of the block at the given position
    {
        for (GameBlock block:BlockList)
        {
            if (block.xi==x && block.yi==y)
                return block.getBlockValue();
        }
        return -1;
    }

    public GameBlock blockAtPosition(int x, int y) //returns reference to the block at the given location
    {
        for (GameBlock block:BlockList)
        {
            if (block.xi==x && block.yi==y)
                return block;
        }
        return null;        //if there are no block at given position
    }

    public void run (){
        myActivity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d("CreateBlock","run");
                        for (GameBlock Block : BlockList) {
                            Block.move();
                        }
                        if (allDoneMoving())    //if all animation is finished, update with new values and delete the blocks flagged to be deleted
                        {
                            for (int i = 0; i< BlockCount; i++)
                            {
                                String s = String.format("Index: %d",i);
                                GameBlock temp = BlockList.get(i);
                                if(BlockList.get(i).toRemove) //if it is flagged to be deleted
                                {
                                    myLayout.removeView(BlockList.get(i).blockText); //remove the block textview
                                    myLayout.removeView(BlockList.get(i));//remove the block
                                    BlockList.remove(i); //remove such from the linkedlist
                                    i--;
                                    BlockCount--;
                                    //decrease corresponding index and blockcount
                                }
                                else if (temp.futureValue>temp.BlockValue)  //if new values are being updated
                                {
                                    temp.setValue(temp.futureValue);
                                }
                            }
                            BlockList.getLast().setVisibility(View.VISIBLE); //turn the newly created block to visible
                            BlockList.getLast().blockText.setVisibility(View.VISIBLE);//turn the newly created block text to visible
                            isGameFinished();   //check for end game conditions
                        }
                    }
                }
        );
    }

}