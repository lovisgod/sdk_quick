package com.interswitch.smartpos.emv.telpo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yemiekai on 2016/11/24 0024.
 */

public class MyProgressDialog extends ProgressDialog {

    private long mTimeOut = 0;// 默认timeOut为0, 即无限大
    private OnTimeOutListener mTimeOutListener = null;// timeOut后的处理器
    private Timer mTimer = null;// 定时器

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mTimeOutListener != null) {
                mTimeOutListener.onTimeOut(MyProgressDialog.this);
                dismiss();
            }
        }
    };

    public MyProgressDialog(Context context, String title, String text) {
        super(context);
        // 设置进度条风格，风格为圆形，旋转的
        this.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 标题
        this.setTitle(title);
        // 设置ProgressDialog 提示信息
        this.setMessage(text);
        // 设置ProgressDialog 的进度条是否不明确
        this.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
        this.setCancelable(false);
    }

    /**
     * 通过静态Create的方式创建一个实例对象
     *
     * @param context
     * @param time     timeout时间长度
     * @param listener timeOutListener 超时后的处理器
     * @return MyProgressDialogCard 对象
     */
    public static MyProgressDialog createProgressDialog(Context context, String title, String text,
                                                        long time, OnTimeOutListener listener) {
        MyProgressDialog progressDialog = new MyProgressDialog(context, title, text);
        if (time != 0) {
            progressDialog.setTimeOut(time, listener);
        }
        return progressDialog;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mTimeOut != 0) {
            mTimer = new Timer();
            TimerTask timerTast = new TimerTask() {
                @Override
                public void run() {
                    // dismiss();
                    Message msg = mHandler.obtainMessage();
                    mHandler.sendMessage(msg);
                }
            };
            mTimer.schedule(timerTast, mTimeOut);
        }

    }

    /**
     * 设置timeOut长度，和处理器
     *
     * @param t               timeout时间长度
     * @param timeOutListener 超时后的处理器
     */
    public void setTimeOut(long t, OnTimeOutListener timeOutListener) {
        mTimeOut = t;
        if (timeOutListener != null) {
            this.mTimeOutListener = timeOutListener;
        }
    }

    /**
     * 处理超时的的接口
     */
    public interface OnTimeOutListener {

        /**
         * 当progressDialog超时时调用此方法
         */
        void onTimeOut(MyProgressDialog dialog);
    }


}
