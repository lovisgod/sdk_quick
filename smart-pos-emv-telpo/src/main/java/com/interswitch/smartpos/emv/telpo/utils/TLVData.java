package com.interswitch.smartpos.emv.telpo.utils;

import android.util.Log;

import com.telpo.emv.EmvTLV;


/**
 * Created by yemiekai on 2016/8/24 0024.
 */
public class TLVData {
    public final static int EMV_TRUE = (1);//成功
    public final static int EMV_FALSE = (0);//失败
    public int Tag;
    public int Len;
    public byte[] Value;
    public int WholeLen;       //整个TLV的长度 (tag+len+value)
    public byte[] WholeValue;  //整个TLV的值 (tag+len+value)

    public TLVData() {
        this.Tag = 0;
        this.Len = 0;
        this.WholeLen = 0;
    }

    static void Log(String str) {
        Log.d("kaiye", "---TLVData---" + str);
    }

    /**
     * 从一堆数据中(TLV混合在一起)解出逐个TLV
     *
     * @param inData 很多个TLV数据在一起,并且最开头的数据必须是有效的TLV
     * @param outTLV 从inData的开头解出第一个有效的TLV
     * @return
     */
    public static int PraseTLV(byte[] inData, TLVData outTLV) {
        int i = 0;
        int TLen = 0;//数据包中,Tag所占长度
        int LLen = 0;//数据包中,Len所占长度
        int VLen = 0;//数据包中,Value所占长度

        if (inData[0] == 0xFF || inData[0] == 0x00) {
            return EMV_FALSE;
        }

        //解析T
        if ((inData[0] & 0x0F) != 0x0F) {
            TLen = 1;
            outTLV.Tag = inData[0] & 0xFF;
        } else {
            TLen = 2;
            outTLV.Tag = ((inData[0] & 0xFF) << 8) | (inData[1] & 0xFF);
        }

        i += TLen;

        //解析L
        if ((inData[i] & 0x80) != 0x80) {
            LLen = 1;
            VLen = inData[i] & 0x7F;
            i += 1;
        } else {
            LLen = inData[i] & 0x7F;
            i += 1;
            for (int x = LLen; x > 0; x--) {
                VLen |= (inData[i++] & 0xff) << (8 * (x - 1));
            }
        }

        outTLV.Len = VLen;
        outTLV.Value = new byte[VLen];
        System.arraycopy(inData, i, outTLV.Value, 0, VLen);

        outTLV.WholeLen = i + VLen;
        outTLV.WholeValue = new byte[outTLV.WholeLen];
        System.arraycopy(inData, 0, outTLV.WholeValue, 0, outTLV.WholeLen);
        return EMV_TRUE;
    }


    //只能适用TAG占1-2字节的数据

    /**
     * 将TLVData格式的数据组包成byte数组 , 以便网络发送
     * 如以下TAG:
     * Tag = 0x9F
     * Len = 02
     * Vlaue = {0x12,0x13}
     * 则返回{0x9F,0x02,0x12,0x13}
     *
     * @param inTLV
     * @return
     */
    public static byte[] generateTLVArray(TLVData inTLV) {
        if (inTLV == null) {
            Log.e("---TLvData---", "[generateTLVArray]-- inTLV is NULL");
            return null;
        }
        if (inTLV.Tag == 0) {
            Log.e("---TLvData---", "[generateTLVArray]-- inTLV.Tag == 0");
            return new byte[0];
        }

        String sTemp = StringUtil.Int2HexString(inTLV.Tag);
        sTemp += StringUtil.Int2HexString(inTLV.Len);
        if (inTLV.Len != 0) {
            sTemp += StringUtil.bytesToHexString(inTLV.Value);
        }
        Log("--- generateTLVArray --- : " + sTemp);
        byte[] ret = StringUtil.hexStringToByte(sTemp);
        return ret;
    }

    /**
     * 将TLVData格式的数据组包成Hex String
     * 如以下TAG:
     * Tag = 0x9F
     * Len = 02
     * Vlaue = {0x12,0x13}
     * 则返回"9F021213"
     *
     * @param inTLV
     * @return
     */
    public static String generateTLVHexString(TLVData inTLV) {
        if (inTLV == null) {
            Log.e("---TLvData---", "[generateTLVHexString]-- inTLV is NULL");
            return null;
        }
        if (inTLV.Tag == 0) {
            Log.e("---TLvData---", "[generateTLVHexString]-- inTLV.Tag == 0");
            return "";
        }
        String sTemp = StringUtil.Int2HexString(inTLV.Tag);
        sTemp += StringUtil.Int2HexString(inTLV.Len);
        if (inTLV.Len != 0) {
            sTemp += StringUtil.bytesToHexString(inTLV.Value);
        }
        return sTemp;
    }

    public static void EmvTLV2TLVData(EmvTLV emvTLV, TLVData tlvData) {
        tlvData.Tag = emvTLV.Tag;
        tlvData.Value = emvTLV.Value;
        tlvData.Len = emvTLV.Value.length;
    }

    /*
    没有脚本数据时,onlinecallback返回的脚本71和脚本72数据需要是null,不能是0x00 0x00 0x00
    这个outScript71和outScript72要在传进来时就被初始化,才能在这个函数里面改变他们的值,在这函数里面new是无效的,不知道为什么

     */
    public static int PraseScript(byte[] inScript, int inScriptLen, byte[] outScript71, byte[] outScript72) {

        if (inScript == null || inScriptLen == 0) {
            return -1;
        }

        TLVData[] TLVdata = new TLVData[10];

        byte[] script71tmp = new byte[256];
        byte[] script72tmp = new byte[256];

        int remainTLVpraseLen = 0;
        int script71Len = 0;
        int script72Len = 0;


        remainTLVpraseLen = inScriptLen;
        byte[] remainCopyTmp = new byte[remainTLVpraseLen];
        System.arraycopy(inScript, 0, remainCopyTmp, 0, inScriptLen);

        for (int i = 0; remainTLVpraseLen > 0; i++) {
            TLVdata[i] = new TLVData();
            byte[] tmp1 = new byte[remainTLVpraseLen];
            System.arraycopy(remainCopyTmp, 0, tmp1, 0, remainTLVpraseLen);

            TLVData.PraseTLV(tmp1, TLVdata[i]);
            Log("\n" + TLVdata[i].toString());
            remainTLVpraseLen -= TLVdata[i].WholeLen;

            if (TLVdata[i].Tag == 0X71) {
                System.arraycopy(TLVdata[i].WholeValue, 0, script71tmp, script71Len, TLVdata[i].WholeLen);
                script71Len += TLVdata[i].WholeLen;
            } else if (TLVdata[i].Tag == 0X72) {
                System.arraycopy(TLVdata[i].WholeValue, 0, script72tmp, script72Len, TLVdata[i].WholeLen);
                script72Len += TLVdata[i].WholeLen;
            }
            remainCopyTmp = new byte[remainTLVpraseLen];
            System.arraycopy(tmp1, TLVdata[i].WholeLen, remainCopyTmp, 0, remainTLVpraseLen);
        }

        Log("script71Len:" + script71Len);
        Log("script71   :" + StringUtil.bytesToHexString_upcase(script71tmp));

        Log("script72Len:" + script72Len);
        Log("script72   :" + StringUtil.bytesToHexString_upcase(script72tmp));

        if (script71Len > 0) {
            outScript71 = new byte[script71Len];//无效
            System.arraycopy(script71tmp, 0, outScript71, 0, script71Len);
            Log("out 71:" + StringUtil.bytesToHexString_upcase(outScript71));
        }
        if (script72Len != 0) {
            outScript72 = new byte[script72Len];//无效
            System.arraycopy(script72tmp, 0, outScript72, 0, script72Len);
            Log("out 72:" + StringUtil.bytesToHexString_upcase(outScript72));
        }

        return 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TAG      :" + StringUtil.Int2HexString_upcase(this.Tag));
        builder.append("\nLEN      :" + this.Len);
        builder.append("\nValue    :" + StringUtil.bytesToHexString_upcase(this.Value));
        builder.append("\nWholeLen :" + this.WholeLen);
        return builder.toString();
    }
}
