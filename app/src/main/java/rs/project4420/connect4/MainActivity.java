package rs.project4420.connect4;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;

public class MainActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{


    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_ROOM = 9002;
    private static final int RC_SELECT_PLAYERS = 9002;
    private static final int RC_LOOK_AT_MATCHES = 9003;


    private GoogleApiClient mGoogleApiClient;

    private boolean mResolvingConnectionFailure = false;
    private View mainScreen;
    private View signedinScreen;
    private View signInButton;
    private View gameScreen;
    private View choosePlayerScreen;
    private View playGameButton;
    private ValueAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        initScreans();
        initButtons();




    }

    private void initScreans() {
        mainScreen = findViewById(R.id.main_screen);
        signedinScreen = findViewById(R.id.signed_in_screen);
        gameScreen = findViewById(R.id.game_screen);
        choosePlayerScreen = findViewById(R.id.choose_player_screen);


        mainScreen.setVisibility(View.VISIBLE);
        signedinScreen.setVisibility(View.GONE);
        gameScreen.setVisibility(View.GONE);
        choosePlayerScreen.setVisibility(View.GONE);

    }

    private void initButtons() {

        signInButton = findViewById(R.id.sign_in_button);
        playGameButton = choosePlayerScreen.findViewById(R.id.play_game_button);
        playGameButton.setEnabled(false);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleApiClient.connect();
            }
        });

        findViewById(R.id.play_AI_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePlayerScreen(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });

        findViewById(R.id.play_friend_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePlayerScreen(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        });

        findViewById(R.id.matches_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Games.TurnBasedMultiplayer
                        .getInboxIntent(mGoogleApiClient);
                startActivityForResult(intent, RC_LOOK_AT_MATCHES);
            }
        });
        findViewById(R.id.multiplayer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Games.TurnBasedMultiplayer
                        .getSelectOpponentsIntent(mGoogleApiClient, 1, 1, true);
                startActivityForResult(intent, RC_ROOM);
            }
        });

        final View redDot = choosePlayerScreen.findViewById(R.id.dot_red);
        final View yellowDot = choosePlayerScreen.findViewById(R.id.dot_yellow);

        redDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playGameButton.setEnabled(true);
                if (animator != null && animator.isRunning()) {
                    animator.end();
                }
                //player = Constants.PLAYER_1;
                dotAnimation(redDot);
            }
        });

        yellowDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playGameButton.setEnabled(true);
                if (animator != null && animator.isRunning()) {
                    animator.end();
                }
                //player = Constants.PLAYER_2;
                dotAnimation(yellowDot);
            }
        });
    }

    private void showChoosePlayerScreen(View.OnClickListener playGameButtonOnClickListener) {
        choosePlayerScreen.setVisibility(View.VISIBLE);
        signedinScreen.setVisibility(View.GONE);
        mainScreen.setVisibility(View.GONE);
        gameScreen.setVisibility(View.GONE);

        playGameButton.setOnClickListener(playGameButtonOnClickListener);
    }

    public void dotAnimation(final View dot){
        animator = ValueAnimator.ofFloat(0, (float) Math.PI);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        final float width = dot.getLayoutParams().width;

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float value = (float) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = dot.getLayoutParams();
                params.width = (int) (width - (float) (Math.abs(Math.sin(value)) * width * .5f));
                params.height = params.width;
                dot.setLayoutParams(params);
                dot.invalidate();
            }
        });
        animator.start();
    }



    @Override
    protected void onStart() {
        super.onStart();
        //Log.d(TAG, "onStart(): Connecting to Google APIs");
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(): Disconnecting from Google APIs");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to Google API");
        signInButton.setVisibility(View.GONE);
        signedinScreen.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended():  Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // Already resolving
            return;
        }
        Log.d(TAG, "connection failed");

        // If the sign in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        mResolvingConnectionFailure = true;

        // Attempt to resolve the connection failure using BaseGameUtils.
        // The R.string.signin_other_error value should reference a generic
        // error string in your strings.xml file, such as "There was
        // an issue with sign in, please try again later."
        if (!BaseGameUtils.resolveConnectionFailure(this,
                mGoogleApiClient, connectionResult,
                RC_SIGN_IN, "error message")) {
            mResolvingConnectionFailure = false;
        }
    }


    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if (request == RC_SIGN_IN) {
            mResolvingConnectionFailure = false;
            if (response == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, request, response, R.string.signin_other_error);
            }
        }
        if(request == RC_ROOM){
            if(response == Activity.RESULT_OK){
                final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

                TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                        .addInvitedPlayers(invitees).build();
                Games.TurnBasedMultiplayer
                        .createMatch(mGoogleApiClient, tbmc)
                        .setResultCallback(new MatchInitiatedCallback());

                showGameUI();

                /*
                Intent intent = new Intent(this, ChoosePlayerActivity.class);
                TurnBasedMatch match = data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);
                intent.putExtra("match", match);
                intent.putStringArrayListExtra("invitees", invitees);
                intent.putExtra("multiplayer", true);
                startActivity(intent);
                */
            }
        }
        if (request == RC_LOOK_AT_MATCHES) {

            // Returning from the 'Select Match' dialog
            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            TurnBasedMatch match = data
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                /*
                final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
                Log.d(TAG, "match != null, " + match.getParticipantIds());
                //TODO pravljenje poteza za mec iz inboxa
                Intent intent = new Intent(this, Connect4Activity.class);
                intent.putExtra("multiplayer", true);
                startActivity(intent);
                */
            }
        }


    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    private void showGameUI() {
        gameScreen.setVisibility(View.VISIBLE);
        mainScreen.setVisibility(View.GONE);
        signedinScreen.setVisibility(View.GONE);
    }


    private class MatchInitiatedCallback
        implements ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>
    {
        @Override
        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {/*
            Status status = result.getStatus();
            if (!status.isSuccess()) {
                statusText.setText(""+status.getStatusCode());
                return;
            }

            TurnBasedMatch match = result.getMatch();
            Log.d(TAG, "==========parts: " + match.getParticipantIds().toString());

            // If this player is not the first player in this match, continue.
            if (match.getCoinData() != null) {
                game.unpersist(match.getCoinData());
                // Second player is in game
                if(game.getTurnCount() == 1){
                    game.setTurnCount(game.getTurnCount() + 1);
                    Games.TurnBasedMultiplayer
                            .takeTurn(mGoogleApiClient,
                                    match.getMatchId(),
                                    game.persist(),
                                    match.getParticipantId(opponentID))
                            .setResultCallback(updateMatchResult);
                    return;
                }


                progress.cancel();
                return;
            }

*/
        }
    }
}
