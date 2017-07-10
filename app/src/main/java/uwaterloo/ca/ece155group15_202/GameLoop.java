package uwaterloo.ca.ece155group15_202;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
    //public GameBlock[][]BlockList = new GameBlock[4][4];
    public int BlockCount = 0;

    public enum gameDirection{UP,DOWN,LEFT,RIGHT};
    public gameDirection currentGameDirection;
    Context myContext;
    RelativeLayout myLayout;

    public boolean [][]isOccupied=  {{false,false,false,false}, {false,false,false,false},{false,false,false,false},{false,false,false,false}};
    public boolean [][]willBeOccupied=  {{false,false,false,false}, {false,false,false,false},{false,false,false,false},{false,false,false,false}};

    //GameLoopTask Constructor
    public GameLoopTask(Activity myACT, ImageView background, Context c, RelativeLayout l)
    {
        myActivity = myACT;
        myBackground = background;
        //myBlock.setPosition((int )(Math.random()*3),(int )(Math.random()*3));
        myContext = c;
        myLayout = l;
        Log.d("CreateBlock", "Creating GameLoopTask");
    }

    public void createBlock(){
        Log.d("LEFT","in createBlock");
        updateFutureOccupancy();
        Log.d("LEFT","after updateFutureOccupancy");
        int spots = 0;
        int spotsLeft [][] = new int [16][2];
        for (int i = 0;i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (willBeOccupied[i][j] == false) {
                    spotsLeft[spots][0] = i;
                    spotsLeft[spots][1] = j;
                    spots++;
                }
            }
        }
        if (spots!=(16-BlockCount))
        {
            String msg = String.format("Spots: %d, BlockCount: %d",spots,BlockCount);
            Log.d("counting",msg);
        }
        if (spots==0)
            return;
        GameBlock block1 = new GameBlock(myContext, myLayout);

        int tempSpot = (int)(Math.random()*(spots-1));

        block1.setPosition(spotsLeft[tempSpot][0],spotsLeft[tempSpot][1]);
        block1.setImageResource(R.drawable.gameblockblueborder);

        block1.setScaleX(0.66f);
        block1.setScaleY(0.65f);
        Log.d("TV", "setting random value gameloop");
        block1.setValue();
        Log.d("CreateBlock", "Before adding");
        //if (allDoneMoving())
            myLayout.addView (block1);
        Log.d("CreateBlock", "added to view");
        if(BlockCount == 0)
            BlockList.addFirst(block1);
        else
            BlockList.add(block1);


        BlockCount += 1;


        Log.d("CreateBlock", "Creating new block");
        updateOccupancy();
    }

    public void updateOccupancy(){
        for (int i = 0; i<4;i++)
        {
            for (int j = 0; j < 4; j++)
            {
                isOccupied[i][j]=false;
            }
        }

        for (GameBlock Block: BlockList)
        {
            isOccupied[Block.xi][Block.yi]=true;
        }
    }

    public void updateFutureOccupancy(){
        for (int i = 0; i<4;i++)
        {
            for (int j = 0; j < 4; j++)
            {
                willBeOccupied[i][j]=false;
            }
        }
        for (GameBlock Block: BlockList)
        {
            willBeOccupied[Block.xf][Block.yf]=true;
        }
        Log.d("LEFT","inside updatefutureoccupancy");
    }

    public boolean allDoneMoving(){
        for (GameBlock Block: BlockList)
        {
            if (Block.changedFlag)
                return false;
        }
        return true;
    }

    public void setDirection(int direction){
        if (allDoneMoving()) {
            switch (direction) {
                case 0:
                    for (GameBlock Block : BlockList) {
                        String msg = String.format("Block :" + Block.xi + " " + Block.yi + " UP " + numOccupied(Block.xi, Block.yi, 0));
                        Log.d("moving", msg);
                        Block.moveUp(numOccupied(Block.xi, Block.yi, 0));
                    }
                    break;
                case 1:
                    for (GameBlock Block : BlockList) {
                        String msg = String.format("Block :" + Block.xi + " " + Block.yi + " RIGHT " + numOccupied(Block.xi, Block.yi, 1));
                        Log.d("moving", msg);
                        Block.moveRight(numOccupied(Block.xi, Block.yi, 1));
                    }
                    break;
                case 2:
                    for (GameBlock Block : BlockList) {
                        String msg = String.format("Block :" + Block.xi + " " + Block.yi + " DOWN " + numOccupied(Block.xi, Block.yi, 2));
                        Log.d("moving", msg);
                        Block.moveDown(numOccupied(Block.xi, Block.yi, 2));
                    }
                    break;
                case 3:
                    for (GameBlock Block : BlockList) {
                        String msg = String.format("Block :" + Block.xi + " " + Block.yi + " LEFT " + numOccupied(Block.xi, Block.yi, 3));
                        Log.d("moving", msg);
                        if (numMerges(Block.xi,Block.yi,3)!=0) {
                            String msg2 = String.format(" number: %d",numMerges(Block.xi,Block.yi,3));
                            Log.d("LEFT", msg2);
                        }
                        Block.moveLeft(numOccupied(Block.xi, Block.yi, 3),numMerges(Block.xi,Block.yi,3));
                    }
                    break;
            }
            updateOccupancy();
            Log.d("LEFT", "Updated Occupancy");
            if (BlockCount < 16) {
                createBlock();
            }
            Log.d("LEFT", "create block");
        }
    }

    public int numOccupied(int x, int y, int direction)
    {
        updateOccupancy();
        int num = 0;
        switch(direction){
            case 0: //up
                for (int i = 0; i<y; i++)
                {
                    if (isOccupied[x][i])
                        num++;
                }
                break;
            case 1: //right
                for (int i = 3 ; i > x; i--)
                {
                    if (isOccupied[i][y])
                        num++;
                }
                break;
            case 2: //down
                for (int i = 3; i>y; i--)
                    if (isOccupied[x][i])
                        num++;
                break;
            case 3: //left
                for (int i = 0 ; i < x; i++)
                {
                    if (isOccupied[i][y])
                        num++;
                }
                break;
        }
        return num;

    }

    public int numMerges(int x, int y, int direction)
    {
        updateOccupancy();
        int num = 0;
        switch(direction){
            case 0: //up
                return 0;
                //break;
            case 1: //right
               /*
               find the next thing, if they are the same, delete and merge

                */
                return 0;
                //break;
            case 2: //down
                return 0;
                //break;
            case 3: //left
                if (x==1)
                {
                    if (valueAtPosition(0,y)==valueAtPosition(1,y))
                    {
                        blockAtPosition(0,y).futureValue=blockAtPosition(0,y).BlockValue*2;
                        blockAtPosition(1,y).toRemove=true;
                        num++;
                    }
                }
                else if (x==2)
                {
                    if (valueAtPosition(0,y)==valueAtPosition(1,y)&&isOccupied[1][y])
                    {
                        blockAtPosition(0,y).futureValue=blockAtPosition(0,y).BlockValue*2;
                        blockAtPosition(1,y).toRemove=true;
                        num++;
                    }
                    else if (valueAtPosition(1,y)==valueAtPosition(2,y))
                    {
                        blockAtPosition(1,y).futureValue=blockAtPosition(1,y).BlockValue*2;
                        blockAtPosition(2,y).toRemove=true;
                        num++;
                    }
                    else if (valueAtPosition(0,y)==valueAtPosition(2,y)&&(isOccupied[1][y]==false))
                    {
                        blockAtPosition(0,y).futureValue=blockAtPosition(0,y).BlockValue*2;
                        blockAtPosition(2,y).toRemove=true;
                        num++;
                    }
                }
                else if (x==3)
                {
                    if (valueAtPosition(0,y)==valueAtPosition(1,y)&&isOccupied[1][y])
                    {
                        blockAtPosition(0,y).futureValue=blockAtPosition(0,y).BlockValue*2;
                        blockAtPosition(1,y).toRemove=true;
                        num++;
                        if (valueAtPosition(2,y)==valueAtPosition(3,y)&&isOccupied[2][y])
                        {
                            blockAtPosition(2,y).futureValue=blockAtPosition(2,y).BlockValue*2;
                            num++;
                            blockAtPosition(3,y).toRemove=true;
                        }
                    }
                    else if (valueAtPosition(1,y)==valueAtPosition(2,y)&&isOccupied[2][y])
                    {
                        blockAtPosition(1,y).futureValue=blockAtPosition(1,y).BlockValue*2;
                        blockAtPosition(2,y).toRemove=true;
                        num++;
                    }
                    else if (valueAtPosition(2,y)==valueAtPosition(3,y)&&isOccupied[3][y])
                    {
                        blockAtPosition(2,y).futureValue=blockAtPosition(2,y).BlockValue*2;
                        blockAtPosition(3,y).toRemove=true;
                        num++;
                    }
                    else if (valueAtPosition(0,y)==valueAtPosition(2,y)&&(isOccupied[1][y]==false)&&isOccupied[2][y])
                    {
                        blockAtPosition(0,y).futureValue=blockAtPosition(0,y).BlockValue*2;
                        blockAtPosition(2,y).toRemove=true;
                        num++;
                    }
                    else if (valueAtPosition(1,y)==valueAtPosition(3,y)&&(isOccupied[2][y]==false)&&isOccupied[3][y])
                    {
                        blockAtPosition(1,y).futureValue=blockAtPosition(1,y).BlockValue*2;
                        blockAtPosition(3,y).toRemove=true;
                        num++;
                    }
                    else if (valueAtPosition(0,y)==valueAtPosition(3,y)&&(isOccupied[1][y]==false)&&(isOccupied[2][y]==false)&&isOccupied[3][y])
                    {
                        blockAtPosition(0,y).futureValue=blockAtPosition(0,y).BlockValue*2;
                        blockAtPosition(3,y).toRemove=true;
                        num++;
                    }
                }
                break;
        }
        return num;
    }

    public int valueAtPosition(int x, int y)
    {
        for (GameBlock block:BlockList)
        {
            if (block.xi==x && block.yi==y)
                return block.getBlockValue();
        }
        return -1;
    }

    public GameBlock blockAtPosition(int x, int y)
    {
        for (GameBlock block:BlockList)
        {
            if (block.xi==x && block.yi==y)
                return block;
        }
        return null;
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
                        if (allDoneMoving())
                        {
                            /*int index = 0;
                            for (GameBlock block:BlockList)
                            {
                                String i = String.format("Index: %d",index);
                                if (block.toRemove)
                                {
                                    Log.d("LEFT",i);
                                    Log.d("LEFT","first remove view");
                                    myLayout.removeView(block.blockText);
                                    Log.d("LEFT","2nd remove view");
                                    myLayout.removeView(block);
                                    Log.d("LEFT","3rd remove view");
                                    BlockList.remove(index);
                                    Log.d("LEFT","blocklist");
                                    BlockCount--;
                                    Log.d("LEFT","blockcount");

                                    index--;
                                }
                                index++;
                            }*/
                            for (int i = 0; i< BlockCount; i++)
                            {
                                String s = String.format("Index: %d",i);
                                GameBlock temp = BlockList.get(i);
                                if(BlockList.get(i).toRemove)
                                {
                                    Log.d("LEFT",s);
                                    myLayout.removeView(BlockList.get(i).blockText);
                                    myLayout.removeView(BlockList.get(i));
                                    BlockList.remove(i);
                                    Log.d("LEFT","blocklistRemove");
                                    i--;
                                    BlockCount--;
                                    Log.d("LEFT","finished");
                                }
                                else if (temp.futureValue>temp.BlockValue)
                                {
                                    temp.setValue(temp.futureValue);
                                }
                            }
                            Log.d("LEFT","finish to delete");
                        }
                    }
                }
        );
    }

}