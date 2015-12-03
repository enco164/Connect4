package rs.project4420.connect4;

/**
 * Created by enco on 4.11.15..
 *
 * Klasa koja sluzi da cuva podatke o jednom novcicu.
 *
 */
public class CoinItem {


    private int color;
    private int coinOwner;

    public CoinItem() {
        color = Constants.COIN_COLOR_GRID;
        coinOwner = Constants.COIN_OWNER_GRID;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getCoinOwner() {
        return coinOwner;
    }

    public void setCoinOwner(int coinOwner) {
        this.coinOwner = coinOwner;
        switch (coinOwner){
            case Constants.COIN_OWNER_GRID:
                this.color = Constants.COIN_COLOR_GRID;
                break;
            case Constants.PLAYER_1:
                this.color = Constants.COIN_COLOR_PL1;
                break;
            case Constants.PLAYER_2:
                this.color = Constants.COIN_COLOR_PL2;
                break;
        }
    }
}

