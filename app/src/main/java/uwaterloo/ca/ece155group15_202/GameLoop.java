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
        updateFutureOccupancy();
        GameBlock block1 = new GameBlock(myContext, myLayout);

        int tempx, tempy;
        tempx = (int )(Math.random()*3);
        tempy = (int )(Math.random()*3);
        while (willBeOccupied[tempx][tempy])
        {
            tempx = (int )(Math.random()*3);
            tempy = (int )(Math.random()*3);
        }
        block1.setPosition(tempx,tempy);
        block1.setImageResource(R.drawable.gameblockblueborder);

        block1.setScaleX(0.66f);
        block1.setScaleY(0.65f);
        Log.d("TV", "setting random value gameloop");
        block1.setValue();
        Log.d("CreateBlock", "Before adding");
        //if (allDoneMoving())
            myLayout.addView (block1);
        Log.d("CreateBlock", "added to view");
        if(BlockCount == 0){
            BlockList.addFirst(block1);
        }
        else{
            BlockList.add(block1);
        }

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
                        Block.moveLeft(numOccupied(Block.xi, Block.yi, 3));
                    }
                    break;
            }
            updateOccupancy();
            if (BlockCount < 16) {
                createBlock();
            }
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
    public void run (){
        myActivity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d("CreateBlock","run");
                        for (GameBlock Block : BlockList) {
                            Block.move();
                        }
                    }
                }
        );
    }

}