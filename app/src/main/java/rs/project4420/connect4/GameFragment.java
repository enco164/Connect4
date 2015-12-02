package rs.project4420.connect4;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.animation.AnimatorListenerCompat;
import android.support.v4.animation.AnimatorUpdateListenerCompat;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment implements GameListener, AdapterView.OnItemClickListener {

    private static final String TAG = "GameFragment";
    private Connect4Adapter adapter;
    private Vibrator vibrator;
    private GridView tableView;
    private TextView statusText;

    GameFragmentListener callback;
    private int isMyTurn;
    private boolean multiplayer = false;
    private Context context;
    private Game game;
    private View rootView;
    private FrameLayout animatingDot;
    private ValueAnimator winningAnimator;
    private ValueAnimator animator;
    private boolean dooing;


    public interface GameFragmentListener{
        void onColumnClicked(int position);
    }

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        adapter = new Connect4Adapter(context, game);
        callback = (GameFragmentListener) getActivity();// TODO: surround try catch
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle args) {
        boolean enableTable = true;
        String status = "";
        if(args != null) {
            enableTable = args.getBoolean(Constants.ARG_ENABLE_TABLE);
            status = args.getString(Constants.ARG_STATUS_TEXT);
        }
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_game, container, false);
        tableView = (GridView)rootView.findViewById(R.id.table);
        tableView.setHorizontalSpacing(10);
        tableView.setVerticalSpacing(10);
        tableView.setAdapter(adapter);
        tableView.setOnItemClickListener(this);
        setEnableTable(enableTable);
        statusText = (TextView) rootView.findViewById(R.id.status_text);
        statusText.setText(status);

        winningAnimator = null;

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (winningAnimator != null && winningAnimator.isRunning()){
            winningAnimator.end();
            winningAnimator = null;
        }

    }

    @Override
    public void gameDataChanged(CoinItem[][] changedData) {
        while (dooing){}
        dooing = true;

        final Game gameFinal = game;
        boolean isFinish = false;



        if(gameFinal.checkFinish()){
            tableView.setEnabled(false);
            isFinish = true;
        }


        final boolean isEnabled = tableView.isEnabled();

        Log.d(TAG, "isEnabled: " + isEnabled);

        final int i = gameFinal.getGameData().getLastI();
        final int j = gameFinal.getGameData().getLastJ();

        final int tableViewWidth = tableView.getMeasuredWidth();

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) tableView.getLayoutParams();
        final int topMargin = lp.topMargin;

        final boolean finalIsFinish = isFinish;


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(animator!= null && animator.isRunning()){
                    animator.end();
                    animator = null;
                }

                if ( i > -1 || j > -1) {
                    tableView.setEnabled(false);
                    CoinView coin = new CoinView(context);
                    coin.setColor(gameFinal.getCoinData()[i][j].getColor());
                    coin.setLayoutParams(new FrameLayout.LayoutParams(tableViewWidth / 7, tableViewWidth / 7));

                    animatingDot = new FrameLayout(context);
                    animatingDot.setLayoutParams(new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                    ));
                    animatingDot.addView(coin);
                }

                if ( i > -1 || j > -1) {
                    ((FrameLayout) rootView).addView(animatingDot);

                    animator = ValueAnimator.ofFloat(0, 1);
                    animator.setDuration(1000);
                    animator.setInterpolator(new BounceInterpolator());
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float fraction = animation.getAnimatedFraction();
                            int x = (j * tableViewWidth / 7);
                            int y = (int) ((i * tableViewWidth / 7 + topMargin) * fraction);
                            animatingDot.setPadding(x, y, 0, 0);
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            dooing = false;
                            adapter.notifyDataSetChanged();
                            tableView.setEnabled(isEnabled);
                            if(animatingDot != null && animatingDot.getParent() != null) // iz nekog razloga (najv zbog asinhronog update sa servera) mora ovako...
                                ((ViewManager) animatingDot.getParent()).removeView(animatingDot);



                            if(finalIsFinish) {

                                winningAnimator = ValueAnimator.ofFloat(0, (float) Math.PI);
                                winningAnimator.setRepeatCount(ValueAnimator.INFINITE);
                                winningAnimator.setDuration(1000);
                                final float width = ((CoinView) tableView.getChildAt(0)).radius;
                                winningAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        float value = (float) animation.getAnimatedValue();
                                        for (Pair<Integer, Integer> pair : gameFinal.getGameData().getWinnerCoins()) {
                                            CoinView childAt = (CoinView) tableView.getChildAt(pair.first * 7 + pair.second);
                                            float v = (float) (Math.abs(Math.sin(value)) * width * .5f);
                                            childAt.radius = width - v;
                                            childAt.invalidate();
                                        }
                                    }
                                });
                                winningAnimator.start();
                            }
                        }
                    });
                    animator.start();
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public void computerThinkig() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText("Computer is Thinking");
            }
        });
    }

    @Override
    public void computerPlayed() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                statusText.setText("");
                tableView.setEnabled(true);
                Log.d(TAG, "computer Played!!");
                if(game.checkFinish()){
                    String status = "Player " + game.getGameData().gameStatus + " won.";
                    statusText.setText(status);
                    tableView.setEnabled(false);
                }

                synchronized (this){
                    this.notify();
                }
            }
        };
        synchronized (runnable){
            getActivity().runOnUiThread(runnable);
            try {
                runnable.wait();
            } catch (InterruptedException e) {

            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        callback.onColumnClicked(position%7);
    }

    public void setGameForAdapter(Game game){
        this.game = game;
    }

    public void setEnableTable(boolean enableTable){
        tableView.setEnabled(enableTable);
    }

    public void setStatusText(String status){
        this.statusText.setText(status);
    }

}
