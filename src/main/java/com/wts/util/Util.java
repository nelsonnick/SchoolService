package com.wts.util;


public class Util {

    public static String getString(String str) {
        if (str==null){
            return "";
        }else{
            return str.trim();
        }
    }
    public static void main(String[] args) {
        String b = "^([\\d]{4}(((0[13578]|1[02])((0[1-9])|([12][0-9])|(3[01])))|(((0[469])|11)((0[1-9])|([12][1-9])|30))|(02((0[1-9])|(1[0-9])|(2[1-8])))))|((((([02468][048])|([13579][26]))00)|([0-9]{2}(([02468][048])|([13579][26]))))(((0[13578]|1[02])((0[1-9])|([12][0-9])|(3[01])))|(((0[469])|11)((0[1-9])|([12][1-9])|30))|(02((0[1-9])|(1[0-9])|(2[1-9])))))";
        System.out.println("201p0229".matches(b));
        // System.out.println("111"+CheckNull(null));
    }
}
