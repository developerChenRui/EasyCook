package com.example.chenrui.easycook;

import org.json.JSONObject;

public class Item {
    boolean checked;
    String ItemString;
    Item(String t, boolean b){
        ItemString = t;
        checked = b;
    }

    Item(){

    }

    public boolean isChecked(){
        return checked;
    }

    public JSONObject ExportJsonObj(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("checker", checked);
            jsonObject.put("ingName", ItemString);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void ImportJsonObj(JSONObject jsonObject){
        try{
            this.checked = jsonObject.getBoolean("checker");
            this.ItemString = jsonObject.getString("ingName");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}