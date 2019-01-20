package com.interswitch.posinterface;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.pax.dal.IIcc;
import com.pax.dal.IMag;
import com.pax.dal.IPicc;
import com.pax.dal.entity.EDetectMode;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.entity.PiccCardInfo;
import com.pax.dal.entity.PollingResult;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.exceptions.MagDevException;
import com.pax.dal.exceptions.PiccDevException;

/**
 * Created by yanglj on 2017-11-02.
 */

public class CardReaderHelper {
    private static CardReaderHelper instance;
    private IMag mag;
    private IIcc icc;
    private IPicc piccInternal;
    private IPicc piccExternal;

    private boolean isStop = false;
    private boolean isPause = false;
    private Context context;
    //private byte readType;
    //private static Object lock;

    private static final byte SLOT_ICC = (byte) 0x00;


    private CardReaderHelper() {
        //lock = new Object();
    }

    public static synchronized CardReaderHelper getInstance() {
        if (instance == null) {
            instance = new CardReaderHelper();
        }
        //instance.setContext(context);
        return instance;
    }

    public void setIsPause(boolean flg){
        isPause = flg;
    }


    public PollingResult polling(EReaderType readerType, int timeout) throws MagDevException, PiccDevException,
            IccDevException {
        //synchronized (lock) {
        isStop = false;
        isPause = false;
        icc = Holder.getIdal().getIcc();
        byte mode = readerType.getEReaderType();
        mag = Holder.getIdal().getMag();
//        if ((readerType.getEReaderType() & EReaderType.MAG.getEReaderType()) == EReaderType.MAG.getEReaderType()) {
//            mag = TradeApplication.getDal().getMag();
//            mag.close();
//            mag.open();
//            mag.reset();
//        }
        //Log.i(TAG, "readerType.getEReaderType() = " + readerType.getEReaderType());
        if ((readerType.getEReaderType() & EReaderType.PICC.getEReaderType()) == EReaderType.PICC.getEReaderType()) {
            // picc.close();
            if (piccInternal == null) {
                piccInternal = Holder.getIdal().getPicc(EPiccType.INTERNAL);
            }
            // picc = piccInternal;
//                if (readerType.getEReaderType() == EReaderType.PICC.getEReaderType()) {
//                    mag = TradeApplication.getDal().getMag();
//                    mag.close();
//                    icc.close(SLOT_ICC);
//                }
            piccInternal.close();
            //SwingCardActivity.prnTime("polling piccInternal.open start timse = ");
            piccInternal.open();
            //SwingCardActivity.prnTime("polling piccInternal.open timse = ");
            //Log.i(TAG, "piccInternal.open ok mode = " + mode);
        }
        if ((readerType.getEReaderType() & EReaderType.PICCEXTERNAL.getEReaderType()) == EReaderType.PICCEXTERNAL
                .getEReaderType()) {
            // picc.close();
            if (piccExternal == null) {
                piccExternal = Holder.getIdal().getPicc(EPiccType.EXTERNAL);
            }
            // picc = piccExternal;
            piccExternal.close();
            piccExternal.open();
        }
        //SwingCardActivity.prnTime("Device Open diff = ");
        serviceReadType.getInstance().setrReadType(EReaderType.DEFAULT.getEReaderType());
        long startTime = System.currentTimeMillis();
        while (!isStop) {
            if (timeout > 0) {
                long endTime = System.currentTimeMillis();
                if (endTime - startTime > timeout) {
                    PollingResult result = new PollingResult();
                    result.setOperationType(PollingResult.EOperationType.TIMEOUT);
                    result.setReaderType(readerType);
                    closeReader(mode);
                    return result;
                }
            }
            // MAG

            if ((mode & EReaderType.MAG.getEReaderType()) == EReaderType.MAG.getEReaderType()) {
//                if (mag.isSwiped()) {

                if (serviceReadType.getInstance().getrReadType() == EReaderType.MAG.getEReaderType()) {
                    PollingResult result = new PollingResult();
                    //TrackData info = mag.read();
                    result.setOperationType(PollingResult.EOperationType.OK);
                    result.setReaderType(EReaderType.MAG);
                    closeReader(mode);
                    return result;
                }
            }
            // ICC

            log("Entering ICC => ");
            if ((mode & EReaderType.ICC.getEReaderType()) == EReaderType.ICC.getEReaderType()) {
                //boolean ret = icc.detect(SLOT_ICC);
                //if (ret) {
                byte type = serviceReadType.getInstance().getrReadType();
                //Log.i(TAG, "serviceReadType.getInstance().getrReadType = " + type);
                PollingResult result = new PollingResult();
                result.setOperationType(PollingResult.EOperationType.OK);
                result.setReaderType(EReaderType.ICC);
                // closeReader((byte) 0x05); // close mag + picc
                closeReader((byte) (mode & 0x05));
                //Log.i(TAG, "closeReader (mode & 0x05) OK" );
                return result;
            }
            // PICC

            if ((mode & EReaderType.PICC.getEReaderType()) == EReaderType.PICC.getEReaderType()) {

                //SwingCardActivity.prnTime("piccInternal.detect start ");
                // for A920C+ terminal, if picc reader closed, then here will throw exception, so catch this exception, and keep thread alive.
                try {


                    PiccCardInfo info = piccInternal.detect(EDetectMode.EMV_AB);

                    //Log.i(TAG, "piccInternal.detect Finish " );
                    if (info != null) {
                        PollingResult result = new PollingResult();
                        result.setOperationType(PollingResult.EOperationType.OK);
                        result.setReaderType(EReaderType.PICC);
                        result.setSerialInfo(info.getSerialInfo());
                        //closeReader((byte) (mode & 0x03)); // close mag + icc
                        return result;
                    }
                }catch (PiccDevException e){
                    e.printStackTrace();
                }
            }
            // PICCEXTERNAL
            if ((mode & EReaderType.PICCEXTERNAL.getEReaderType()) == EReaderType.PICCEXTERNAL.getEReaderType()) {
                PiccCardInfo info = piccExternal.detect(EDetectMode.EMV_AB);
                if (info != null) {
                    PollingResult result = new PollingResult();
                    result.setOperationType(PollingResult.EOperationType.OK);
                    result.setReaderType(EReaderType.PICCEXTERNAL);
                    result.setSerialInfo(info.getSerialInfo());
                    // picc.close();
                    // closeReader((byte) 0x03); // close mag + icc
                    closeReader((byte) (mode & 0x03));
                    return result;
                }
            }
        }
        // SystemClock.sleep(100);
        PollingResult result = new PollingResult();
        if (isPause) {
            result.setOperationType(PollingResult.EOperationType.PAUSE);
        } else {
            result.setOperationType(PollingResult.EOperationType.CANCEL);
        }
        // closeReader((byte) 0x07); // close all
        closeReader(mode);
        return result;
    }



    public void stopPolling() {
        // synchronized (lock) {
        // 避免极端情况：在polling之后马上调用stopPolling，因线程还未初始化导致stopPolling不起作用。
        SystemClock.sleep(30);
        isStop = true;
        // }
        // SystemClock.sleep(100);
    }

    private void closeReader(byte flag) {

        if ((flag & 0x01) != 0) {
            try {
                mag.close();
            } catch (Exception e) {
                Log.e("mag close error : ", e.getMessage());
                //e.printStackTrace();
            }
        }

        if ((flag & 0x02) != 0) {
            try {
                icc.close(SLOT_ICC);
            } catch (Exception e) {
                Log.e("icc close error : ", e.getMessage());
                //e.printStackTrace();
            }
        }

        if ((flag & 0x04) != 0) {

            if (piccExternal != null) {
                try {
                    piccExternal.close();
                } catch (Exception e) {
                    Log.e("piccExt close err: ", e.getMessage());
                    //e.printStackTrace();
                }
            }

//            try {
//                piccInternal.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

    }


    void log(String m) {
        Log.d("Card", m);
    }
}
