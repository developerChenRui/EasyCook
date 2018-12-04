package com.example.chenrui.easycook;

import java.util.ArrayList;

public interface AsyncData {
    public void onData(ArrayList<Recipe> recipeList);
    public void onError(String errorMessage);
}
