package com.interswitch.smartpos.emv.telpo.utils;

import android.util.Log;


/**
 * Created by yemiekai on 2016/8/24 0024.
 */
public class TLVData_BOCField48 {
    public final static int EMV_TRUE = (1);//成功
    public final static int EMV_FALSE = (0);//失败
    public String Tag;
    public String Len;
    public String Value;
    public int WholeLen;       //整个TLV的长度 (tag+len+value)

    public TLVData_BOCField48() {
        this.Tag = "";
        this.Len = "0";
        this.Value = "";
        this.WholeLen = 0;
    }

    static void Log(String str) {
        Log.d("kaiye", "---TLVData---" + str);
    }

    /**
     * 从一堆数据中(TLV混合在一起)解出逐个TLV
     *
     * @param inData 很多个TLV数据在一起,并且最开头的数据必须是有效的TLV, 输入参数是ASCII, 如
     *               {0x39,0x38,0x30,0x33,0x35,0x31,0x31}
     *               那么Tag就是"98",Length就是"03",value就是"511";
     * @param outTLV 从inData的开头解出第一个有效的TLV
     * @return
     */
    public static int PraseTLV(byte[] inData, TLVData_BOCField48 outTLV) {
        int i = 0;
        int TLen = 0;//数据包中,Tag所占长度
        int LLen = 0;//数据包中,Len所占长度
        int VLen = 0;//数据包中,Value所占长度

        byte[] praseTmp = inData;
        byte[] tmp;
        if (inData[0] == 0xFF || inData[0] == 0x00) {
            return EMV_FALSE;
        }

        //解析T
        TLen = 2;
        tmp = ByteArrayUtil.byteArrayGetHead(praseTmp, TLen);
        praseTmp = ByteArrayUtil.byteArrayTrimHead(praseTmp, TLen);
        outTLV.Tag = new String(tmp);


        //解析L
        if (outTLV.Tag.equals("AQ")//用法16
                || outTLV.Tag.equals("PP")//用法15
                || outTLV.Tag.equals("PN")//用法14
                || outTLV.Tag.equals("TA")//用法13
                || outTLV.Tag.equals("75")//用法12
                || outTLV.Tag.equals("97"))//用法5
        {
            LLen = 3;
        } else {
            LLen = 2;
        }
        tmp = ByteArrayUtil.byteArrayGetHead(praseTmp, LLen);
        praseTmp = ByteArrayUtil.byteArrayTrimHead(praseTmp, LLen);
        outTLV.Len = new String(tmp);


        //解析V
        VLen = Integer.parseInt(outTLV.Len);
        tmp = ByteArrayUtil.byteArrayGetHead(praseTmp, VLen);
        outTLV.Value = new String(tmp);

        outTLV.WholeLen = TLen + LLen + VLen;

        return EMV_TRUE;
    }


    //只能适用TAG占1-2字节的数据

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TAG      :" + this.Tag);
        builder.append("\nLEN      :" + this.Len);
        builder.append("\nValue    :" + this.Value);
        builder.append("\nWholeLen :" + this.WholeLen);
        return builder.toString();
    }

}
