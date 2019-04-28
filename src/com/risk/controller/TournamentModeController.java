/**
 * This class is a controller for the tournament mode view
 * 
 * @author DKM
 * @author Tianyi
 * @version 3.0
 */

package com.risk.controller;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.risk.model.MapModel;
import com.risk.model.PlayerPhaseModel;
import com.risk.model.exceptions.CannotFindException;
import com.risk.model.exceptions.DuplicatesException;
import com.risk.model.map.Country;
import com.risk.model.player.Player;
import com.risk.model.strategy.Aggressive;
import com.risk.model.strategy.Benevolent;
import com.risk.model.strategy.Cheater;
import com.risk.model.strategy.Random;
import com.risk.model.utilities.FileParser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class TournamentModeController implements Initializable 
{

    @FXML
    private ComboBox<String> typePlayerID;

    @FXML
    private Button assignMap;

    @FXML
    private ComboBox<Integer> selectMapID;

    @FXML
    private ComboBox<Integer> selectPlayerID;
    
    @FXML
    private Button startTournament;
    
    @FXML
    private ComboBox<String> typeMapID;

    @FXML
    private ListView<String> hiscoreView;
    
    @FXML
    private ListView<String> mapView;
    
    @FXML
    private ListView<Player> playerView;

    @FXML
    private ComboBox<Integer> numberMapsID;

    @FXML
    private ComboBox<Integer> numberPlayersID;
    
    @FXML
    private ComboBox<Integer> numberGamesID;

    @FXML
    private TextField numberTurnsID;
    
    // at the end this should be displayed
    ObservableList<String> hiscoreObservableList = FXCollections.observableArrayList();
    
    // these are the data you need arya
    ObservableList<Player> playerObservableList = FXCollections.observableArrayList();
    ObservableList<String> mapObservableList = FXCollections.observableArrayList();
    ArrayList<String> files = new ArrayList<String>(); 
   
    int turns;
    int games;

	/**
	 * This method is necessary for initialization of the necessary objects for the tournamentmode controller
	 * 
	 * @see javafx.fxml.Initializable
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		numberMapsID.getItems().addAll(1,2,3,4,5);
		numberPlayersID.getItems().addAll(2,3,4);
		numberGamesID.getItems().addAll(1,2,3,4,5);
		typeMapID.getItems().addAll("mapL1", "mapL2", "mapL3", "mapL4", "mapL5");
		typePlayerID.getItems().addAll("AggressivePlayer", "BenevolentPlayer", "CheaterPlayer", "RandomPlayer");
		
		hiscoreView.setItems(hiscoreObservableList);
		mapView.setItems(mapObservableList);
		playerView.setItems(playerObservableList);
        playerView.setCellFactory(param -> new ListCell<Player>() 
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
	}
	
	/**
	 * this method is necessary to start the game
	 * @throws FileNotFoundException exception thrown when file not found
	 * @throws DuplicatesException exception thrown when country is duplicated
	 * @throws CannotFindException exception  thrown when number of country in a continent is different from fixed number
	 */
	@FXML
	public void startTournament() throws FileNotFoundException, CannotFindException, DuplicatesException
	{
		if (selectMapID.getSelectionModel().getSelectedItem() != null && selectPlayerID.getSelectionModel().getSelectedItem() != null)
		{
			if (mapObservableList.size() == numberMapsID.getSelectionModel().getSelectedItem() 
					&& playerObservableList.size() == numberPlayersID.getSelectionModel().getSelectedItem()
					&& numberGamesID.getSelectionModel().getSelectedItem() != null
					&& !numberTurnsID.getText().trim().isEmpty()
					&& Integer.parseInt(numberTurnsID.getText()) >= 10
					&& Integer.parseInt(numberTurnsID.getText()) <= 50)
			{
				
				
				hiscoreObservableList.clear();
				turns = Integer.parseInt(numberTurnsID.getText());
				games = numberGamesID.getSelectionModel().getSelectedItem();
				
				String[][] result = new String[mapObservableList.size()+1][games+1];
				result[0][0] = ""; 
				for(int i = 1; i<mapObservableList.size()+1; i++) 
				{
					result[i][0] = "Map "+i;
				}
				for(int i = 1; i<games+1; i++) 
				{
					result[0][i] = "Game "+i;
				}
				for (int i = 0 ; i < mapObservableList.size(); i++)
				{
					String path = "src/com/risk/main/mapTextFiles/"+ mapObservableList.get(i) + ".txt";
					for(int j = 0; j< games; j++) 
					{
						MapModel.getMapModel().getContinents().clear();
			            MapModel.getMapModel().getCountries().clear();
			            PlayerPhaseModel.getPlayerModel().getPlayers().clear();
						MapEditorController mapEditor = new MapEditorController();
						Scanner scan = new Scanner (new File (path));
						FileParser fileParser = new FileParser();
						fileParser.init(scan);
						mapEditor.setPlayers(playerObservableList);
						mapEditor.setDeck();
						mapEditor.calcStartingArmies();
						mapEditor.autoAssignCountriesToPlayers();
						mapEditor.determinePlayersStartingOrder();
						System.out.println ("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$SETUP DONE");
						String winner = "";
						ArrayList<Player> playerList = PlayerPhaseModel.getPlayerModel().getPlayers();
						winner = tournamentRound(playerList, turns);
						if(winner.equals("")) 
						{
							result[i+1][j+1] = "draw";
						}else 
						{
							result[i+1][j+1] = winner;
						}
					}
				}
				boolean mapName = true;				
				for(int i=0; i<mapObservableList.size()+1; i++) 
				{
					for(int j = 0; j< games+1; j++) 
					{
						if (mapName)
						{
							mapName = false;
							hiscoreObservableList.add("map name");
						}
						System.out.printf("%20s", result[i][j]);
						
						hiscoreObservableList.add(result[i][j]);
					}
					
					System.out.println();
					hiscoreObservableList.add("              ");
				}
			}
		}
	}
	
	/**
	 * this method is necessary to select number of starting players
	 */
	@FXML
	public void selectNumbPlayers()
	{
		selectPlayerID.getItems().clear();
		playerObservableList.clear();
		int numbPlayers = numberPlayersID.getSelectionModel().getSelectedItem();
		for (int i = 1; i <= numbPlayers; i++)
		{
			selectPlayerID.getItems().add(i);
		}
	}
	
	/**
	 * this method is necessary to select number of maps
	 */
	@FXML
	public void selectNumbMaps()
	{
		selectMapID.getItems().clear();
		mapObservableList.clear();
		int numbMaps = numberMapsID.getSelectionModel().getSelectedItem();
		for (int i = 1; i <= numbMaps; i++)
		{
			selectMapID.getItems().add(i);
		}
	}

	/**
	 * 
	 * This method sets the strategy of players
	 * @param player this is the player without strategy
	 * @return returns player with strategy
	 */
    public Player setStrategy(Player player)
    {
    	String behaviour = player.getName();
   
    	if (behaviour.equals("AggressivePlayer"))
    	{
    		player.setStrategy(new Aggressive());
    	}
    	else if (behaviour.equals("BenevolentPlayer"))
    	{
    		player.setStrategy(new Benevolent());
    	}
    	else if (behaviour.equals("CheaterPlayer"))
    	{
    		player.setStrategy(new Cheater());
    	}
    	else if (behaviour.equals("RandomPlayer"))
    	{
    		player.setStrategy(new Random());
    	}
    	return player;
    }
	
    /**
     * this method checks whether the behavior has already been assigned
     * @param computerName name of the behavior
     * @return returns true if already assigned, else false
     */
	public boolean checkBehaviourAssigned(String computerName)
	{
		for (Player player : playerObservableList)
		{
			if (player.getName().equals(computerName))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * this method checks whether the map has already been assigned
	 * @param mapName type of the map
	 * @return returns true if map assigned else false
	 */
	public boolean checkMapAssigned(String mapName)
	{
		
		for (String map : mapObservableList)
		{
			
			if (map.equals(mapName))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * this method is necessary to assign different behaviors for a tournament
	 */
	@FXML
	public void assignPlayer()
	{
		if (selectPlayerID.getSelectionModel().getSelectedItem() != null && typePlayerID.getSelectionModel().getSelectedItem() != null)
		{
			String behaviour = typePlayerID.getSelectionModel().getSelectedItem();
			int playerIndex = selectPlayerID.getSelectionModel().getSelectedItem();
			
			if (!checkBehaviourAssigned(behaviour+playerIndex))
			{
				if (playerObservableList.size() < numberPlayersID.getSelectionModel().getSelectedItem())
				{
					boolean exists = false;
					for (Player p : playerObservableList)
					{
						if (p.getName().substring(0, p.getName().length()-1).equals(behaviour))
						{
							exists = true;
							break;
						}
					}
					if (!exists)
					{
						Player player =  new Player(behaviour+playerIndex);
						player = setStrategy(player);
						playerObservableList.add(player);
					}
					
				}
				else 
				{
					boolean exists = false;
					for (Player p : playerObservableList)
					{
						if (p.getName().substring(0, p.getName().length()-1).equals(behaviour))
						{
							exists = true;
							break;
						}
					}
					if (!exists)
					{
						Player player =  new Player(behaviour+playerIndex);
						player = setStrategy(player);
						playerObservableList.set(playerIndex-1, player);
					}
					
				}	
			}
		}
	}
	
	/**
	 * this method is necessary to select type of map
	 */
	@FXML
	public void assignMap()
	{
		if (selectMapID.getSelectionModel().getSelectedItem() != null && typeMapID.getSelectionModel().getSelectedItem() != null)
		{
			String mapName = typeMapID.getSelectionModel().getSelectedItem();
			int mapIndex = selectMapID.getSelectionModel().getSelectedItem();
			if (!checkMapAssigned(mapName))
			{
				if (mapObservableList.size() < numberMapsID.getSelectionModel().getSelectedItem())
				{
				
					mapObservableList.add(mapName);
				}
				else 
				{
					mapObservableList.set(mapIndex-1, mapName);
				}
			}
		}
	}
	
	public boolean checkWinner(Player p) 
	{
		return p.getOccupiedCountries().size() == MapModel.getMapModel().getCountries().size();
	}
	
	public String tournamentRound(ArrayList<Player> playerList, int turns)
	{
		String winner = "";
		int turn=0;
		boolean win = false;
		while(turn<turns&&!win) 
		{
			for(Player p: playerList) 
			{
				if(p.isPlayerLost())
					continue;
				else 
				{
					System.out.println("=============================================================");
					System.out.println(p.getName() + "'s turn");
					for (Country c : p.getOccupiedCountries())
					{
						System.out.println(c.getName() + " (" + c.getArmyCount() + ")");
					}
					System.out.println("===================");
					p.reinforceStrategy(p);
					
					for (Country c : p.getOccupiedCountries())
					{
						System.out.println(c.getName() + " (" + c.getArmyCount() + ")");
					}
					System.out.println("===================");
					p.attackStrategy(p);
					if(checkWinner(p)) 
					{
						winner = p.getName();
						win = true;
						break;
					}
					for (Country c : p.getOccupiedCountries())
					{
						System.out.println(c.getName() + " (" + c.getArmyCount() + ")");
					}
					System.out.println("===================");
					p.fortifyStrategy(p);
					for (Country c : p.getOccupiedCountries())
					{
						System.out.println(c.getName() + " (" + c.getArmyCount() + ")");
					}
				}		
			}
			turn++;
		}
		return winner;
	}
}
