package rs.project4420.connect4;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class Main2ActivityFragment extends Fragment  {

    private static final String TAG = "Main2ActivityFragment";
    private boolean signedIn;
    private View rootView;

    MainFragmentListener callback;
    public interface MainFragmentListener {
        void onPlayFriendButton();
        void onPlayAIButton();
        void onSignInButton();
        void onMultiplayerButton();
        void onMatchesButton();
    }

    public Main2ActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main2, container, false);
        rootView.findViewById(R.id.play_friend_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {callback.onPlayFriendButton();}
        });
        rootView.findViewById(R.id.play_AI_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {callback.onPlayAIButton();}
        });
        rootView.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {callback.onSignInButton();}
        });
        rootView.findViewById(R.id.multiplayer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {callback.onMultiplayerButton();}
        });
        rootView.findViewById(R.id.matches_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMatchesButton();
            }
        });
        rootView.findViewById(R.id.multiplayer_button).setVisibility(View.GONE);
        rootView.findViewById(R.id.matches_button).setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (MainFragmentListener) getActivity();
        } catch (ClassCastException e){
            throw new ClassCastException("Activity must implement MainFragmentListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isConnected = ((Main2Activity)getActivity()).getGoogleApiClient().isConnected();
        setSignedIn(isConnected);
    }

    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
        if(signedIn){
            rootView.findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            rootView.findViewById(R.id.multiplayer_button).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.matches_button).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.multiplayer_button).setVisibility(View.GONE);
            rootView.findViewById(R.id.matches_button).setVisibility(View.GONE);
        }


    }

}
