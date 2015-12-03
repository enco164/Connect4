package rs.project4420.connect4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by enco on 4.11.15..
 *
 * Graficka reprezentacija jednog novcica.
 * Uparuje se sa CoinItem klasom
 *
 */
public class CoinView extends View {



    private static final String TAG = "CoinView";
    Paint paint;
    public float radius;
    private float cx;
    private float cy;


    private float paddingLeft;
    private float paddingTop;

    public CoinView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.RED);
    }

    public CoinView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(Color.RED);
    }

    public CoinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(cx, cy, radius *.38f, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        radius = getMeasuredWidth();
        cx = getMeasuredHeight()/2.0f + paddingLeft;
        cy = getMeasuredHeight()/2.0f + paddingTop;
    }

    public void setColor(int color) {
        paint.setColor(getResources().getColor(color));
    }

    public void setColorInt(int color){
        paint.setColor(color);
    }

    public void setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public void setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
    }

}
