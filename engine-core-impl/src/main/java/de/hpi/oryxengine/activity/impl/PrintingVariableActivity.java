package de.hpi.oryxengine.activity.impl;

import de.hpi.oryxengine.activity.Activity;
import de.hpi.oryxengine.processInstance.ProcessInstance;

/**
 * The Class PrintingVariableActivity.
 * Prints out a variable value which the activity gets in its constructor.
 */
public class PrintingVariableActivity implements Activity {

    /** The variable name. */
    private String variableName;

    /**
     * Instantiates a new printing variable activity.
     *
     * @param variableToBePrinted the variable to be printed
     */
    public PrintingVariableActivity(String variableToBePrinted) {

        variableName = variableToBePrinted;
    }

    /** 
     * Just prints out the value of the variable which gets set in the constructor. 
     * 
     * @param instance
     *            the processinstance (to get the variable)
     * @see de.hpi.oryxengine.activity.Activity#execute(de.hpi.oryxengine.processInstance.ProcessInstance)
     */
    public void execute(ProcessInstance instance) {

        String variableValue = (String) instance.getVariable(variableName).toString();
        System.out.println("In der Variable " + variableName + " steht " + variableValue + " .");
    }

}
