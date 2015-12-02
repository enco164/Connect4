package rs.project4420.connect4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by enco on 4.11.15..
 */
public class Connect4Adapter extends BaseAdapter{

    private static final String TAG = "Adapter";
    private final Game game;
    private Context mContext;

    public Connect4Adapter(Context context, Game game) {
        mContext = context;
        this.game = game;
    }

    @Override
    public int getCount() {
        return 7*6;
    }

    @Override
    public Object getItem(int position) {
        return game.getCoinData()[position/7][position%7];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.coin_item, parent, false);
        }
        ((CoinView) convertView).setColor(game.getCoinData()[position / 7][position % 7].getColor());
        return convertView;
    }

}

