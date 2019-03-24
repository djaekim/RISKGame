/**
 *
 */
package com.risk.model;

import java.util.Observable;

import com.risk.model.map.Continent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author DKM
 *
 */
public class ActionModel extends Observable {

    private ObservableList<String> actions = FXCollections.observableArrayList();
    private static ActionModel actionModel;

    /**
     * This method is necessary for adding game action to observableList
     * 
     * @param action
     */
    public void addAction(String action)
    {
        actions.add(action);
    }

    /**
     * This method is necessary for clearing observableList
     */
    public void clearAction()
    {
        actions.clear();
    }

    /**
     * This method returns the observableList of action
     * 
     * @return
     */
    public ObservableList<String> getActions()
    {
        return actions;
    }

    /**
     * singleton pattern for returning only once instance of object
     *
     * @return actionModel object
     */
    public static ActionModel getActionModel()
    {
        if (actionModel == null)
        {
            actionModel = new ActionModel();
        }
        return actionModel;
    }

}
