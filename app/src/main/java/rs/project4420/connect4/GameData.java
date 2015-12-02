package rs.project4420.connect4;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by enco on 8.11.15..
 */
public class GameData implements Parcelable{
    private static final String TAG = "GameData";
    public int turn;

    private CoinItem[][] data;

    private int turnCount = 0;

    public int gameStatus = -1;

    private int type;
    private ArrayList<String> participants;

    private int lastI;
    private int lastJ;

    private ArrayList<Pair<Integer, Integer>> winnerCoins;


    public GameData() {
        this.turn = 1;
        this.turnCount = 0;
        data = new CoinItem[6][7];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                data[i][j] = new CoinItem();
                /*if(j == 3){
                    if (i > 2)
                        data[i][j].setCoinOwner(Constants.PLAYER_1);
                }*/
            }
        }
        lastI = lastJ = -1;
        winnerCoins = new ArrayList<Pair<Integer, Integer>>();
    }

    public void setData(CoinItem[][] data) {
        this.data = data;
    }

    public CoinItem[][] getData() {
        return data;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public int getLastI() {
        return lastI;
    }

    public int getLastJ() {
        return lastJ;
    }


    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(turn);
        dest.writeInt(turnCount);
        dest.writeInt(gameStatus);
        dest.writeInt(type);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                int coinOwner = data[i][j].getCoinOwner();
                dest.writeInt(coinOwner);
            }
        }
        dest.writeInt(lastI);
        dest.writeInt(lastJ);

        int n = winnerCoins.size();
        dest.writeInt(n);
        for (int i = 0; i < n; i++) {
            dest.writeInt(winnerCoins.get(i).first);
            dest.writeInt(winnerCoins.get(i).second);
        }
        //dest.writeStringList(participants);
    }

    public static final Parcelable.Creator<GameData> CREATOR
            = new Parcelable.Creator<GameData>(){

        @Override
        public GameData createFromParcel(Parcel source) {
            return new GameData(source);
        }

        @Override
        public GameData[] newArray(int size) {
            return new GameData[size];
        }
    };

    private GameData(Parcel source){
        this.turn = source.readInt();
        this.turnCount = source.readInt();
        this.gameStatus = source.readInt();
        this.type = source.readInt();
        this.data = new CoinItem[6][7];

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                data[i][j] = new CoinItem();
                int coinOwner = source.readInt();
                data[i][j].setCoinOwner(coinOwner);
            }
        }

        this.lastI = source.readInt();
        this.lastJ = source.readInt();

        winnerCoins = new ArrayList<Pair<Integer, Integer>>();

        int n = source.readInt();
        for (int i = 0; i < n; i++) {
            winnerCoins.add(new Pair<Integer, Integer>(source.readInt(), source.readInt()));
        }

        //source.readStringList(this.participants);
    }

    public byte[] persist(){
        JSONObject retVal = new JSONObject();
        try {
            retVal.put("turn", turn);
            retVal.put("turnCount", turnCount);
            retVal.put("gameStatus", gameStatus);
            JSONArray board = new JSONArray();
            for (int i = 0; i < 6; i++) {
                JSONArray row = new JSONArray();
                for (int j = 0; j < 7; j++) {
                    row.put(data[i][j].getCoinOwner());
                }
                board.put(row);
            }
            retVal.put("data", board);
            Log.d(TAG, "PERSIST: " + retVal.toString());
            return retVal.toString().getBytes(Charset.forName("UTF-8"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void unpersist(byte[] bytes){
        if(bytes == null) return;
        String s = null;
        try {
            s = new String(bytes, "UTF-8");
            Log.d(TAG, s);
            JSONObject object = new JSONObject(s);

            if(object.has("turn")) this.turn = object.getInt("turn");
            if(object.has("turnCount")) this.turnCount = object.getInt("turnCount");
            if(object.has("gameStatus")) this.gameStatus = object.getInt("gameStatus");
            if(object.has("data")) {
                CoinItem[][] coins = new CoinItem[6][7];
                JSONArray d = object.getJSONArray("data");
                for (int i = 0; i < d.length(); i++) {
                    JSONArray row = d.getJSONArray(i);
                    for (int j = 0; j < 7; j++) {
                        coins[i][j] = new CoinItem();
                        coins[i][j].setCoinOwner(row.getInt(j));
                    }
                }
                data = coins;
            }
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "GameData{" +
                "turn=" + turn +
                ", data=" + Arrays.toString(data) +
                ", turnCount=" + turnCount +
                ", gameStatus=" + gameStatus +
                ", type=" + type +
                ", participants=" + participants +
                '}';
    }

    public void setLastI(int lastI) {
        this.lastI = lastI;
    }

    public void setLastJ(int lastJ) {
        this.lastJ = lastJ;
    }

    public ArrayList<Pair<Integer, Integer>> getWinnerCoins() {
        return winnerCoins;
    }

    public void addWinnerCoin (Pair<Integer, Integer> coin){
        winnerCoins.add(coin);
    }

    public void clearWinnerCoins(){
        winnerCoins.clear();
    }
}
