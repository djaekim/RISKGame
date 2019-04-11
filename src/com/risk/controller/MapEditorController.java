/**
 * This class is necessary for controller of the interactive Map Editor phase of the game.
 * It's responsible for file parsing, validating map, and assigning countries to players
 *
 * @author DKM
 * @version 3.0
 *
 */
package com.risk.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.risk.model.ActionModel;
import com.risk.model.DeckModel;
import com.risk.model.GamePhaseModel;
import com.risk.model.MapModel;

import com.risk.model.PlayerPhaseModel;
import com.risk.model.dice.Dice;
import com.risk.model.exceptions.CannotFindException;
import com.risk.model.exceptions.CountLimitException;
import com.risk.model.exceptions.DuplicatesException;
import com.risk.model.map.Continent;
import com.risk.model.map.Country;
import com.risk.model.player.Player;
import com.risk.model.utilities.FileParser;
import com.risk.model.utilities.Validate;
import com.risk.model.utilities.generateOutputFile.Output;
import com.risk.model.utilities.loadGame.LoadGame;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class MapEditorController implements Initializable 
{

    @FXML
    ListView<Continent> ContinentView;

    @FXML
    ListView<Country> TerritoryView;

    @FXML
    TextField TerritoryInput;

    @FXML
    ListView<Country> AdjacentView;

    @FXML
    TextField AdjacentInput;

    @FXML
    TextField ExistingFile;

    @FXML
    TextField ContinentInput;

    @FXML
    Label AdjacentError;

    @FXML
    ComboBox<String> PlayerID;

    @FXML
    RadioButton skipRobinID;

    @FXML
    RadioButton startCardsID;
    
    @FXML
    ListView <String> mapActionBoard;
    
    //
    @FXML
    ListView<Player> behaviourViewID;
    	
    @FXML
    ComboBox <String> playerForBehaviourID;
    
    @FXML
    ComboBox <String> typeOfBehaviourID;

    private int validated = 0;
    ObservableList<Country> territoryObservableList = FXCollections.observableArrayList();
    ObservableList<Country> adjacentObservableList = FXCollections.observableArrayList();
    ObservableList<Player> behaviourObservableList = FXCollections.observableArrayList();
    
    
    ActionModel actions;

    /**
     * This method is data binding for connection between controller and UI.
     *
     * @see javafx.fxml.Initializable
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1)
    {
    	mapActionBoard.setItems(ActionModel.getActionModel().getActions());
    	behaviourViewID.setItems(behaviourObservableList);
        actions = ActionModel.getActionModel();
        ContinentView.setItems(MapModel.getMapModel().getContinents());
        TerritoryView.setItems(territoryObservableList);
        AdjacentView.setItems(adjacentObservableList);
        renderView();
    }
    
    @FXML
    public void selectNumbPlayers()
    {
    	if (PlayerID.getSelectionModel().getSelectedItem() != null)
    	{	
    		typeOfBehaviourID.getItems().clear();
    		typeOfBehaviourID.getItems().addAll("AggressivePlayer", "HumanPlayer", "BenevolentPlayer", "CheaterPlayer", "RandomPlayer");
    		playerForBehaviourID.getItems().clear();
    		int number = Integer.parseInt(PlayerID.getSelectionModel().getSelectedItem());
    		for (int i = 1; i <= number; i++)
    		{
    			playerForBehaviourID.getItems().add(Integer.toString(i));
    		}
    	
    	}
    }
        
    /**
     * This method assigns behaviour to players
     */
    @FXML
    public void assignBehaviour()
    {
    	if (playerForBehaviourID.getSelectionModel().getSelectedItem() != null && typeOfBehaviourID.getSelectionModel().getSelectedItem() != null)
    	{
    		String behaviour = typeOfBehaviourID.getSelectionModel().getSelectedItem();
    		int playerIndex = Integer.parseInt(playerForBehaviourID.getSelectionModel().getSelectedItem());
    		Player player = new Player(behaviour+playerIndex);

    		int index = 0;
    		boolean notExist = true;
    		
    		for (Player p: behaviourObservableList)
    		{
    			if (p.getName().charAt(p.getName().length()-1) == player.getName().charAt(player.getName().length()-1))
    			{
    				behaviourObservableList.set(index, player);
    				notExist = false;
     				break;
    			}
    			index++;
    		}	
    		if (notExist)
    		{
    			behaviourObservableList.add(player);
    		}
		}   	
    }
    
    /**
     * This method loads the territory in the selected continent
     *
     * @param arg0 this listens for the mouse event in the ContinentView
     * ListView
     */
    @FXML
    public void loadTerritoryHandler(MouseEvent arg0)
    {
        if (ContinentView.getSelectionModel().getSelectedItem() != null)
        {
            territoryObservableList.clear();
            adjacentObservableList.clear();
            territoryObservableList.addAll(ContinentView.getSelectionModel().getSelectedItem().getCountries());
        }
    }

    /**
     * This method loads the adjacent territory of the selecter territory
     *
     * @param arg0 listens for the click event in the TerritoryView ListView
     */
    @FXML
    public void loadAdjacentHandler(MouseEvent arg0)
    {
        if (TerritoryView.getSelectionModel().getSelectedItem() != null)
        {
            adjacentObservableList.clear();
            adjacentObservableList.addAll(TerritoryView.getSelectionModel().getSelectedItem().getConnectedCountries());
        }
    }

    /**
     * This method handles adding territory to the continent
     */
    @FXML
    public void territoryAddHandler()
    {
        if (!TerritoryInput.getText().trim().isEmpty() && ContinentView.getSelectionModel().getSelectedItem() != null)
        {

            if (searchTerritory(ContinentView.getItems(), TerritoryInput.getText()) == null)
            {
                Country country = new Country(TerritoryInput.getText());
                country.setContinent(ContinentView.getSelectionModel().getSelectedItem());
                ContinentView.getSelectionModel().getSelectedItem().setCountry(country);
                MapModel.getMapModel().getCountries().add(country);
                territoryObservableList.clear();
                territoryObservableList.addAll(ContinentView.getSelectionModel().getSelectedItem().getCountries());
                actions.addAction("added a territory to the continent");
            }
        }
    }

    /**
     * This method checks if the territory already exists in the map
     *
     * @param continents arraylist of continents
     * @param countryName country to be searched in the map
     * @return if the country if it exists, otherwise null
     */
    public Country searchTerritory(ObservableList<Continent> continents, String countryName)
    {
        Country territoryExists = null;
        for (Continent continent : continents)
        {
            for (Country country : continent.getCountries())
            {
                if (country.getName().equals(countryName))
                {
                    territoryExists = country;
                    break;
                }
            }
        }
        return territoryExists;
    }

    /**
     * This method handles adding the neighbour in the territory
     */
    @FXML
    public void adjacentAddHandler()
    {
        if (!AdjacentInput.getText().trim().isEmpty() && TerritoryView.getSelectionModel().getSelectedItem() != null)
        {
            Country selectedCountry = TerritoryView.getSelectionModel().getSelectedItem();
            if (AdjacentInput.getText().equals(TerritoryView.getSelectionModel().getSelectedItem().getName()))
            {
                actions.addAction("You can't add a territory as it's own neighbour");

            }

            else if (existsInAdjacentList(selectedCountry, AdjacentInput.getText()))
            {
                actions.addAction("Territory is already a neighbour");
            }
            else if (searchTerritory(ContinentView.getItems(), AdjacentInput.getText()) != null && !existsInAdjacentList(selectedCountry, AdjacentInput.getText()))
            {
                Country country = searchTerritory(ContinentView.getItems(), AdjacentInput.getText());
                TerritoryView.getSelectionModel().getSelectedItem().getConnectedCountries().add(country);
                country.getConnectedCountries().add(TerritoryView.getSelectionModel().getSelectedItem());
                adjacentObservableList.clear();
                adjacentObservableList
                        .addAll(TerritoryView.getSelectionModel().getSelectedItem().getConnectedCountries());
                actions.addAction("added neighbour");
            }
            else
            {
                actions.addAction("territory doesn't exist");
            }
        }
    }

    /**
     * This method is used to check if the country input already exists in the
     * adjacent list of the selected country
     *
     * @param selectedCountry The country which is selected
     * @param addingCountry The name of country to be searched in the adjacent
     * list
     *
     * @return true if the country already exists in the adjacent list, false
     * otherwise
     */
    public boolean existsInAdjacentList(Country selectedCountry, String addingCountry)
    {
        for (Country country : selectedCountry.getConnectedCountries())
        {
            if (country.getName().equals(addingCountry))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * This method handles deleting a territory and all of it's connection in
     * the map
     */
    @FXML
    public void territoryDeleteHandler()
    {
        if (TerritoryView.getSelectionModel().getSelectedItem() != null && ContinentView.getSelectionModel().getSelectedItem() != null)
        {
        	
            Country removingTerritory = TerritoryView.getSelectionModel().getSelectedItem();
           
            Continent continent = ContinentView.getSelectionModel().getSelectedItem();
            continent.getCountries().remove(removingTerritory);

            for (Country connected : removingTerritory.getConnectedCountries())
            {
                connected.getConnectedCountries().remove(removingTerritory);
            }
            MapModel.getMapModel().getCountries().remove(removingTerritory);

            territoryObservableList.clear();
            adjacentObservableList.clear();
            territoryObservableList.addAll(continent.getCountries());
            actions.addAction("territory deleted along with connections");
        }
    }

    /**
     * This method handles deleting adjacent territories
     */
    @FXML
    public void adjacentDeleteHandler()
    {

        if (AdjacentView.getSelectionModel().getSelectedItem() != null && TerritoryView.getSelectionModel().getSelectedItem() != null)
        {

            Country selectedTerritory = TerritoryView.getSelectionModel().getSelectedItem();
            Country removingTerritory = AdjacentView.getSelectionModel().getSelectedItem();
            selectedTerritory.getConnectedCountries().remove(removingTerritory);
            removeAdjacentCountry(ContinentView.getItems(), selectedTerritory, removingTerritory);
            adjacentObservableList.clear();
            adjacentObservableList.addAll(TerritoryView.getSelectionModel().getSelectedItem().getConnectedCountries());
        }
    }

    /**
     * This method remove adjacent territory and all of its connections
     *
     * @param continentlist this is the list of all the continents
     * @param selectedCountry territory that was selected
     * @param removingCountry territory to remove from adjacency
     */
    public void removeAdjacentCountry(ObservableList<Continent> continentlist, Country selectedCountry, Country removingCountry)
    {
        String removingCountryName = removingCountry.getName();
        for (Continent continent : continentlist)
        {
            for (Country country : continent.getCountries())
            {
                if (country.getName().equals(removingCountryName))
                {
                    country.getConnectedCountries().remove(selectedCountry);
                    break;
                }
            }
        }
    }

    /**
     * This method handles loading the map to the UI from input file by parsing
     * and checking validity of the map
     *
     * @throws CannotFindException file format is invalid
     * @throws DuplicatesException country is duplicated
     * @throws FileNotFoundException file is not found
     * @throws CountLimitException continent has more or less than fixed number
     * of country
     */
    @FXML
    public void loadMapHandler() throws CannotFindException, DuplicatesException, FileNotFoundException, CountLimitException
    {
        validated = 0;
        clearMapEditor();
        String inputFile = "src/com/risk/main/mapTextFiles/" + ExistingFile.getText() + ".txt"; 
        String inputFileName = ExistingFile.getText();
       
        if (!ExistingFile.getText().trim().isEmpty() && LoadGame.generate(ExistingFile.getText())) {

        }
        else if (!ExistingFile.getText().trim().isEmpty() && new File(inputFile).isFile())
        {
            MapModel.getMapModel().getContinents().clear();
            MapModel.getMapModel().getCountries().clear();
            PlayerPhaseModel.getPlayerModel().getPlayers().clear();
            Scanner scan = new Scanner(new File(inputFile));
            FileParser fileParser = new FileParser();
            if (fileParser.init(scan))
            {
                Validate.getValidate().validateMap();
                if (Validate.getValidate().getValidateSize() == MapModel.getMapModel().getCountries().size())
                {
                	MapModel.getMapModel().setMapType(inputFileName);
                    actions.addAction("Connected map");
                }
                else
                {
                    actions.addAction("Disconnected Map. Might want to reassign territories");
                }
            }
        }
        else
        {
            actions.addAction("file does not exist");
            clearMapEditor();
        }
    }

    /**
     * This methods handles validation of edited map file before saving
     * outputting to a file
     *
     * @throws CannotFindException file format is invalid
     * @throws CountLimitException continent has more or less than fixed number
     * of country
     */
    @FXML
    public void saveMapHandler() throws CannotFindException, CountLimitException
    {
        if (MapModel.getMapModel().getCountries().size() != 0)
        {
            Validate.getValidate().validateMap();
            if (Validate.getValidate().getValidateSize() == MapModel.getMapModel().getCountries().size())
            {
            	System.out.println("validate getvalidate size() " + Validate.getValidate().getValidateSize());
            	System.out.println("mapmodel.getsize() " + MapModel.getMapModel().getCountries().size());
                actions.addAction("saved File");
                Output.generate(ExistingFile.getText());
                initializePlayers();
                validated = 1;
            }
            else
            {
            	System.out.println("validate getvalidate size() " + Validate.getValidate().getValidateSize());
            	System.out.println("mapmodel.getsize() " + MapModel.getMapModel().getCountries().size());
                actions.addAction("Can't Save Map it's an invalid map");
                validated = 0;
            }
        }
        else
        {
        	System.out.println("validate getvalidate size() " + Validate.getValidate().getValidateSize());
        	System.out.println("mapmodel.getsize() " + MapModel.getMapModel().getCountries().size());
            actions.addAction("Can't save - invalid map");
        }
    }

    /**
     * This method handles generating a new map with new file name
     */
    @FXML
    public void newMapHandler()
    {
        if (!ExistingFile.getText().trim().isEmpty())
        {
            validated = 0;
            clearMapEditor();
            initializeContinents();
            actions.addAction("This is a fixed map with the following continents");

            actions.addAction("New map");

        }
        else
        {
            actions.addAction("You need a name for a file");
        }
    }

    /**
     * This method initialize the initial number of cards for each player
     */
    public void startWithCards()
    {
        for (Player p : PlayerPhaseModel.getPlayerModel().getPlayers())
        {
            for (int i = 0; i < 5; i++)
            {
                DeckModel.getCardModel().sendCard(p);
            }
        }
    }

    /**
     * This method handles setting number of players, calculating starting
     * armies in each country, it assigns countries to players and determines
     * round robin turn. Once all of these are done it will set the phase to
     * reinforcement.
     */
    @FXML
    public void startGameHandler()
    {
        if (validated == 1)
        {

            if (PlayerID.getSelectionModel().getSelectedItem() != null && !skipRobinID.isSelected())
            {
            	
                int numbPlayers = Integer.parseInt(PlayerID.getSelectionModel().getSelectedItem());
                if (numbPlayers == behaviourObservableList.size())
                {
                    setPlayers(behaviourObservableList);
                    setDeck();
                    
                    calcStartingArmies();

                    assignCountriesToPlayers();
                    
                    determinePlayersStartingOrder(); 
                 
                    ActionModel.getActionModel().addAction("order of player goes ");
                    ActionModel.getActionModel().addAction("---------------------");
                    for (Player p : PlayerPhaseModel.getPlayerModel().getPlayers())
                    {
                    	 ActionModel.getActionModel().addAction(p.getName());
                    }
                    ActionModel.getActionModel().addAction("---------------------");
                    if (startCardsID.isSelected())
                    {
                        startWithCards();
                    }
                    GamePhaseModel.getGamePhaseModel().setPhase("setup");
                }
            }
            else
            {
            	
            	int numbPlayers = Integer.parseInt(PlayerID.getSelectionModel().getSelectedItem());
            	if (numbPlayers == behaviourObservableList.size())
                {
            		 setPlayers(behaviourObservableList);
                     setDeck();
             
                     calcStartingArmies();
                     
                     autoAssignCountriesToPlayers();
            
                    
                     determinePlayersStartingOrder();
                     
                     ActionModel.getActionModel().addAction("order of player goes ");
                     ActionModel.getActionModel().addAction("---------------------");
                     for (Player p : PlayerPhaseModel.getPlayerModel().getPlayers())
                     {
                     	 ActionModel.getActionModel().addAction(p.getName());
                     }
                     ActionModel.getActionModel().addAction("---------------------");
                     
                     if (startCardsID.isSelected())
                     {
                         startWithCards();
                     }
                     GamePhaseModel.getGamePhaseModel().setPhase("setup complete");
                     GamePhaseModel.getGamePhaseModel().setPhase("reinforcement");
                }
              
               
            }
        }
    }

    /**
     * This method updates the view of continent, territory and adjacent
     * territory
     */
    public void renderView()
    {
        ContinentView.setCellFactory(param -> new ListCell<Continent>() 
        {
            @Override
            protected void updateItem(Continent continent, boolean empty)
            {
                super.updateItem(continent, empty);
                if (empty || continent == null || continent.getName() == null)
                {
                    setText(null);
                }
                else
                {
                    setText(continent.getName());
                }
            }
        });
        behaviourViewID.setCellFactory(param -> new ListCell<Player>() 
        {
            @Override
            protected void updateItem(Player player, boolean empty)
            {
                super.updateItem(player, empty);
                if (empty || player == null || player.getName() == null)
                {
                    setText(null);
                }
                else
                {
                    setText(player.getName());
                }
            }
        });
        TerritoryView.setCellFactory(param -> new ListCell<Country>() 
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
                    setText(country.getName());
                }
            }
        });
        AdjacentView.setCellFactory(param -> new ListCell<Country>() 
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
                    setText(country.getName());
                }
            }
        });
    }

    /**
     * This method initializes fixed continents in the UI
     */
    public void initializeContinents()
    {
        ContinentView.getItems().clear();
        ArrayList<Continent> continent = new ArrayList<>();
        continent.add(new Continent("North America", 10));
        continent.add(new Continent("South America", 10));
        continent.add(new Continent("Europe", 10));
        continent.add(new Continent("Africa", 10));
        continent.add(new Continent("Asia", 10));
        continent.add(new Continent("Australia", 10));
        ContinentView.getItems().addAll(continent);

    }

    /**
     * This method initializes the 2 - 6 players in the UI
     */
    public void initializePlayers()
    {
        PlayerID.getItems().clear();
        int numberContinent = MapModel.getMapModel().getContinents().size();
        for (int i = 0; i < numberContinent - 1; i++)
        {
            PlayerID.getItems().add(Integer.toString(i + 2));
        }
    }

    /**
     * This method is a helper method for clearing the UI board
     */
    public void clearMapEditor()
    {
        ContinentView.getItems().clear();
        territoryObservableList.clear();
        adjacentObservableList.clear();
    }

    /**
     * sets the number of players
     *
     * @param players the number of players
     */
    public void setPlayers(ObservableList <Player> players)
    {     
    	
    	for (Player player : players)
    	{	
    		
    		Player p = new Player(player.getName());
    		PlayerPhaseModel.getPlayerModel().addPlayer(p);
    	}
    }
    

    /**
     * This method initializes the deck of our game
     */
    public void setDeck()
    {
        DeckModel.getCardModel().initialize();
    }

    /**
     * This method calculates the round robin. It first roll the dice to
     * determine who goes first. The player who rolls the highest number starts
     * the game. Then the play order goes clockwise from the starting player.
     * The game starts after the order of play has been determined.
     */
    public void determinePlayersStartingOrder()
    {
        int[] diceRolls = new int[PlayerPhaseModel.getPlayerModel().getNumberOfPlayer()];

        for (int i = 0; i < diceRolls.length; i++)
        {
            diceRolls[i] = Dice.roll();
        }
        int maxRollIndex = 0;
        for (int i = 1; i < diceRolls.length; i++)
        {
            if (diceRolls[maxRollIndex] < diceRolls[i])
            {
                maxRollIndex = i;
            }
        }
        boolean tieBreakingNeeded = false;
        for (int i = 1; i < diceRolls.length; i++)
        {
            if (diceRolls[maxRollIndex] == diceRolls[i] && maxRollIndex != i)
            {
                tieBreakingNeeded = true;
            }
        }
        if (tieBreakingNeeded)
        {
            determinePlayersStartingOrder();
        }
        else
        {
            for (int i = 0; i < maxRollIndex; i++)
            {
                Player temp = PlayerPhaseModel.getPlayerModel().getPlayers().remove(0);
                PlayerPhaseModel.getPlayerModel().addPlayer(temp);
            }
        }
    }

    /**
     * This method assigns countries to players
     */
    public void assignCountriesToPlayers()
    {
    	
        int totalCountrySize = MapModel.getMapModel().getCountries().size();
        System.out.println(totalCountrySize);
        boolean[] countryOccupied = new boolean[totalCountrySize];
        System.out.println(countryOccupied);
        int i = 0;
        while (i < totalCountrySize)
        {
            for (Player p : PlayerPhaseModel.getPlayerModel().getPlayers())
            {
                int random = (int) (Math.random() * totalCountrySize);
                while (countryOccupied[random])
                {
                    random = (int) (Math.random() * totalCountrySize);
                }
                if (!countryOccupied[random])
                {
                    MapModel.getMapModel().getCountries().get(random).setRuler(p);
                    MapModel.getMapModel().getCountries().get(random).setIsOccupied(true);
                    MapModel.getMapModel().getCountries().get(random).setArmyCount(1);

                    p.setStartingPoints(p.getStartingPoints() - 1);
                    countryOccupied[random] = true;
                    p.addCountry(MapModel.getMapModel().getCountries().get(random));
                    i++;
                }
                if (i >= totalCountrySize)
                {
                    break;
                }
            }
        }

    }

    /**
     * This method random assigns armies to setup stage
     */
    public void autoAssignCountriesToPlayers()
    {
    	
        int totalCountrySize = MapModel.getMapModel().getCountries().size();
        boolean[] countryOccupied = new boolean[totalCountrySize];
        
        int i = 0;
        while (i < totalCountrySize)
        {
            for (Player p : PlayerPhaseModel.getPlayerModel().getPlayers())
            {
            	
                int random = (int) (Math.random() * totalCountrySize);
                while (countryOccupied[random])
                {
                    random = (int) (Math.random() * totalCountrySize);
                }
                if (!countryOccupied[random])
                {
                    MapModel.getMapModel().getCountries().get(random).setRuler(p);
                    MapModel.getMapModel().getCountries().get(random).setIsOccupied(true);
                    MapModel.getMapModel().getCountries().get(random).setArmyCount(1);
                    p.setStartingPoints(p.getStartingPoints() - 1);
                    countryOccupied[random] = true;
                    p.addCountry(MapModel.getMapModel().getCountries().get(random));
                    i++;
                }
                if (i >= totalCountrySize)
                {
                    break;
                }
            }
        }

        boolean[] armiesRemaining = new boolean[PlayerPhaseModel.getPlayerModel().getNumberOfPlayer()];
        boolean done = false;

        while (!done)
        {
            for (int ii = 0; ii < PlayerPhaseModel.getPlayerModel().getNumberOfPlayer(); ii++)
            {
                if (PlayerPhaseModel.getPlayerModel().getPlayers().get(ii).getStartingPoints() > 0)
                {
                    int random = (int) (Math.random()
                            * PlayerPhaseModel.getPlayerModel().getPlayers().get(ii).numbOccupied());

                    PlayerPhaseModel.getPlayerModel().getPlayers().get(ii).getOccupiedCountries().get(random)
                            .setArmyCount(1);
                    PlayerPhaseModel.getPlayerModel().getPlayers().get(ii).setStartingPoints(PlayerPhaseModel.getPlayerModel().getPlayers().get(ii).getStartingPoints() - 1);
                }
                else
                {
                    armiesRemaining[ii] = true;
                }
            }
            int countP = 0;
            for (boolean d : armiesRemaining)
            {
                if (d)
                {
                    countP++;
                }
            }
            if (countP == PlayerPhaseModel.getPlayerModel().getNumberOfPlayer())
            {
                done = true;
            }
        }
    }

    /**
     * This method sets the starting armies during initialization based on
     * number of PlayerModel.getPlayerModel().
     */
    public void calcStartingArmies()
    {
        for (Player player : PlayerPhaseModel.getPlayerModel().getPlayers())
        {
            player.setStartingPoints(calcStartingArmiesHelper(PlayerPhaseModel.getPlayerModel().getNumberOfPlayer()));
        }
    }

    /**
     * This is a helper method for setStartingPoints. Based on the number of
     * players it returns initial army count.
     *
     * @param getPlayerSize number of players
     * @return startingP initial army count
     */
    public int calcStartingArmiesHelper(int getPlayerSize)
    {
        int startingP = 0;
        switch (getPlayerSize)
        {
            case 2:
                startingP = 40;
                break;
            case 3:
                startingP = 35;
                break;
            case 4:
                startingP = 30;
                break;
            case 5:
                startingP = 25;
                break;
            case 6:
                startingP = 20;
                break;
        }
        return startingP;

    }
}
