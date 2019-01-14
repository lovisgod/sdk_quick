/*
 * ============================================================================
 * COPYRIGHT
 *              Pax CORPORATION PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with Pax Corporation and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2016 - ? Pax Corporation. All rights reserved.
 * Module Date: 2016-11-25
 * Module Author: Steven.W
 * Description:
 *
 * ============================================================================
 */
package com.interswitch.posinterface.action;

import android.content.Context;

import com.interswitch.posinterface.AAction;


public class ActionEnterPin extends AAction {
    private Context context;
    private String title;
    private String pan;
    private String header;
    private String subHeader;
    private String totalAmount;
    private String offlinePinLeftTimes;
    private boolean isOnlinePin;
    //private EEnterPinType enterPinType;

    public ActionEnterPin(ActionStartListener listener) {
        super(listener);
    }

    /**
     * 脱机pin时返回的结果
     *
     * @author Steven.W
     */
    public static class OfflinePinResult {
        // SW1 SW2
        byte[] respOut;
        int ret;

        public byte[] getRespOut() {
            return respOut;
        }

        public void setRespOut(byte[] respOut) {
            this.respOut = respOut;
        }

        public int getRet() {
            return ret;
        }

        public void setRet(int ret) {
            this.ret = ret;
        }
    }

    public void setParam(Context context, String pan, boolean onlinePin, String totalAmount, String offlinePinLeftTimes) {
        this.context = context;
        this.pan = pan;
        this.isOnlinePin = onlinePin;
        //this.header = header;
        //this.subHeader = subHeader;
        this.totalAmount = totalAmount;
        this.offlinePinLeftTimes = offlinePinLeftTimes; //AET-81
        //this.enterPinType = enterPinType;
    }

    public enum EEnterPinType {
        ONLINE_PIN, // 联机pin
        OFFLINE_PLAIN_PIN, // 脱机明文pin
        OFFLINE_CIPHER_PIN, // 脱机密文pin
        OFFLINE_PCI_MODE, //JEMV PCI MODE, no callback for offline pin
    }

    @Override
    protected void process() {


    }

}
