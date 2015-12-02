package rs.project4420.connect4;

/**
 * Created by enco on 4.11.15..
 */
public interface GameListener {
    void gameDataChanged(CoinItem[][] data);
    void computerThinkig();
    void computerPlayed();
}
