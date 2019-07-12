package com.efe.logicvalidator;

import java.util.ArrayList;

public class Variable {

    public ArrayList<String> truthArray;
    public String tag;

    public Variable (String tag) {

        this.tag = tag;
        truthArray = new ArrayList<String>();

    }

}

