package rs.project4420.connect4;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 *
 * Fragment za odabir igraca.
 */
public class ChoosePlayerFragment extends Fragment {

    private int type = -1;
    private Button playBtn;

    private ValueAnimator animator;

    private Context context;

    private ChoosePlayerFragmentListener callback;
    private int player;


    public interface ChoosePlayerFragmentListener {
        void onPlayButton(int type);
    }

    public ChoosePlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            type = savedInstanceState.getInt(Constants.ARG_TYPE);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_choose_player, container, false);
        playBtn = ((Button)rootView.findViewById(R.id.play_game_button));
        playBtn.setEnabled(false);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {callback.onPlayButton(type);}
        });

        // reference za dve tacke
        final View redDot = rootView.findViewById(R.id.dot_red);
        final View yellowDot = rootView.findViewById(R.id.dot_yellow);

        // liseneri za klik na tacke, pokretanje animacije
        redDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playBtn.setEnabled(true);
                if (animator != null && animator.isRunning()) {
                    animator.end();
                }
                player = Constants.PLAYER_1;
                dotAnimation(redDot);
            }
        });

        yellowDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playBtn.setEnabled(true);
                if (animator != null && animator.isRunning()) {
                    animator.end();
                }
                player = Constants.PLAYER_2;
                dotAnimation(yellowDot);
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            callback = (ChoosePlayerFragmentListener) getActivity();
        } catch (ClassCastException e){
            throw new ClassCastException("Activity must implement ChoosePlayerFragmentListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set type based on argument passed in
            type = args.getInt(Constants.ARG_TYPE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.ARG_TYPE, type);
    }

    // animacija sakupljanja/sirenja tacke kada je selektovana
    public void dotAnimation(final View dot){
        animator = ValueAnimator.ofFloat(0, (float) Math.PI);
        animator.setDuration(1100);
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
}
