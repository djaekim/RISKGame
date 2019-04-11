/**
 * Necessary for handling business logic for Reinforcement phase.
 *
 * @author DKM
 * @author Tianyi
 * @version 3.0
 *
 */
package com.risk.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import com.risk.model.map.Country;
import com.risk.model.player.Player;
import com.risk.model.ActionModel;
import com.risk.model.GamePhaseModel;
import com.risk.model.PlayerPhaseModel;
import com.risk.model.card.Card;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

public class ReinforcementController implements Initializable 
{

    public int TotalReinforcement;
    public ArrayList<String> cards = new ArrayList<>();

    @FXML
    FlowPane fPane;

    @FXML
    Label armyAvailable;

    @FXML
    TextField inputArmy;

    @FXML
    ListView<Country> countryId;

    @FXML
    Button nextPlayer;

    @FXML
    Label inputArmyError;

    @FXML
    Label CardMessage;

    @FXML
    ListView<Card> yourCard;

    @FXML
    ListView<Card> tradeCard;

    @FXML
    ListView<Country> adjacentEnemy;

    @FXML
    ListView<Country> adjacentOwned;

    @FXML
    Pane CardPane;

    @FXML
    AnchorPane child;

    @FXML
    private CardController cardController;

    ObservableList<Country> territoryObservableList = FXCollections.observableArrayList();
    ObservableList<Country> adjacentEnemyObservableList = FXCollections.observableArrayList();
    ObservableList<Country> adjacentOwnedObservableList = FXCollections.observableArrayList();
    Player player;
    ActionModel actions;

    /**
     * This is the constructor for the reinforcement controller
     */
    public ReinforcementController()
    {
    }

    /**
     * This method is data binding for connection between controller and UI.
     *
     *
     * @see javafx.fxml.Initializable
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

        cardController.addObserver(new cardObserver());
        player = PlayerPhaseModel.getPlayerModel().getCurrentPlayer();
        actions = ActionModel.getActionModel();
        TotalReinforcement = player.getReinforcementArmy();
        armyAvailable.setText("Army: " + Integer.toString(TotalReinforcement));
        territoryObservableList.addAll(player.getOccupiedCountries());
        countryId.setItems(territoryObservableList);
        adjacentEnemy.setItems(adjacentEnemyObservableList);
        adjacentOwned.setItems(adjacentOwnedObservableList);
        updateView();
        
    }

    /**
     * This method is responsible for filling the listView with adjacent
     * territories from currently selected territory
     */
    @FXML
    public void territoryHandler()
    {
        if (countryId.getSelectionModel().getSelectedItem() != null)
        {
            adjacentEnemyObservableList.clear();
            adjacentOwnedObservableList.clear();
            adjacentEnemyObservableList.addAll(countryId.getSelectionModel().getSelectedItem().getConnectedEnemy());
            adjacentOwnedObservableList.addAll(countryId.getSelectionModel().getSelectedItem().getConnectedOwned());
        }
    }

    /**
     * This method sets the number of available army to your occupied country
     */
    @FXML
    public void setArmy()
    {
        int Armyinput = 0;
        if (!inputArmy.getText().trim().isEmpty() && TotalReinforcement != 0
                && countryId.getSelectionModel().getSelectedItem() != null)
        {
            Country selectedCountry = countryId.getSelectionModel().getSelectedItem();
            Armyinput = Integer.parseInt(inputArmy.getText());

            if (Armyinput <= TotalReinforcement)
            {
          
                selectedCountry.getRuler().reinforce(selectedCountry, Armyinput);
                TotalReinforcement = TotalReinforcement - Armyinput;
                updateView();
                actions.addAction("Added " + Armyinput + " Army to " + selectedCountry.getName());
            }
            else
            {
                actions.addAction("you don't have that many reinforcements");
            }
            
            if (PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getCards().size() < 3 && TotalReinforcement == 0)
            {
            	 GamePhaseModel.getGamePhaseModel().setPhase("attack");
            } 
        }
        armyAvailable.setText(
                "Army: " + Integer.toString(TotalReinforcement));
    }
    
    /**
     * This class observes reinforcement calculated by trading card
     *
     * @author DKM
     *
     */
    private class cardObserver implements Observer 
    {

        @Override
        public void update(Observable o, Object arg)
        {
            if (arg != null)
            {
                int reinforcementFromCards = (int) arg;
                TotalReinforcement += reinforcementFromCards;
                armyAvailable.setText(
                        "Army: " + Integer.toString(TotalReinforcement));
                ActionModel.getActionModel().addAction("got " + reinforcementFromCards + " army");

                if (PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getCards().size() < 3)
                {

                    child.getChildren().clear();
                }
            }
            else if (arg == null)
            {
                child.getChildren().clear();
            }
        }
    }

    /**
     * This method for re-rendering the listView
     */
    public void updateView()
    {
        countryId.setCellFactory(param -> new ListCell<Country>() 
        {
            @Override
            protected void updateItem(Country country, boolean empty)
            {
                super.updateItem(country, empty);
                if (empty || country == null || country.getName() == null)
                {
                    setText(null);
                }
                else
                {
                    setText(country.getName() + " (" + country.getArmyCount() + ")");
                }
            }
        });

        adjacentOwned.setCellFactory(param -> new ListCell<Country>() 
        {
            @Override
            protected void updateItem(Country country, boolean empty)
            {
                super.updateItem(country, empty);
                if (empty || country == null || country.getName() == null)
                {
                    setText(null);
                }
                else
                {

                    setText(country.getName() + " (" + country.getArmyCount() + ")");
                }
            }
        });

        adjacentEnemy.setCellFactory(param -> new ListCell<Country>() 
        {
            @Override
            protected void updateItem(Country country, boolean empty)
            {
                super.updateItem(country, empty);
                if (empty || country == null || country.getName() == null)
                {
                    setText(null);
                }
                else
                {

                    setText(country.getName() + " (" + country.getArmyCount() + ")");
                }
            }
        });
    }

}
