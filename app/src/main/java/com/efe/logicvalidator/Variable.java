package com.efe.logicvalidator;

import java.util.ArrayList;

//variable for representing simple proposition in premises and conclusion. Contains a truth table of every possible boolean combination.
public class Variable {

    ArrayList<String> truthArray;
    String tag;

    public Variable (String tag) {

        this.tag = tag;
        truthArray = new ArrayList<>();

    }

}

