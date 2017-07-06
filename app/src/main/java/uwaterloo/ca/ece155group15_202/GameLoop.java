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

        GameBlock block1 = new GameBlock(myContext, myLayout);
        block1.setPosition((int )(Math.random()*3),(int )(Math.random()*3));
        block1.setImageResource(R.drawable.block);
        block1.setScaleX(0.66f);
        block1.setScaleY(0.65f);
        Log.d("CreateBlock", "Before adding");
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
    }


    public void setDirection(int direction){
        switch (direction) {
            case 0:
                for (GameBlock Block : BlockList) {
                    Block.moveUp();
                }
                break;
            case 1:
                for (GameBlock Block : BlockList) {
                    Block.moveRight();
                }
                break;
            case 2:
                for (GameBlock Block : BlockList) {
                    Block.moveDown();
                }
                break;
            case 3:
                for (GameBlock Block : BlockList) {
                    Block.moveLeft();
                }
                break;
        }
        if(BlockCount < 16){
            createBlock();

        }
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