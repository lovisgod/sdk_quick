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

//    private static SimpleDateFormat F7 = new SimpleDateFormat("MMddhhmmss", Locale.getDefault());
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
        init();
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

    private IsoMessage makeKeyCall(String code) {
        try {

            Date now = new Date();
            String stan = getNextStan();

            NibssIsoMessage message = new NibssIsoMessage(messageFactory.newMessage(0x800));
            message
                    .setValue(3, code)
                    .setValue(7, timeAndDateFormatter.format(now))
                    .setValue(11, stan)
                    .setValue(12, timeFormatter.format(now))
                    .setValue(13, dateFormatter.format(now))
                    .setValue(41, terminalID);

            // remove unset fields
            message.getMessage().removeFields(62, 64);

            message.dump(System.out, "request -- ");

            NibssIsoSocket socket = socketFactory.create(SERVER_IP, SERVER_PORT, TIMEOUT);
            socket.connect();

            byte[] response = socket.sendReceive(message.getMessage().writeData());
            NibssIsoMessage msg = new NibssIsoMessage(messageFactory.parseMessage(response, 0));

            msg.dump(System.out, "response -- ");

            return msg.getMessage();
        } catch (UnsupportedEncodingException | ParseException e) {
            logEx(e);
        }

        return null;

    }

    public IsoMessage makeGetMasterKeyCall() {
        return makeKeyCall("9A0000");
    }

    public IsoMessage makeGetSessionKeyCall() {
            return makeKeyCall("9B0000");
    }

    public IsoMessage makeGetPinKeyCall() {
        return makeKeyCall("9G0000");
    }

    public ISOMsg makeGetParametersCall() {

        try {

            String field3 = "9C0000";
            String field62 = "01009280824266";

            ISO87APackager packager = new ISO87APackager();
            packager.setFieldPackager(64, new IFA_BINARY(32, "MAC 1"));

            Date now = new Date();
            String stan = getNextStan();

            NibssIsoMessage message = new NibssIsoMessage(messageFactory.newMessage(0x800));
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
        NibssIsoMessage message = new NibssIsoMessage(messageFactory.newMessage(0x200));
        Date now = new Date();

        message
                .setValue(2, transaction.card.pan)
                .setValue(3, "002000")
                .setValue(4, "000000002100") //String.format(Locale.getDefault(), "%012d", transaction.amount))
                .setValue(7, timeAndDateFormatter.format(now))
                .setValue(11, getNextStan())
                .setValue(12, timeFormatter.format(now))
                .setValue(13, dateFormatter.format(now))
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


        // remove unset fields
        message.getMessage().removeFields(32, 52, 59);
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

        message.dump(System.out, "request -- ");
        return message.getMessage();
    }

    public IsoMessage getPaycodeTransaction() {

        try {

            TerminalData terminalData = TerminalData.get(preferences);
            NibssIsoMessage message = new NibssIsoMessage(messageFactory.newMessage(0x200));
            Date now = new Date();

            message
                    .setValue(2, "5061011234567890008")
                    .setValue(3, "000000")
                    .setValue(4, "000000002100") //String.format(Locale.getDefault(), "%012d", transaction.amount))
                    .setValue(7, timeAndDateFormatter.format(now))
                    .setValue(11, getNextStan())
                    .setValue(12, timeFormatter.format(now))
                    .setValue(13, dateFormatter.format(now))
                    .setValue(18, terminalData.merchantCategoryCode)
                    .setValue(22, "001")
                    .setValue(23, "001")
                    .setValue(25, "00")
                    .setValue(26, "06")
                    .setValue(28, "C00000000")
                    .setValue(37, "000000000008")
                    .setValue(40, "601")
                    .setValue(41, terminalID)
                    .setValue(42, terminalData.merchantId)
                    .setValue(43, terminalData.merchantLocation)
                    .setValue(49, terminalData.currencyCode)
                    .setValue(59, "09")
                    .setValue(123, "510101511344101");

            // remove unset fields
            message.getMessage().removeFields(14, 35, 32, 52, 55);

            byte[] bytes = message.getMessage().writeData();
            int length = bytes.length;
            byte[] temp = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(bytes, 0, temp, 0, length - 64);
            }

            String hashValue = TripleDES.getMac(get(KEY_SESSION_KEY), temp); //SHA256
            message.setValue(128, hashValue);

            message.dump(System.out, "request -- ");

            return message.getMessage();

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

