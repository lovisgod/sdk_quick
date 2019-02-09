package com.igweze.ebi.paxemvcontact.iso8583;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

import org.jpos.iso.IFA_BINARY;
import org.jpos.iso.IFA_LLBINARY;
import org.jpos.iso.IFA_LLCHAR;
import org.jpos.iso.IFA_NUMERIC;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.iso.packager.ISO87APackager;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static com.igweze.ebi.paxemvcontact.utilities.Constants.SERVER_IP;
import static com.igweze.ebi.paxemvcontact.utilities.Constants.SERVER_PORT;
import static com.igweze.ebi.paxemvcontact.utilities.Constants.TIMEOUT;
import static com.igweze.ebi.paxemvcontact.utilities.DisplayUtils.dateFormatter;
import static com.igweze.ebi.paxemvcontact.utilities.DisplayUtils.timeAndDateFormatter;
import static com.igweze.ebi.paxemvcontact.utilities.DisplayUtils.timeFormatter;

public class NibbsCommunication {

    private SharedPreferences preferences;
    public static final String
            NIBSS_PREFS = "nibss_data_pref",
            KEY_STAN = "stan",
            KEY_MASTER_KEY = "master_key_data",
            KEY_SESSION_KEY = "session_key_data",
            KEY_PIN_KEY = "pin_key_data";

    private static SimpleDateFormat F7 = new SimpleDateFormat("MMddhhmmss", Locale.getDefault());
    private static SimpleDateFormat LOCAL_TIME = new SimpleDateFormat("hhmmss", Locale.getDefault());
    private static SimpleDateFormat F13 = new SimpleDateFormat("MMdd", Locale.getDefault());
    private ISO87APackager packager = new ISO87APackager();
    public static String CLEAR_MASTER_KEY = "DBEECACCB4210977ACE73A1D873CA59F";
    private Context mContext;
    public MessageFactory<IsoMessage> messageFactory;
    private SocketFactory socketFactory;
    private String terminalID = "20390007"; // "2039661C";


    public NibbsCommunication(Context context, SocketFactory factory) {
        mContext = context;
        socketFactory = factory;
        preferences = context.getSharedPreferences(NIBSS_PREFS, Context.MODE_PRIVATE);
    }

    public ISOMsg makeGetMasterKeyCall() {

        try {

            packager.setFieldPackager(53, new IFA_NUMERIC(32, "Security Related Data Control"));
            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setMTI("0800");
            isoMsg.set(3, "9A0000");
            isoMsg.set(7, F7.format(new Date()));
            isoMsg.set(11, getNextStan());
            isoMsg.set(12, LOCAL_TIME.format(new Date()));
            isoMsg.set(13, F13.format(new Date()));
            isoMsg.set(41, terminalID);
            isoMsg.setPackager(packager);
            isoMsg.dump(System.out, "request ---");

            byte[] message = isoMsg.pack();
            NibssIsoSocket socket = socketFactory.create(SERVER_IP, SERVER_PORT, TIMEOUT);
            socket.connect();

            byte[] response = socket.sendReceive(message);
            init();
            IsoMessage messageResponse = messageFactory.parseMessage(response, 0);
            ISOMsg msg = new ISOMsg();
            msg.setPackager(packager);
            msg.unpack(response);
            msg.dump(System.out, "");

            return msg;
        } catch (ISOException e) {
            logEx(e);
        } catch (UnsupportedEncodingException | ParseException e) {
            logEx(e);
        }

        return null;
    }

    public ISOMsg makeGetSessionKeyCall() {

        try {

            packager.setFieldPackager(53, new IFA_NUMERIC(32, "Security Related Data Control"));

            ISOMsg sessionKeyIsoMsg = new ISOMsg();
            sessionKeyIsoMsg.setMTI("0800");
            sessionKeyIsoMsg.set(3, "9B0000");
            sessionKeyIsoMsg.set(7, F7.format(new Date()));
            sessionKeyIsoMsg.set(11, getNextStan());
            sessionKeyIsoMsg.set(12, LOCAL_TIME.format(new Date()));
            sessionKeyIsoMsg.set(13, F13.format(new Date()));
            sessionKeyIsoMsg.set(41, terminalID);
            sessionKeyIsoMsg.setPackager(packager);

            byte[] sessionKeyPayload = sessionKeyIsoMsg.pack();

            NibssIsoSocket socket = socketFactory.create(SERVER_IP, SERVER_PORT, TIMEOUT);
            socket.connect();

            byte[] response = socket.sendReceive(sessionKeyPayload);

            ISOMsg sessionResponse = new ISOMsg();
            sessionResponse.setPackager(packager);
            sessionResponse.unpack(response);
            sessionResponse.dump(System.out, "");

            return sessionResponse;
        } catch (Exception e) {
            logEx(e);
        }
        return null;
    }

    public ISOMsg makeGetPinKeyCall() {

        try {

            packager.setFieldPackager(53, new IFA_NUMERIC(32, "Security Related Data Control"));
            ISOMsg pinKeyMsg = new ISOMsg();
            pinKeyMsg.setMTI("0800");
            pinKeyMsg.set(3, "9G0000");
            pinKeyMsg.set(7, F7.format(new Date()));
            pinKeyMsg.set(11, getNextStan());
            pinKeyMsg.set(12, LOCAL_TIME.format(new Date()));
            pinKeyMsg.set(13, F13.format(new Date()));
            pinKeyMsg.set(41, terminalID);
            pinKeyMsg.setPackager(packager);

            byte[] pinKeyData = pinKeyMsg.pack();
            NibssIsoSocket socket = socketFactory.create(SERVER_IP, SERVER_PORT, TIMEOUT);
            socket.connect();

            byte[] pinKeyDataResponse = socket.sendReceive(pinKeyData);
            ISOMsg pinKeyResponse = new ISOMsg();
            pinKeyResponse.setPackager(packager);
            pinKeyResponse.unpack(pinKeyDataResponse);

            pinKeyResponse.dump(System.out, "");
            return pinKeyResponse;
        } catch (Exception e) {

            logEx(e);
        }
        return null;
    }

    public void init() {

        try {

            if (messageFactory == null) {
                byte[] data = Util.getFromAssets(mContext);
                String string = new String(data);
                StringReader stringReader = new StringReader(string);
                messageFactory = ConfigParser.createFromReader(stringReader);
                messageFactory.setUseBinaryBitmap(false); //NIBSS usebinarybitmap = false
                messageFactory.setCharacterEncoding("UTF-8");
            }

        } catch (Exception e) {
            logEx(e);
        }
    }

    public ISOMsg makeGetParametersCall() {

        try {


            String field0 = "0800";
            String field3 = "9C0000";
            String field62 = "01009280824266";

            ISO87APackager packager = new ISO87APackager();
            packager.setFieldPackager(64, new IFA_BINARY(32, "MAC 1"));

            Date now = new Date();
            String stan = getNextStan();


            init();

            int type = Integer.parseInt(field0, 16);
            NibssIsoMessage message = new NibssIsoMessage(messageFactory.newMessage(type));
            message
                    .setValue(3, field3)
                    .setValue(7, timeAndDateFormatter.format(now))
                    .setValue(11, stan)
                    .setValue(12, timeFormatter.format(now))
                    .setValue(13, dateFormatter.format(now))
                    .setValue(41, terminalID)
                    .setValue(62, field62);


            byte[] bytes = message.getMessage().writeData();
            int length = bytes.length;
            byte[] temp = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(bytes, 0, temp, 0, length - 64);
            }

            String hashValue = TripleDES.getMac(get(KEY_SESSION_KEY), temp); //SHA256

            message.setValue(64, hashValue);

            ISOMsg tem = new ISOMsg();
            tem.setPackager(this.packager);
            tem.unpack(message.getMessage().writeData());
            tem.dump(System.out, "parameter request ---- ");

            NibssIsoSocket socket = socketFactory.create(SERVER_IP, SERVER_PORT, TIMEOUT * 5);
            socket.connect();

            byte[] response = socket.sendReceive(message.getMessage().writeData());//(terminalDownloadMsg.pack());

            IsoMessage isoMessage = messageFactory.parseMessage(response, 0, false);


            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.unpack(response);
            isoMsg.dump(System.out, "parameter response ----");

            return isoMsg;
        } catch (Exception e) {
            logEx(e);
            e.printStackTrace();
        }
        return null;
    }

    public IsoMessage getPurchaseMessage() throws Exception {

        Transaction transaction = new Transaction();
        Card card = new Card();


        card.expiry = "2702";
//        card.pin = "1992"
        card.pan = "5060990580000367864";
        card.track2Data = "5060990580000367864D2702601018444995";
        String iccData = "9F26086FD4B7FA1411E34F9F2701809F10200FA501A139F8140000000000000000000F0100000000000000000000000000009F3704EA677C889F36020E1D950540800000009A031810299C01009F02060000000021005F2A020566820258009F1A0205669F34034103029F3303E0F8C89F3501349F0306000000000000";
        transaction.amount = 2100;
        transaction.card = card;


        TerminalData terminalData = TerminalData.get(preferences);
        int type = Integer.parseInt("0200", 16);
        NibssIsoMessage message = new NibssIsoMessage(messageFactory.newMessage(type));
        Date now = new Date();

        message
                .setValue(2, transaction.card.pan)
                .setValue(3, "002000")
                .setValue(4, "000000002100") //String.format(Locale.getDefault(), "%012d", transaction.amount))
                .setValue(7, F7.format(now))
                .setValue(11, getNextStan())
                .setValue(12, LOCAL_TIME.format(now))
                .setValue(13, F13.format(now))
                .setValue(14, transaction.card.expiry)
                .setValue(18, terminalData.merchantCategoryCode)
                .setValue(22, "051")
                .setValue(23, "001")
                .setValue(25, "00")
                .setValue(26, "06")
                .setValue(28, "C00000000")
                .setValue(35, transaction.card.track2Data)
                .setValue(37, "000000000008")
                .setValue(40, "601")
                .setValue(41, terminalID)
                .setValue(42, terminalData.merchantId)
                .setValue(43, terminalData.merchantLocation)
                .setValue(49, terminalData.currencyCode)


//        String pinData = TripleDES.harden(get(KEY_PIN_KEY), transaction.card.pin);
//        message.setValue(52, pinData.getBytes(), templ.getField(52).getType(), templ.getField(52).getLength());
                .setValue(55, iccData)
                .setValue(123, "510101511344101");


        System.out.println("purchase request before copy => " + message.getMessage().debugString());

        byte[] bytes = message.getMessage().writeData();
        int length = bytes.length;
        byte[] temp = new byte[length - 64];
        if (length >= 64) {
            System.arraycopy(bytes, 0, temp, 0, length - 64);
        }

        String hashValue = TripleDES.getMac(get(KEY_SESSION_KEY), temp); //SHA256
        message.setValue(128, hashValue);

        byte[] messageData = message.getMessage().writeData();
        System.out.println("purchase request after copy => " + message.getMessage().debugString());


        ISOMsg sample = new ISOMsg();
        sample.setPackager(new ISO87APackager());
        sample.unpack(messageData);
        sample.dump(System.out, "purchase request ---- ");

        return message.getMessage();
    }


    public ISOMsg paycodeTransaction() {

        try {
            TerminalData terminalData = TerminalData.get(preferences);
            int type = Integer.parseInt("0200", 16);
            IsoMessage message = messageFactory.newMessage(type);
            IsoMessage templ = messageFactory.getMessageTemplate(type);
            Date now = new Date();

            message.setValue(2, "5061011234567890008", templ.getField(2).getType(), templ.getField(2).getLength());
            message.setValue(3, "010000", templ.getField(3).getType(), templ.getField(3).getLength());
            message.setValue(4, "000015000000", templ.getField(4).getType(), templ.getField(4).getLength());

            message.setValue(7, F7.format(now), templ.getField(7).getType(), templ.getField(7).getLength());

            message.setValue(11, getNextStan(), templ.getField(11).getType(), templ.getField(11).getLength());

            message.setValue(12, LOCAL_TIME.format(now), templ.getField(12).getType(),
                    templ.getField(12).getLength());

            message.setValue(13, F13.format(now), templ.getField(13).getType(), templ.getField(13).getLength());

            message.setValue(18, terminalData.merchantCategoryCode, templ.getField(18).getType(),
                    templ.getField(18).getLength());

            message.setValue(22, "051", templ.getField(22).getType(), templ.getField(22).getLength());

            message.setValue(23, "001", templ.getField(23).getType(), templ.getField(23).getLength());

            message.setValue(25, "00", templ.getField(25).getType(),
                    templ.getField(25).getLength());

            message.setValue(26, "06", templ.getField(26).getType(),
                    templ.getField(26).getLength());

            message.setValue(28, "C00005000", templ.getField(28).getType(),
                    templ.getField(28).getLength());

            message.setValue(37, "000000000008", templ.getField(37).getType(), templ.getField(37).getLength());

            message.setValue(40, "601", templ.getField(40).getType(),
                    templ.getField(40).getLength());

            message.setValue(41, "20390007", templ.getField(41).getType(),
                    templ.getField(41).getLength());

            message.setValue(42, terminalData.merchantId, IsoType.ALPHA, 15);

            message.setValue(43, terminalData.merchantLocation, templ.getField(43).getType(),
                    templ.getField(43).getLength());

            message.setValue(49, "566", templ.getField(49).getType(),
                    templ.getField(49).getLength());

            message.setValue(59, "90", templ.getField(59).getType(),
                    templ.getField(59).getLength());

            message.setValue(123, "510101511344101", templ.getField(123).getType(), templ.getField(123).getLength());

            message.setValue(128, new String(new byte[]{0x0}), templ.getField(128).getType(), templ.getField(128).getLength());

            byte[] bytes = message.writeData();
            int length = bytes.length;
            byte[] temp = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(bytes, 0, temp, 0, length - 64);
            }

            String hashValue = TripleDES.getMac(get(KEY_SESSION_KEY), temp); //SHA256
            message.setValue(128, hashValue, templ.getField(128).getType(), templ.getField(128).getLength());

            byte[] messageData = message.writeData();
            NibssIsoSocket socket = new NibssIsoSocket(SERVER_IP, SERVER_PORT, TIMEOUT * 10);
            socket.connect();

            System.out.print("message ---- " + message.debugString());
            /// sample log
            ISOMsg sample = new ISOMsg();
            sample.setPackager(new ISO87APackager());
            sample.unpack(messageData);
            sample.dump(System.out, "sample ---- ");
            /// end sample log

            byte[] response = socket.sendReceive(sample.pack());
            ISOMsg responseMsg = new ISOMsg();
            responseMsg.setPackager(packager);
            responseMsg.unpack(response);
            return responseMsg;
        } catch (Exception e) {

            logEx(e);
        }
        return null;
    }

    private String getNextStan() {
        int stan = preferences.getInt(KEY_STAN, 0);

        int newStan = ++stan;
        preferences.edit().putInt(KEY_STAN, newStan).apply();

        return String.format(Locale.getDefault(), "%06d", (newStan));
    }

    private void logEx(Throwable throwable) {
//        L.e(throwable);
    }

    public void put(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public String get(String key) {
        return preferences.getString(key, "");
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }


    public interface SocketFactory {
        NibssIsoSocket create(String serverIp, int port, long timeout);
    }
}

