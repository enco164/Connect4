package rs.project4420.connect4;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by enco on 4.11.15..
 */
public class Game {
    private static final String TAG = Game.class.getSimpleName();

    private final Set<GameListener> gameListeners = new HashSet<>();

    private Connect4AI ai;
    private GameData gameData;

    public Game() {
        gameData = new GameData();

        //ai = new Connect4AI();
    }

    public CoinItem[][] getCoinData() {
        return gameData.getData();
    }

    public GameData getGameData() {
        return gameData;
    }

    public void setGameData(GameData gameData) {
        this.gameData = gameData;
        Log.d(TAG, "i = " + gameData.getLastI() + "; j = " + gameData.getLastJ());
        broadcastDataChanged();

    }

    public void setCoinData(CoinItem[][] data) {
        gameData.setData(data);
        broadcastDataChanged();
    }


    public void setType(int type) {
        if(Constants.ARG_AI == type)
            ai = new Connect4AI();
        gameData.setType(type);
        Log.d(TAG, "type: " + type);
    }

    public int getType() {
        return gameData.getType();
    }

    public int getTurnCount() {
        return gameData.getTurnCount();
    }

    public void setTurnCount(int turnCount) {
        gameData.setTurnCount(turnCount);
    }

    public void addGameListener(GameListener listener){
        gameListeners.add(listener);
    }

    public void removeGameListener(GameListener listener){
        gameListeners.remove(listener);
    }

    public boolean next(int col){
        if(col>6)return false;
        if(checkFinish()) return false;
        // check if valid move
        for (int i = 5; i >= 0; i--) {
            if(Constants.COIN_OWNER_GRID == gameData.getData()[i][col].getCoinOwner()){
                gameData.setLastI(i);
                gameData.setLastJ(col);

                gameData.getData()[i][col].setCoinOwner(gameData.turn);
                broadcastDataChanged();
                gameData.turn = -1 * gameData.turn;
                gameData.setTurnCount(gameData.getTurnCount()+1);
                logData();
                return true;
            }
        }
        return false;
    }

    public void nextComputer(){
        broadcastComputerThinking();

        new AINextMove().execute(this);
        gameData.setTurnCount(gameData.getTurnCount()+1);

    }

    public void setParticipants(ArrayList<String> participants) {
        gameData.setParticipants(participants);
    }

    private class AINextMove extends AsyncTask<Game, Void, Void> {

        @Override
        protected Void doInBackground(Game... params) {
            int[][] data = new int[6][7];
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 7; j++) {
                    data[i][j]=gameData.getData()[i][j].getCoinOwner();
                }
            }


            int ret = ai.getMinimaxNextStep(data);


                    //next(gameData.getData(), params[0].gameData.turn * (-1));
            for (int i = 5; i >= 0; i--) {
                if (gameData.getData()[i][ret].getCoinOwner() == Constants.COIN_OWNER_GRID) {
                    gameData.getData()[i][ret].setCoinOwner(gameData.turn);
                    gameData.setLastI(i);
                    gameData.setLastJ(ret);
                    broadcastComputerPlayed();
                    broadcastDataChanged();
                    params[0].gameData.turn = -1 * params[0].gameData.turn;
                    break;
                }
            }

            return null;
        }

    }

    private void broadcastDataChanged(){
        Set<GameListener> snapshot = new HashSet<>(gameListeners);
        for (GameListener listener : snapshot) {
            listener.gameDataChanged(gameData.getData());
        }
    }

    private void broadcastComputerThinking() {
        Set<GameListener> snapshot = new HashSet<>(gameListeners);
        for (GameListener listener : snapshot) {
            listener.computerThinkig();
        }
    }

    private void broadcastComputerPlayed() {
        Set<GameListener> snapshot = new HashSet<>(gameListeners);
        for (GameListener listener : snapshot) {
            listener.computerPlayed();
        }
    }

    public boolean checkFinish() {

        // check if full
        int m = 0;
        for (int i = 0; i < 7; i++) {
            if(gameData.getData()[0][i].getCoinOwner() != Constants.COIN_OWNER_GRID){
                m++;
            }
        }
        if(m==7){
            gameData.gameStatus = Constants.STATUS_DRAW;
            return true;
        }


        // check path -
        for (int i = 0; i < 6; i++) {
            int max = 1;
            for (int j = 1; j < 7; j++) {
                if(gameData.getData()[i][j].getCoinOwner() != Constants.COIN_OWNER_GRID &&
                        gameData.getData()[i][j-1].getCoinOwner() == gameData.getData()[i][j].getCoinOwner()){
                    max++;
                    gameData.addWinnerCoin(new Pair<Integer, Integer>(i, j));
                    if(max >= 4){
                        processStatus(gameData.getData()[i][j].getCoinOwner());
                        return true;
                    }
                } else {
                    gameData.clearWinnerCoins();
                    gameData.addWinnerCoin(new Pair<Integer, Integer>(i, j));
                    max = 1;
                }
            }
        }

        // check path |
        for (int j = 0; j < 7; j++) {
            int max = 1;
            for (int i = 1; i < 6; i++) {
                if(gameData.getData()[i][j].getCoinOwner() != Constants.COIN_OWNER_GRID &&
                        gameData.getData()[i-1][j].getCoinOwner() == gameData.getData()[i][j].getCoinOwner()){
                    gameData.addWinnerCoin(new Pair<Integer, Integer>(i, j));
                    max++;
                    if(max >= 4){
                        processStatus(gameData.getData()[i][j].getCoinOwner());
                        return true;
                    }
                } else {
                    gameData.clearWinnerCoins();
                    gameData.addWinnerCoin(new Pair<Integer, Integer>(i, j));
                    max = 1;
                }
            }
        }

        // check path /
        for(int n=0;n<(6+7);n++){
            int max = 1;

            int x = 0;
            int y = n;

            while (x <= 7 && y>=0){
                if(x>=6 ||x<0 ||y<0 ||y >= 7) {
                    x++;
                    y--;

                    continue;
                }
                if (gameData.getData()[x][y].getCoinOwner() != Constants.COIN_OWNER_GRID &&
                        x>0 && y< 7-1 &&
                        gameData.getData()[x-1][y+1].getCoinOwner()==gameData.getData()[x][y].getCoinOwner() ){
                    gameData.addWinnerCoin(new Pair<Integer, Integer>(x-1, y+1));
                    max++;
                    if(max >=4){
                        processStatus(gameData.getData()[x][y].getCoinOwner());
                        return true;
                    }
                }else{
                    gameData.clearWinnerCoins();
                    gameData.addWinnerCoin(new Pair<Integer, Integer>(x-1, y+1));
                    max = 1;
                }

                x++;
                y--;
            }
        }

        //check path \
        for(int n=0;n<(6+7);n++){
            int max = 1;

            int x = 6 - 1;
            int y = n;

            while (x <= 7 && y>=0){
                if(x>=6 ||x<0 ||y<0 ||y >= 7) {
                    x--;
                    y--;

                    continue;
                }
                if (gameData.getData()[x][y].getCoinOwner() != Constants.COIN_OWNER_GRID &&
                        x>0 && y>0 &&
                        gameData.getData()[x-1][y-1].getCoinOwner()==gameData.getData()[x][y].getCoinOwner() ){

                    gameData.addWinnerCoin(new Pair<Integer, Integer>(x, y));
                    max++;
                    if(max >=4) {
                        processStatus(gameData.getData()[x][y].getCoinOwner());
                        return true;
                    }
                }else{
                    gameData.clearWinnerCoins();
                    gameData.addWinnerCoin(new Pair<Integer, Integer>(x, y));
                    max = 1;
                }

                x--;
                y--;
            }
        }

        return false;

    }

    private void processStatus(int coinOwner) {
        gameData.gameStatus = (coinOwner == Constants.PLAYER_1) ? Constants.STATUS_P1_WIN : Constants.STATUS_P2_WIN;
    }

    public void logData(){

        for (int x = 0; x < 6; x++) {
            String s = "";
            for (int y = 0; y < 7; y++) {
                s+= " " + gameData.getData()[x][y].getCoinOwner();
            }
            Log.d(TAG, s);
        }
        Log.d(TAG, "-------");
    }

    public void initMultiplayer() {

    }

    @Override
    public String toString() {
        return "Game{" +
                "gameListeners=" + gameListeners +
                ", ai=" + ai +
                ", gameData=" + gameData +
                '}';
    }
}

