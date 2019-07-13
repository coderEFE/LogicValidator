package com.efe.logicvalidator;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Variable> variableArray = new ArrayList<>();
    EditText premiseText;
    EditText conclusionText;
    String expr;
    String expr2;
    static boolean errorShowing = false;
    Dialog infoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initiate the info dialog
        infoDialog = new Dialog(this);

        //get text input from premises and conclusion
        premiseText = findViewById(R.id.editText);
        premiseText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        conclusionText = findViewById(R.id.editText2);
        conclusionText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        //set on click listener for button
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //reset errors
                errorShowing = false;
                //get input from premises...
                expr = premiseText.getText().toString();
                expr = expr.replaceAll("\\s","");
                expr = expr.replace("!!","");
                //...and conclusion
                expr2 = conclusionText.getText().toString();
                expr2 = expr2.replaceAll("\\s","");
                expr = expr.replace("!!","");
                //check if input is empty
                if (!expr.isEmpty() && !expr2.isEmpty()) {
                    //make a variable array
                    variableArray = new ArrayList<>();
                    //evaluate and make truth tables out of premise variables
                    List<String> premiseList = Arrays.asList(expr.split(","));
                    for (int i = 0; i < premiseList.size(); i++) {
                        for (int j = 0; j < premiseList.get(i).length(); j++) {
                            //check if character in premise is a variable, not a true, false, or logic operator
                            if (Character.isLetter(premiseList.get(i).charAt(j))) {
                                String variable = ("" + Character.toUpperCase(premiseList.get(i).charAt(j)));
                                //also check if that letter is already a variable
                                if (notInVariables(variable)) {
                                    variableArray.add(new Variable(variable));
                                }
                                //set all variables in premise list to uppercase to prevent a later function from replacing letters within booleans
                                premiseList.set(i, premiseList.get(i).replace(premiseList.get(i).charAt(j), Character.toUpperCase(premiseList.get(i).charAt(j))));
                            }
                        }
                    }

                    //evaluate and make single truth table from conclusion
                    for (int j = 0; j < expr2.length(); j++) {
                        //check if character in conclusion is a variable, not a true, false, or logic operator
                        if (Character.isLetter(expr2.charAt(j))) {
                            String variable = ("" + Character.toUpperCase(expr2.charAt(j)));
                            //also check if that letter is already a variable
                            if (notInVariables(variable)) {
                                variableArray.add(new Variable(variable));
                            }
                            //upper case variables are the same as lower case variables in this parser
                            //set all variables in premise list to uppercase to prevent a later function from replacing letters within booleans
                            expr2 = expr2.replace(expr2.charAt(j), Character.toUpperCase(expr2.charAt(j)));
                        }
                    }
                    //generate truth tables for variables with every possible configuration of boolean values
                    setTruthTables();
                    //simplify each premise into a single boolean value
                    ArrayList<List<String>> premiseTables = evaluatePremises(premiseList);
                    //simplify the conclusion into a boolean value
                    List<String> conclusionList = new ArrayList<>();
                    conclusionList.add(expr2);
                    ArrayList<List<String>> conclusionTable = evaluatePremises(conclusionList);

                    if (!errorShowing) {
                        //determine validity of logic argument
                        //TextView adviceView = findViewById(R.id.adviceView);
                        ConstraintLayout constraintLayout = findViewById(R.id.root);
                        if (argumentIsValid(premiseTables, conclusionTable)) {
                            Snackbar snackbar = Snackbar.make(constraintLayout, "The argument is VALID (but not necessarily true).", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            //adviceView.setText("The argument is VALID (but not necessarily true).");
                        } else {
                            Snackbar snackbar = Snackbar.make(constraintLayout, "The argument is INVALID (but not necessarily false).", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            //adviceView.setText("The argument is INVALID (but not necessarily false).");
                        }
                    }
                } else {
                    //if input is empty, display error in snackbar
                    ConstraintLayout constraintLayout = findViewById(R.id.root);
                    Snackbar snackbar = Snackbar.make(constraintLayout, "The premises and conclusion cannot be empty", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

    }

    //A logical argument is valid only if the premises are all true, but the conclusion is false
    private boolean argumentIsValid (ArrayList<List<String>> premiseTables, ArrayList<List<String>> conclusionTable) {
        for (int i = 0; i < premiseTables.size(); i++) {
            //calculate how many booleans in premise tables are true
            int numTrue = 0;
            for (int j = 0; j < premiseTables.get(i).size(); j++) {
                if (premiseTables.get(i).get(j).contains("true")) {
                    numTrue++;
                }
            }
            //System.out.println(numTrue);
            //check if all premises contain the true boolean and the conclusion contains the false boolean
            if (numTrue == premiseTables.get(i).size() && conclusionTable.get(i).get(0).contains("false")) {
                //state that the argument is invalid
                return false;
            }
        }
        //otherwise, state that the argument is valid
        return true;
    }

    private ArrayList<List<String>> evaluatePremises (List<String> premiseList) {
        //list of all parsed premise truth tables
        ArrayList<List<String>> evaluatedTables = new ArrayList<>();
        //make a list of premises with variables replaced by truth vales and parsed with a string-to-boolean parser
        List<String> newPremises = new ArrayList<>();
        for (int k = 0; k < variableArray.get(0).truthArray.size(); k++) {
            //reset premises before changing variables to booleans
            for (int i = 0; i < premiseList.size(); i++) {
                if (newPremises.size() < premiseList.size()) {
                    newPremises.add("");
                }
                newPremises.set(i, premiseList.get(i));
            }
            //replace variables with boolean values and parse logic premise
            for (int i = 0; i < premiseList.size(); i++) {
                for (int j = 0; j < variableArray.size(); j++) {
                    String replacedPremise = newPremises.get(i).replace(variableArray.get(j).tag, variableArray.get(j).truthArray.get(k));

                    newPremises.set(i, replacedPremise);
                }
                //change string to a boolean and evaluate the boolean in a parser
                try {
                    newPremises.set(i, new Parser(new Lexer(newPremises.get(i))).parseAndExecute() + "");
                } catch (LexException | ParseException e) {
                    //e.printStackTrace();
                    //display error with a snackbar
                    if (!errorShowing) {
                        ConstraintLayout constraintLayout = findViewById(R.id.root);
                        //get error message
                        Snackbar snackbar = Snackbar.make(constraintLayout, e.getMessage(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        errorShowing = true;
                    }
                }
            }
            //add parsed premise to evaluatedTables array
            List<String> table = new ArrayList<>(newPremises);
            evaluatedTables.add(table);
        }
        //System.out.println(evaluatedTables);
        return evaluatedTables;
    }

    //checks if a certain tag (a string) is already a variable
    private boolean notInVariables (String tag) {
        for (Variable var : variableArray) {
            if (tag.equals(var.tag)) {
                return false;
            }
        }
        return true;
    }

    //makes truth tables based on variables.
    /*
    Example with 1 variable:
        1stVar: true, false.
    Example with 2 variables:
        1stVar: true, true, false, false. 2ndVar: true, false, true, false.
    */
    private void setTruthTables () {
        for (int i = 0; i < variableArray.size(); i++) {
            if (variableArray.get(i).truthArray.size() > 0) {
                variableArray.get(i).truthArray.clear();
            }
            //add boolean values depending on number of variables
            for (int j = 0; j < (Math.pow(2, i)); j++) {
                for (int k = 0; k < ((Math.pow(2, variableArray.size()) / (Math.pow(2, (i + 1))))); k++) {
                    variableArray.get(i).truthArray.add("true");
                }
                for (int k = 0; k < ((Math.pow(2, variableArray.size()) / (Math.pow(2, (i + 1))))); k++) {
                    variableArray.get(i).truthArray.add("false");
                }
            }
        }
    }

    //show info popup when floating action button is pressed
    public void ShowPopup(View v) {
        Button txtclose;
        infoDialog.setContentView(R.layout.custompopup);
        txtclose = (Button) infoDialog.findViewById(R.id.btnBack);
        //back button
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });
        //show dialog
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoDialog.show();
    }

}
