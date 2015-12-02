package rs.project4420.connect4;

/**
 * Created by enco on 7.11.15..
 */

public class Evaluation {
    private static final String TAG = Evaluation.class.getSimpleName();

    public static int evalFunction(CoinItem[][] data, int kolona, int turn, int znak){
        int sum = 0;

        int red = -1;
        for (int i = 5; i >= 0; i--) {
            if (data[i][kolona].getCoinOwner() == turn) {
                red = i;
            }
        }

        if (red != -1){
            sum += proveriPoziciju(data, kolona, red, turn);
            sum*=10;
            //pobeda
            //sum += 10000*countOnOpenOneSide(data, 4, turn);
            //sum += -10000*countOnOpenOneSide(data, 4, -turn);
            if (countOnOpenOneSide(data, 4, turn) > 0) return (int)Double.POSITIVE_INFINITY;
            if (countOnOpenOneSide(data, 4, -turn) > 0) return (int)Double.NEGATIVE_INFINITY;
            //racunar
            sum += 10*countOnOpenOneSide(data, 2, turn);
            sum += 40*countOnOpenOneSide(data, 3, turn);
            sum += 20*countOnOpenTwoSides(data, 2, turn);
            sum += 60*countOnOpenTwoSides(data, 3, turn);
            //protivnik
            sum += -10*countOnOpenOneSide(data, 2, -turn);
            sum += -40*countOnOpenOneSide(data, 3, -turn);
            sum += -20*countOnOpenTwoSides(data, 2, -turn);
            sum += -60*countOnOpenTwoSides(data, 3, -turn);

        }

        return sum;
    }

    public static int proveriPoziciju(CoinItem[][] data, int kolona, int red, int turn) {
        int suma = 0;


        if (kolona == 0 || kolona == 6) { if (red == 0 || red == 5) suma=13; if (red == 1 || red == 4) suma=14; if (red == 2 || red ==3) suma=15; }
        if (kolona == 1 || kolona == 5) { if (red == 0 || red == 5) suma=14; if (red == 1 || red == 4) suma=16; if (red == 2 || red ==3) suma=18; }
        if (kolona == 2 || kolona == 4) { if (red == 0 || red == 5) suma=15; if (red == 1 || red == 4) suma=18; if (red == 2 || red ==3) suma=21;}
        if (kolona == 3){ if (red == 0 || red == 5) suma =17; if (red == 1 || red == 4) suma =20; if (red == 2 || red == 3) suma =23; }

        return suma;
    }

    public static int countOnOpenOneSide(CoinItem[][]data, int broj, int turn){
        int count = 0;

        //for each red
        for(int i = 0; i < 6; i++){
            int max = 1;
            for(int j = 0; j < 7; j++){
                if(data[i][j].getCoinOwner() != 0 && j>0 &&
                        data[i][j-1].getCoinOwner() == data[i][j].getCoinOwner() &&
                        data[i][j].getCoinOwner() == turn){
                    max++;
                    if(max >= broj) count++;
                } else max = 1;
            }
        }

        //for each kolonu
        for (int j = 0; j < 7; j++) {
            int max = 1;
            for (int i = 5; i >= 0; i--) {
                if (data[i][j].getCoinOwner() != 0 && i<5 &&
                        data[i+1][j].getCoinOwner() == data[i][j].getCoinOwner() &&
                        data[i][j].getCoinOwner() == turn){
                    max++;
                    if (max>=broj){
                        if (i>0 && data[i-1][j].getCoinOwner() ==0) count++;
                    }
                } else max = 1;
            }
        }

        //for each sporednaDijagonala
        for(int i = 0; i< 13; i++){
            int max = 1;
            int x = 0;
            int y = i;
            while (x <= 7 && y >= 0){
                if(x>=6 || x < 0 || y < 0 ||y >= 7) {
                    x++;
                    y--;
                    continue;
                }
                if(data[x][y].getCoinOwner() != 0 && x > 0 && y < 5 &&
                        data[x-1][y+1].getCoinOwner() == data[x][y].getCoinOwner() &&
                        data[x][y].getCoinOwner() == turn){
                    max++;
                    if(max >= broj) count++;
                } else max = 1;
                x++;
                y--;
            }
        }

        //for each glavnaDijagonala
        for(int i = 0; i < 13; i++){
            int max = 1;
            int x = 5;
            int y = i;

            while (x <= 7 && y >= 0){
                if(x>=6 || x < 0 || y < 0 ||y >= 7) {
                    x--;
                    y--;
                    continue;
                }

                if(data[x][y].getCoinOwner() !=0 && x > 0 && y > 0 &&
                        data[x-1][y-1].getCoinOwner() == data[x][y].getCoinOwner() &&
                        data[x][y].getCoinOwner() == turn){
                    max++;
                    if(max >= broj) {
                        if(( x >= broj && y < 6-broj &&
                                data[x-broj][y+broj].getCoinOwner() == 0) ||
                                ( x < 5 && y > 0 &&
                                        data[x+1][y-1].getCoinOwner() == 0))
                            count++;
                    }
                } else max = 1;
                x--;
                y--;
            }
        }

        return count;
    }

    public static int countOnOpenTwoSides(CoinItem[][]data, int broj, int turn){
        int count = 0;

        //for each red
        for(int i = 0; i < 6; i++){
            int max = 1;

            for(int j = 0; j<7; j++){
                if(data[i][j].getCoinOwner() != 0 && j>0 &&
                        data[i][j-1].getCoinOwner() == data[i][j].getCoinOwner() ){
                    max++;
                    if(max >= broj){
                        if((j > broj-1 && data[i][j-broj].getCoinOwner() == 0) ||
                                (j < 5 && data[i][j+1].getCoinOwner() == 0))
                            count++;
                    }
                }else{
                    max = 1;
                }
            }
        }

        //for each kolona
        for(int j = 0; j < 7; j++){
            int max = 1;
            for(int i = 0; i < 6; i++){
                if(data[i][j].getCoinOwner() != 0 && i > 0 &&
                        data[i-1][j].getCoinOwner() == data[i][j].getCoinOwner()){
                    max++;
                    if(max >= broj) {
                        count++;
                    }
                } else {
                    max = 1;
                }
            }
        }

        //for each sporednaDijagonala
        for(int i = 0; i < 6+7; i++){
            int max = 1;
            int x = 0;
            int y = i;
            while (x <= 7 && y>=0){
                if(x >= 6 || x < 0 || y < 0 || y >= 7) {
                    x++;
                    y--;
                    continue;
                }
                if (data[x][y].getCoinOwner() != 0 && x > 0 && y < 5 &&
                        data[x-1][y+1].getCoinOwner() == data[x][y].getCoinOwner() &&
                        data[x][y].getCoinOwner() == turn){
                    max++;
                    if(max >= broj) {
                        if(( x >= broj && y < 6-broj &&
                                data[x-broj][y+broj].getCoinOwner() == 0) ||
                                (x < 5 && y > 0 && data[x+1][y-1].getCoinOwner() == 0))
                            count++;
                    }
                } else {
                    max = 1;
                }
                x++;
                y--;
            }
        }

        for (int i = 0; i < 13; i++) {
            int max = 1;
            int x = 5;
            int y = i;
            while(x<=7 && y>=0){
                if (x>=6 || x<0 || y<0 || y>=7){
                    x--;
                    y--;
                    continue;
                }
                if (data[x][y].getCoinOwner() != 0 && x>0 && y>0 &&
                        data[x-1][y-1].getCoinOwner() == data[x][y].getCoinOwner() &&
                        data[x][y].getCoinOwner() == turn){
                    max++;
                    if (max>=broj){
                        if (x>broj && y>broj && data[x-broj][y-broj].getCoinOwner() == 0 && x<5 && y<3 && data[x+1][y+1].getCoinOwner() == 0)
                            count++;
                    }
                } else {
                    max = 1;
                }
                x--;
                y--;
            }
        }

        return count;
    }
}
