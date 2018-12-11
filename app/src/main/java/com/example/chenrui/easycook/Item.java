package com.example.chenrui.easycook;

public class Item {
    boolean checked;
    String ItemString;
    Item(String t, boolean b){
        ItemString = t;
        checked = b;
    }

    public boolean isChecked(){
        return checked;
    }
}
