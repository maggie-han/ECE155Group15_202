package uwaterloo.ca.ece155group15_202;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Maggie on 2017-06-29.
 */

public abstract class GameBlockTemplate extends ImageView{
    public GameBlockTemplate(Context c){
        super(c);
    }
    public abstract void setDestination();
    public abstract void move();
}
