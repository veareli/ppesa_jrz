package com.daicon.pc.ppesa;

import java.text.DecimalFormat;

public class Utilerias {

    public static double limitarDecimales(double num){
        double numFormated;
        DecimalFormat df = new DecimalFormat("#.00");
        numFormated = Double.valueOf(df.format(num));
        return numFormated;
    }

}
