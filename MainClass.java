package com.SLOC;

public class MainClass {
    public static void main(String[] args) {
        LOC locObj = new LOC();
        String file = "C:\\Users\\aleks\\OneDrive\\Робочий стіл\\Lab_6_tprs\\src\\main\\java\\com\\SLOC\\LOC.java";

        locObj.start(String.join(" ", file).trim());
        System.out.println(locObj.toString());

    }
}
