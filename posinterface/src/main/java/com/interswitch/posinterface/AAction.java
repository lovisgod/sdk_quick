package com.interswitch.posinterface;


import com.interswitch.posinterface.action.ActionResult;

/**
 * Action 抽象类定义
 * 
 * @author Steven.W
 * 
 */
public abstract class AAction {
    /**
     * action 执行开始回调
     * 
     * @author Steven.W
     * 
     */
    public interface ActionStartListener {
        public void onStart(AAction action);
    }

//    public interface ActionEndListener {
//        public void onEnd(AAction action);
//    }

    /**
     * ACTION结束监听器
     * 
     * @author Steven.W
     * 
     */
    public interface ActionEndListener {
        public void onEnd(AAction action, ActionResult result);
    }

    private ActionStartListener startListener;
    private ActionEndListener endListener;

    /**
     * 子类构造方法必须调用super设置ActionStartListener
     * 
     * @param listener
     *            {@link ActionStartListener}
     */
    public AAction(ActionStartListener listener) {
        this.startListener = listener;
    }

    /**
     * 执行ACTION之前需要先设置{@link ActionEndListener}, 此接口内部先调用{@link ActionStartListener#onStart(AAction)} , 再调用
     * {@link AAction#process}
     */
    public void execute() {
        TransContext.getInstance().setCurrentAction(this);
        if (startListener != null) {
            startListener.onStart(this);
        }
        process();
    }

    /**
     * 
     * @param listener
     */
    public void setEndListener(ActionEndListener listener) {
        this.endListener = listener;
    }

    /**
     * action的具体处理方法
     */
    protected abstract void process();



    public void setResult(ActionResult result) {
        if (endListener != null) {
            endListener.onEnd(this, result);
        }
    }
}