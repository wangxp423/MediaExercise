package com.xp.media.floatwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.xp.media.R;
import com.xp.media.util.LogUtils;

/**
 * @类描述：
 * @创建人：Wangxiaopan
 * @创建时间：2018/4/3 0003 15:15
 * @修改人：
 * @修改时间：2018/4/3 0003 15:15
 * @修改备注：
 */

@SuppressLint("ViewConstructor")
public class DraggableFloatView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = DraggableFloatView.class.getSimpleName();

    private Context mContext;
    private FrameLayout flFloatContrain;
    private OnFlingListener mOnFlingListener;
    private OnTouchButtonClickListener mTouchButtonClickListener;

    public DraggableFloatView(Context context, OnFlingListener flingListener, View popWin) {
        super(context);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.floatwindow_contrain_layout, this);
        flFloatContrain = (FrameLayout) findViewById(R.id.fl_float_contrain);
        flFloatContrain.addView(popWin);
        mOnFlingListener = flingListener;
//        mTouchBt.setOnClickListener(this);
    }

    private void testSomething() {
        LogUtils.d("Test", "flFloatContrain.width = " + flFloatContrain.getWidth() + "   flFloatContrain.height = " + flFloatContrain.getHeight());
        LogUtils.d("Test", "flFloatContrain.x = " + flFloatContrain.getX() + "   flFloatContrain.y = " + flFloatContrain.getY());
        LayoutParams params = (LayoutParams) flFloatContrain.getLayoutParams();
        LogUtils.d("Test", "leftMargin = " + params.leftMargin + "   topMargin = " + params.topMargin);
        params.leftMargin = params.leftMargin - 20;
        flFloatContrain.setLayoutParams(params);

    }

    float downX, downY;
    float moveX, moveY;
    long startTime, endTime;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            LogUtils.d("Test", "ACTION_DOWN");
//            testSomething();
            startTime = System.currentTimeMillis();
            downX = ev.getRawX();
            downY = ev.getRawY();
            LogUtils.d("Test", "downX " + downX + "   downY = " + downY);
            LogUtils.d("Test", "getX " + ev.getX() + "   getY = " + ev.getY());
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//            LogUtils.d("Test", "ACTION_MOVE");
            moveX = ev.getRawX();
            moveY = ev.getRawY();
            if (mOnFlingListener != null)
                mOnFlingListener.onMove(moveX - downX, moveY - downY);
            downX = moveX;
            downY = moveY;
            return true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
//            LogUtils.d("Test", "ACTION_UP");
            endTime = System.currentTimeMillis();
            //当从点击到弹起小于半秒的时候,则判断为点击,如果超过则不响应点击事件
            if ((endTime - startTime) > 0.5 * 1000L) {
                LogUtils.d("Test", "time = true");
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setTouchButtonClickListener(OnTouchButtonClickListener touchButtonClickListener) {
        mTouchButtonClickListener = touchButtonClickListener;
    }

    @Override
    public void onClick(View v) {
        if (mTouchButtonClickListener != null) {
            mTouchButtonClickListener.onClick(v);
        }
    }

    public interface OnTouchButtonClickListener {
        void onClick(View view);
    }
}
