package com.igweze.ebi.paxemvcontact.iso8583;

import java.util.ArrayList;
import java.util.List;

public class TerminalParameter {

    public String tag;
    public int len;
    public String value;



    public TerminalParameter() {}

    public TerminalParameter(String tag, int len, String value) {
        this.tag = tag;
        this.len = len;
        this.value = value;
    }

    public static TerminalData parse(String rawData) {

        List<List<TerminalParameter>> paramatersLists = new ArrayList<>();
        List<TerminalParameter> terminalParameters = new ArrayList<>();
        TerminalData data = new TerminalData();
        try {

            String tmp = rawData;
            while (tmp.length() > 0) {

                String tag = tmp.substring(0, 2);
                tmp = tmp.substring(2);
                int len = Integer.parseInt(tmp.substring(0, 3));
                tmp = tmp.substring(3);
                String value = tmp.substring(0, len);
                tmp = tmp.substring(len);
                TerminalParameter tlv = new TerminalParameter(tag, len, value);
                terminalParameters.add(tlv);
                int tmpLen = tmp.length();
                String delim = tmpLen > 0 ? tmp.substring(0, 1) : "";
                if(delim.equalsIgnoreCase("~") || tmpLen == 0) {
                    paramatersLists.add(terminalParameters);
                    tmp = tmpLen > 0 ? tmp.substring(1) : tmp;
                    terminalParameters = new ArrayList<>();
                }
            }

            if (paramatersLists.size() > 0) {

                terminalParameters = paramatersLists.get(0);
                for(TerminalParameter tlv: terminalParameters){
                    if("03".equalsIgnoreCase(tlv.getTag()))
                        data.merchantId = tlv.getValue();
                    else if("04".equalsIgnoreCase(tlv.getTag()))
                        data.serverTimeoutInSec = Integer.parseInt(tlv.getValue());
                    else if("05".equalsIgnoreCase(tlv.getTag()))
                        data.currencyCode = "0" + tlv.getValue();
                    else if("06".equalsIgnoreCase(tlv.getTag()))
                        data.countryCode = "0" + tlv.getValue();
                    else if("07".equalsIgnoreCase(tlv.getTag()))
                        data.callHomeTimeInMin = Integer.parseInt(tlv.getValue()) * 60;
                    else if("08".equalsIgnoreCase(tlv.getTag()))
                        data.merchantCategoryCode = tlv.getValue();
                    else if("52".equalsIgnoreCase(tlv.getTag()))
                        data.merchantLocation = tlv.getValue();
                }
            }

            if (data.countryCode.length() >= 4) {
                data.countryCode = data.countryCode.substring(1, data.countryCode.length());
            }

            if (data.currencyCode.length() >= 4) {
                data.currencyCode = data.currencyCode.substring(1, data.currencyCode.length());
            }
        } catch(Exception ex){
            return null;
        }

        return data;
    }

    public String getTag() {
        return tag;
    }

    public String getValue() {
        return value;
    }

    public int getLen() {
        return len;
    }
}
