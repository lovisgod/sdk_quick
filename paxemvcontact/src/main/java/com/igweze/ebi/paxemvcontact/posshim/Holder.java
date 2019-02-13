package com.igweze.ebi.paxemvcontact.posshim;

import com.pax.dal.IDAL;

public class Holder {

    private static IDAL idal;
//    private static IConvert convert = new ConverterImp();

    public static void setdal(IDAL idal) {
        Holder.idal = idal;
    }

    public static IDAL getIdal() {
        return idal;
    }

//    public static IConvert getConvert() {
//        return convert;
//    }
}
