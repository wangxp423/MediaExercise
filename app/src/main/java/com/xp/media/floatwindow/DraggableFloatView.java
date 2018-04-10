package com.xp.media.floatwindow;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
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
        screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        floatWindowWidth = getWidth();
        LogUtils.d("Test", "floatWindowWidth = " + floatWindowWidth);
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
    int leftDistance, rightDistance;
    int screenWidth, floatWindowWidth;
    int translationTotalX;//在X轴的滑动偏移

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            LogUtils.d("Test", "ACTION_DOWN");
            floatWindowWidth = getWidth();
            LogUtils.d("Test", "floatWindowWidth = " + floatWindowWidth);
            startTime = System.currentTimeMillis();
            downX = ev.getRawX();
            downY = ev.getRawY();
            leftDistance = (int) (downX - ev.getX());
            rightDistance = screenWidth - leftDistance - getWidth();
            LogUtils.d("Test", "leftDistance = " + leftDistance + "   rightDistance = " + rightDistance);
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//            LogUtils.d("Test", "ACTION_MOVE");
            moveX = ev.getRawX();
            moveY = ev.getRawY();
            setDragParams();
            downX = moveX;
            downY = moveY;
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
//            LogUtils.d("Test", "ACTION_UP");
            setFloatWindowDismiss();
            translationTotalX = 0;
            LogUtils.d("Test", "getWidth = " + getWidth());
            endTime = System.currentTimeMillis();
            //当从点击到弹起小于半秒的时候,则判断为点击,如果超过则不响应点击事件
            if ((endTime - startTime) > 0.5 * 1000L) {
                LogUtils.d("Test", "time = true");
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setDragParams() {
        int translationX = 0;
        if (mOnFlingListener != null) {
            translationX = Math.round(moveX - downX);
            mOnFlingListener.onMove(translationX, moveY - downY);
        }
        translationTotalX = translationTotalX + translationX;
        LogUtils.d("Test", "translationTotalX = " + translationTotalX);
        if (translationTotalX > 0 && translationTotalX >= rightDistance) {
            setRightMarginLayoutParams(translationX);
        } else if (translationTotalX < 0 && Math.abs(translationTotalX) >= leftDistance) {
            setLeftMarginLayoutParams(translationX);
        }
    }

    private void setRightMarginLayoutParams(int translationX) {
        LayoutParams params = (LayoutParams) flFloatContrain.getLayoutParams();
        params.rightMargin = params.rightMargin - translationX;
        flFloatContrain.setLayoutParams(params);
    }

    private void setLeftMarginLayoutParams(int translationX) {
        LayoutParams params = (LayoutParams) flFloatContrain.getLayoutParams();
        params.leftMargin = params.leftMargin + translationX;
        flFloatContrain.setLayoutParams(params);
    }

    private void resetLayoutParams() {
        LayoutParams params = (LayoutParams) flFloatContrain.getLayoutParams();
        params.rightMargin = 0;
        params.leftMargin = 0;
        flFloatContrain.setLayoutParams(params);
    }

    //如果移除屏幕距离大于窗口宽度的一半 则移除悬浮窗
    private void setFloatWindowDismiss() {
        if (translationTotalX > 0 && translationTotalX >= rightDistance && (translationTotalX - rightDistance) > floatWindowWidth / 2) {
            DraggableFloatWindowManager.getInstance().removeFloatWindow();
            resetWindowManagerLayoutParamsAtRight();
        } else if (translationTotalX < 0 && Math.abs(translationTotalX) >= leftDistance && (Math.abs(translationTotalX) - leftDistance) > floatWindowWidth / 2) {
            DraggableFloatWindowManager.getInstance().removeFloatWindow();
            resetWindowManagerLayoutParamsAtLeft();
        } else {
            if (translationTotalX > 0 && translationTotalX >= rightDistance && (translationTotalX - rightDistance) < floatWindowWidth / 2) {
                resetWindowManagerLayoutParamsAtRight();
            } else if (translationTotalX < 0 && Math.abs(translationTotalX) >= leftDistance && (Math.abs(translationTotalX) - leftDistance) < floatWindowWidth / 2) {
                resetWindowManagerLayoutParamsAtLeft();
            }
            resetLayoutParams();
        }
    }

    private void resetWindowManagerLayoutParamsAtLeft() {
        DraggableFloatWindowManager.getInstance().getWindowManagerParams().x = 0;

    }

    private void resetWindowManagerLayoutParamsAtRight() {
        DraggableFloatWindowManager.getInstance().getWindowManagerParams().x = screenWidth - floatWindowWidth;
    }

    int offset;

    private void setActionUpAnimation() {
        LogUtils.d("Test", "getWidth = " + getWidth() + "   floatWindowWidth = " + floatWindowWidth);
        ValueAnimator animator = ValueAnimator.ofInt(getWidth(), floatWindowWidth);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(600);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (int) animation.getAnimatedValue() - offset;
                if (translationTotalX > 0 && translationTotalX >= rightDistance) {
                    setRightMarginLayoutParams(offset);
                } else if (translationTotalX < 0 && Math.abs(translationTotalX) >= leftDistance) {
                    setLeftMarginLayoutParams(-offset);
                }
                offset = (int) animation.getAnimatedValue();
                if (offset == floatWindowWidth) {
                    DraggableFloatWindowManager.getInstance().removeFloatWindow();
                }
            }
        });
        animator.start();
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
