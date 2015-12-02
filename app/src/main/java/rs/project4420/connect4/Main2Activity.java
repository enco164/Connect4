package rs.project4420.connect4;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity
        implements Main2ActivityFragment.MainFragmentListener,
        ChoosePlayerFragment.ChoosePlayerFragmentListener,
        GameFragment.GameFragmentListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnTurnBasedMatchUpdateReceivedListener {

    private static final String TAG = "Main2Activity";

    private static final int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;

    private GoogleApiClient mGoogleApiClient;

    // Current turn-based match
    private TurnBasedMatch match;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    private Game game;
    private Vibrator vibrator;
    private GameFragment gameFragment;
    private ArrayList<String> participants;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        game = new Game();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            Main2ActivityFragment firstFragment = new Main2ActivityFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment, "MainFragmentTag").commit();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart(): Connecting to Google APIs");
        mGoogleApiClient.connect();
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
    public void onConnected(Bundle connectionHint) {

        // As a demonstration, we are registering this activity as a handler for
        // invitation and match events.

        // This is *NOT* required; if you do not register a handler for
        // invitation events, you will get standard notifications instead.
        // Standard notifications may be preferable behavior in many cases.
        //Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

        // Likewise, we are registering the optional MatchUpdateListener, which
        // will replace notifications you would get otherwise. You do *NOT* have
        // to register a MatchUpdateListener.
        Games.TurnBasedMultiplayer.registerMatchUpdateListener(mGoogleApiClient, this);

        Main2ActivityFragment fragment = (Main2ActivityFragment) getSupportFragmentManager()
                .findFragmentByTag("MainFragmentTag");
        if (fragment != null && fragment.isVisible()) {
            fragment.setSignedIn(true);
        }

        Log.d(TAG, "onConnected(): Connection successful");

        // Retrieve the TurnBasedMatch from the connectionHint
        if (connectionHint != null) {
            Log.d(TAG, "connection hint not null");
            match = connectionHint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);

            // If this player is not the first player in this match, continue.
            if (match != null && match.getData() != null) {

                if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                    Log.d(TAG, "Warning: accessing TurnBasedMatch when not connected");
                }
                processResult();

                if (game.getTurnCount() < 2) {
                    game.setTurnCount(game.getTurnCount() + 1);
                    Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient,
                            match.getMatchId(),
                            ParcelableUtil.marshall(game.getGameData()),
                            getNextParticipantId())
                            .setResultCallback(new UpdateMatchCallback());
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended():  Trying to reconnect.");
        mGoogleApiClient.connect();
        //setViewVisibility();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            // Already resolving
            Log.d(TAG, "onConnectionFailed(): ignoring connection failure, already resolving.");
            return;
        }

        // Launch the sign-in flow if the button was clicked or if auto sign-in is enabled
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;

            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult, RC_SIGN_IN,
                    getString(R.string.signin_other_error));
        }
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if (request == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (response == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, request, response, R.string.signin_other_error);
            }
        } else if (request == RC_SELECT_PLAYERS) {
            Log.d(TAG,"Returned from 'Select players to Invite' dialog");

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            final ArrayList<String> invitees = data
                    .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees).build();

            // Start the match
            Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, tbmc)
                    .setResultCallback(new MatchInitiatedCallback());
            progress = new ProgressDialog(this);
            progress.setMessage("Initiating game...");
            progress.show();

        } else if (request == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (response != Activity.RESULT_OK) {// user canceled
                return;
            }

            match = data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                processResult();

                if (game.getTurnCount() < 2) {
                    game.setTurnCount(game.getTurnCount() + 1);
                    Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient,
                            match.getMatchId(),
                            ParcelableUtil.marshall(game.getGameData()),
                            getNextParticipantId())
                            .setResultCallback(new UpdateMatchCallback());
                }
            }
        }
    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
        if(match != null && match.getMatchId().equalsIgnoreCase(turnBasedMatch.getMatchId()) ) {
            match = turnBasedMatch;
            processResult();
            Toast.makeText(Main2Activity.this, "Your turn!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Main2Activity.this, "Another game received", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {
        Toast.makeText(Main2Activity.this, s, Toast.LENGTH_SHORT).show();
    }

    private class MatchInitiatedCallback implements
            ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> {

        @Override
        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
            Log.d(TAG, "=======MATCH INITIATED=======");
            Status status = result.getStatus();
            if (!status.isSuccess()) {
                // TODO: showError(status.getStatusCode());
                return;
            }

            match = result.getMatch();

            // If this player is not the first player in this match, continue.
            if (match.getData() != null) {

                processResult();

                if(game.getTurnCount() < 2) {
                    game.setTurnCount(game.getTurnCount()+1);
                    Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient,
                            match.getMatchId(),
                            ParcelableUtil.marshall(game.getGameData()),
                            getNextParticipantId())
                            .setResultCallback(new UpdateMatchCallback());
                }
                return;
            }

            // Otherwise, this is the first player. Initialize the game state.
            initGame(match);
        }
    }

    private class UpdateMatchCallback implements ResultCallback<TurnBasedMultiplayer.UpdateMatchResult> {
        @Override
        public void onResult(TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
            Log.d(TAG, "=======MATCH UPDATED=======");
            match = updateMatchResult.getMatch();
            processResult();
        }
    }

    private void initGame(TurnBasedMatch match) {
        participants = match.getParticipantIds();
        Log.d(TAG, "init GAME: participants: " + participants.toString());
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_TYPE, Constants.ARG_CREATOR_MULTIPLAYER);
        if(progress!= null) progress.dismiss();
        changeFragment(new ChoosePlayerFragment(), args, "GameFragmentTag", true);
    }



    private void processResult(){
        GameFragment fragment = (GameFragment) getSupportFragmentManager()
                .findFragmentByTag("GameFragmentTag");
        boolean enableTable =
                TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN == match.getTurnStatus();

        if (fragment == null || !fragment.isVisible()){
            Bundle args = new Bundle();
            args.putBoolean(Constants.ARG_ENABLE_TABLE, enableTable);
            createAndBindMVC(Constants.ARG_INVITED, match.getData(), args);
        } else {
            gameFragment.setEnableTable(enableTable);
            game.setGameData(GameData.CREATOR.createFromParcel(
                    ParcelableUtil.unmarshall(match.getData())));
            if(game.checkFinish()) {
                String status = "Player " + game.getGameData().gameStatus + " won.";
                gameFragment.setStatusText(status);
                gameFragment.setEnableTable(false);
            }
        }
    }

    @Override
    public void onColumnClicked(int position) {
        if(game.getType() != Constants.ARG_FRIEND)
            gameFragment.setEnableTable(false);

        if(!game.next(position)){
            vibrator.vibrate(75);
            gameFragment.setEnableTable(true);
        } else {
            switch (game.getType()) {
                case Constants.ARG_FRIEND:
                    if (game.checkFinish()) {
                        gameFragment.setEnableTable(false);
                        StringBuilder msg = new StringBuilder("Player");
                        String status = "Player " + game.getGameData().gameStatus + " won.";
                        gameFragment.setStatusText(status);
                    }
                    break;
                case Constants.ARG_AI:

                    if (game.checkFinish()) {
                        String status = "Player " + game.getGameData().gameStatus + " won.";
                        gameFragment.setStatusText(status);
                    } else {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    game.nextComputer();
                                                }
                                            }
                        , 900);
                    }
                    break;
                case Constants.ARG_CREATOR_MULTIPLAYER:
                case Constants.ARG_INVITED:

                    if (game.checkFinish()) {
                        String status = "Player " + game.getGameData().gameStatus + " won.";
                        gameFragment.setStatusText(status);
                        Games.TurnBasedMultiplayer.finishMatch(
                                mGoogleApiClient,
                                match.getMatchId(),
                                ParcelableUtil.marshall(game.getGameData()))
                                .setResultCallback(new UpdateMatchCallback());
                        return;
                    }
                    Games.TurnBasedMultiplayer.takeTurn(
                            mGoogleApiClient,
                            match.getMatchId(),
                            ParcelableUtil.marshall(game.getGameData()),
                            getNextParticipantId());
                    break;
            }
        }
    }

    private void changeFragment(Fragment fragment, Bundle args, String tag, boolean addToBackStack){

        if(!addToBackStack) getSupportFragmentManager().popBackStack();

        if(args != null) fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (tag == null) {
            transaction.replace(R.id.fragment_container, fragment);
        } else {
            transaction.replace(R.id.fragment_container, fragment, tag);
        }
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private void createAndBindMVC(int type, byte[] data, Bundle args) {
        gameFragment = new GameFragment();
        game = new Game();

        game.setGameData(GameData.CREATOR.createFromParcel(ParcelableUtil.unmarshall(data)));
        game.addGameListener(gameFragment);
        gameFragment.setGameForAdapter(game);

        if(game.checkFinish()) {
            String status = "Player " + game.getGameData().gameStatus + " won.";
            args.putBoolean(Constants.ARG_ENABLE_TABLE, false);
            args.putString(Constants.ARG_STATUS_TEXT, status);
        }

        changeFragment(gameFragment, args, "GameFragmentTag", true);
    }

    public String getNextParticipantId() {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = match.getParticipantId(playerId);

        ArrayList<String> participantIds = match.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (match.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    @Override
    public void onPlayButton(int type) {
        gameFragment = new GameFragment();
        game = new Game();
        game.setType(type);
        game.addGameListener(gameFragment);
        gameFragment.setGameForAdapter(game);
        changeFragment(gameFragment, null, "GameFragmentTag", false);
        if(type == Constants.ARG_CREATOR_MULTIPLAYER){
            game.setTurnCount(game.getTurnCount() + 1);
            game.setParticipants(participants);
            Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient,
                    match.getMatchId(),
                    ParcelableUtil.marshall(game.getGameData()),
                    getNextParticipantId())
                    .setResultCallback(new UpdateMatchCallback());
        }
    }

    @Override
    public void onPlayFriendButton() {
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_TYPE, Constants.ARG_FRIEND);

        changeFragment(new ChoosePlayerFragment(), args, null, true);
    }

    @Override
    public void onPlayAIButton() {
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_TYPE, Constants.ARG_AI);

        changeFragment(new ChoosePlayerFragment(), args, null, true);
    }

    @Override
    public void onSignInButton() {
        mSignInClicked = true;
        match = null;
        mGoogleApiClient.connect();
    }

    @Override
    public void onMultiplayerButton() {
        Intent intent = Games.TurnBasedMultiplayer
                .getSelectOpponentsIntent(mGoogleApiClient, 1, 1, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    @Override
    public void onMatchesButton() {
        Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

}
