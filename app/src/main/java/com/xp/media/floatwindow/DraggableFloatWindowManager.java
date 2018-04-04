package com.xp.media.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.xp.media.util.App;
import com.xp.media.util.LogUtils;


/**
 * @类描述：悬浮窗 管理类
 * @创建人：Wangxiaopan
 * @创建时间：2018/4/3 0003 15:15
 * @修改人：
 * @修改时间：2018/4/3 0003 15:15
 * @修改备注：
 */

public class DraggableFloatWindowManager {

    private static final String TAG = DraggableFloatWindowManager.class.getSimpleName();
    private static DraggableFloatWindowManager mDraggableFloatWindow;
    private WindowManager.LayoutParams mParams = null;
    private WindowManager mWindowManager = null;
    private DraggableFloatView mDraggableFloatView;
    private Context mContext;

    private DraggableFloatWindowManager() {
        mContext = App.INSTANCE;
        initWindowManagerParams();
    }

    public static DraggableFloatWindowManager getInstance() {
        if (mDraggableFloatWindow == null) {
            synchronized (DraggableFloatWindowManager.class) {
                if (mDraggableFloatWindow == null) {
                    mDraggableFloatWindow = new DraggableFloatWindowManager();
                }
            }
        }
        return mDraggableFloatWindow;
    }

    //初始化WindowManager 相关参数
    private void initWindowManagerParams() {
        if (null == mWindowManager) {
            mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            mParams = new WindowManager.LayoutParams();
            mParams.packageName = mContext.getPackageName();
            mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            //The default position is vertically to the right
//        mParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
            mParams.format = PixelFormat.RGBA_8888;
            LogUtils.d("Test", "params.x = " + mParams.x + "   params.y = " + mParams.y);
        }
    }


    public void addFloatWindow(View popWindow) {
        initDraggableFloatView(mContext, popWindow);
        attachFloatViewToWindow();
    }

    public void removeFloatWindow() {
        if (null != mDraggableFloatView) {
            mWindowManager.removeView(mDraggableFloatView);
        }
        mContext = null;
        mDraggableFloatView.removeAllViews();
        mWindowManager = null;
        mParams = null;
        mDraggableFloatWindow = null;
    }

    public DraggableFloatView getFloatViewContrain() {
        if (null != mDraggableFloatView) {
            return mDraggableFloatView;
        }
        return null;
    }

    /**
     * attach floatView to window
     */
    private void attachFloatViewToWindow() {
        if (mDraggableFloatView == null) {
            throw new IllegalStateException("DraggableFloatView can not be null");
        }
        if (mParams == null) {
            throw new IllegalStateException("WindowManager.LayoutParams can not be null");
        }
        try {
            mWindowManager.updateViewLayout(mDraggableFloatView, mParams);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
            //if floatView not attached to window,addView
            mWindowManager.addView(mDraggableFloatView, mParams);
        }
    }

    /**
     * 初始化touch按钮所在window
     *
     * @param context，上下文对象
     */
    private void initDraggableFloatView(Context context, View popWin) {
        mDraggableFloatView = new DraggableFloatView(context, new OnFlingListener() {
            @Override
            public void onMove(float moveX, float moveY) {
                LogUtils.d("Test", "moveX = " + moveX + " moveY = " + moveY);
                mParams.x = (int) (mParams.x + moveX);
                mParams.y = (int) (mParams.y + moveY);
                LogUtils.d("Test", "mParams.x = " + mParams.x + " mParams.y = " + mParams.y);
                mWindowManager.updateViewLayout(mDraggableFloatView, mParams);
            }
        }, popWin);
    }
}
