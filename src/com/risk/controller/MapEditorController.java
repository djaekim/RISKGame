/**
 * 
 */
package com.risk.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.risk.model.GamePhaseModel;
import com.risk.model.MapModel;

import com.risk.model.PlayerModel;
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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * @author DKM
 *
 */

public class MapEditorController implements Initializable {
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
	ListView<String> ValidationError;

	@FXML
	Label AdjacentError;

	@FXML
	ComboBox<String> PlayerID;

	private int validated = 0;
	ObservableList<Country> territoryObservableList = FXCollections.observableArrayList();
	ObservableList<Country> adjacentObservableList = FXCollections.observableArrayList();
	ObservableList<String> messageObservableList = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		ValidationError.setItems(messageObservableList);
		
		ContinentView.setItems(MapModel.getMapModel().getContinents());
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

		// this adds observables to the view, meaning view now represents change in
		// observablist dynamically
		TerritoryView.setItems(territoryObservableList);

		// Looks long, but it all it does to access object properties so i can print
		// stuff to view
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
		// this adds observables to the view, meaning view now represents change in
		// observablist dynamically
		AdjacentView.setItems(adjacentObservableList);

		// Looks long, but it all it does to access object properties so i can print
		// stuff to view
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

	// when user clicks continent it loads territory associated with it
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

	// when user clicks territory it loads adjacent territory
	@FXML
	public void loadAdjacentHandler(MouseEvent arg0) 
	{
		if (TerritoryView.getSelectionModel().getSelectedItem() != null) 
		{
			adjacentObservableList.clear();
			adjacentObservableList.addAll(TerritoryView.getSelectionModel().getSelectedItem().getConnectedCountries());
		}
	}

	// add a territory
	@FXML
	public void territoryAddHandler() 
	{
		if (!TerritoryInput.getText().trim().isEmpty() && ContinentView.getSelectionModel().getSelectedItem() != null)
		{
			
			if (searchTerritory(TerritoryInput.getText()) == null)
			{
				Country country = new Country(TerritoryInput.getText(),
						ContinentView.getSelectionModel().getSelectedItem().getName());
				ContinentView.getSelectionModel().getSelectedItem().setCountry(country); 
				territoryObservableList.clear();
				territoryObservableList.addAll(ContinentView.getSelectionModel().getSelectedItem().getCountries());
				messageObservableList.add("added a territory to the continent");
				
			}
		}
	}
	
	// check whether territory already exists in the continents
	public Country searchTerritory(String countryName) 
	{
		Country territoryExists = null;
		for (Continent continent : ContinentView.getItems()) 
		{
			for  (Country country : continent.getCountries()) 
			{
				if (country.getName().equals(countryName)) 
				{
					territoryExists = country;
					messageObservableList.add("territory exists");
					break;
				}
			}
		}
		return territoryExists;
	}
	
	// add an adjacent territory
	@FXML
	public void adjacentAddHandler() 
	{
		if (!AdjacentInput.getText().trim().isEmpty() && TerritoryView.getSelectionModel().getSelectedItem() != null) 
		{

			if (AdjacentInput.getText().equals(TerritoryView.getSelectionModel().getSelectedItem().getName())) 
			{
				messageObservableList.add("You can't add a territory as it's own neighbour");

			} 
			else if (existsInAdjacentList(AdjacentInput.getText())) 
			{
				messageObservableList.add("Territory is already a neighbour");
			} 
			else if (searchTerritory(AdjacentInput.getText()) != null && !existsInAdjacentList(AdjacentInput.getText()))
			{
					Country country = searchTerritory(AdjacentInput.getText());
					TerritoryView.getSelectionModel().getSelectedItem().getConnectedCountries().add(country);
					country.getConnectedCountries().add(TerritoryView.getSelectionModel().getSelectedItem());
					adjacentObservableList.clear();	
					adjacentObservableList
							.addAll(TerritoryView.getSelectionModel().getSelectedItem().getConnectedCountries());
					messageObservableList.add("added neighbour");
			}
			else 
			{
				messageObservableList.add("territory doesn't exist");
			}
		}
	}

	private boolean existsInAdjacentList(String countryName) 
	{
		for (Country country : TerritoryView.getSelectionModel().getSelectedItem().getConnectedCountries()) 
		{
			if (country.getName().equals(countryName)) 
			{
				return true;
			}
		}
		return false;
	}

	@FXML
	public void territoryDeleteHandler() {
		if (TerritoryView.getSelectionModel().getSelectedItem() != null && ContinentView.getSelectionModel().getSelectedItem() != null) 
		{
			ContinentView.getSelectionModel().getSelectedItem().getCountries()
					.remove(TerritoryView.getSelectionModel().getSelectedItem());
			for (Country c : TerritoryView.getSelectionModel().getSelectedItem().getConnectedCountries()) 
			{
				for (Country connected : c.getConnectedCountries())
				{
					if (connected.getName().equals(TerritoryView.getSelectionModel().getSelectedItem().getName())) 
					{
						c.getConnectedCountries().remove(TerritoryView.getSelectionModel().getSelectedItem());
						break;
					}
				}
			}
			territoryObservableList.clear();
			adjacentObservableList.clear();
			territoryObservableList.addAll(ContinentView.getSelectionModel().getSelectedItem().getCountries());
			messageObservableList.add("territory deleted along with connections");
		} 
	}

	@FXML
	public void adjacentDeleteHandler() 
	{
		
		if (AdjacentView.getSelectionModel().getSelectedItem() != null && TerritoryView.getSelectionModel().getSelectedItem() != null) 
		{
			TerritoryView.getSelectionModel().getSelectedItem().getConnectedCountries()
					.remove(AdjacentView.getSelectionModel().getSelectedItem());

			for (Continent continent : ContinentView.getItems()) 
			{
				for (Country country : continent.getCountries())
				{
					if (country.getName().equals(AdjacentView.getSelectionModel().getSelectedItem().getName())) 
					{
						for (Country adj : country.getConnectedCountries())
						{
							if (adj.getName().equals(TerritoryView.getSelectionModel().getSelectedItem().getName()))
							{
								country.getConnectedCountries().remove(adj);
								break;
							}
						}
					}
				}
			}
			adjacentObservableList.clear();	
			adjacentObservableList.addAll(TerritoryView.getSelectionModel().getSelectedItem().getConnectedCountries());
		} 
	}

	@FXML
	public void loadMapHandler() throws CannotFindException, DuplicatesException, FileNotFoundException, CountLimitException 
	{
		validated = 0;
		clearMapEditor();
		String inputFile = "src/com/risk/main/mapTextFiles/"+ExistingFile.getText()+".txt";
		if (!ExistingFile.getText().trim().isEmpty() && new File(inputFile).isFile()) 
		{
			MapModel.getMapModel().getContinents().clear();
			MapModel.getMapModel().getCountries().clear();			
			Scanner scan = new Scanner(new File(inputFile));
			FileParser fileParser = new FileParser();
			if (fileParser.init(scan))
			{
				Validate.getValidate().validateMap();
				if (Validate.getValidate().getValidateSize() == MapModel.getMapModel().getCountries().size()) 
				{
					messageObservableList.add("Connected Map");
				} 
				else 
				{
					messageObservableList.add("Invalid Map. Might want to reassign territories");
				}
			}
			
		} 
		else
		{
			messageObservableList.add("file does not exist");
			clearMapEditor();
		}		
	}

	@FXML
	public void saveMapHandler() throws CannotFindException, CountLimitException {		
		if (MapModel.getMapModel().getCountries().size() != 0)
		{
			Validate.getValidate().validateMap();
			if (Validate.getValidate().getValidateSize() == MapModel.getMapModel().getCountries().size()) 
			{
				messageObservableList.add("Saved File");
				Output.generate(ExistingFile.getText());
				initializePlayers();
				validated = 1;
			} 
			else
			{
				messageObservableList.add("Can't Save Map it's an invalid map");
				validated = 0;
			}
		} 
		else 
		{
			messageObservableList.add("can't save - invalid map");
		} 
	}

	@FXML
	public void newMapHandler()
	{
		if (!ExistingFile.getText().trim().isEmpty()) 
		{
			validated = 0;
			clearMapEditor();
			initializeContinents();
			messageObservableList.add("New Map");
			
		} 
		else
		{
			messageObservableList.add("You need a name for the file");
		}
	}

	@FXML
	public void startGameHandler()
	{
		if (validated == 1) 
		{
			if (PlayerID.getSelectionModel().getSelectedItem() != null) 
			{
				int numbPlayers = Integer.parseInt(PlayerID.getSelectionModel().getSelectedItem());
				setPlayers(numbPlayers);
				calcStartingArmies();
				assignCountriesToPlayers();
				determinePlayersStartingOrder();
				GamePhaseModel.getGamePhaseModel().setPhase("reinforcement");
			}
		} 
		else 
		{
			messageObservableList.add("Need to save the map before you could start");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
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
		messageObservableList.add("This is a fixed map with the following continents");
	}

	public void initializePlayers() 
	{
		PlayerID.getItems().clear();
		PlayerID.getItems().addAll("2", "3", "4", "5", "6");
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
	 * @param numberOfPlayer the number of players
	 */
	public void setPlayers(int numberOfPlayer)
	{
		while (numberOfPlayer > 0)
		{
			PlayerModel.getPlayerModel().addPlayer(new Player(PlayerModel.PLAYERCOLOR[numberOfPlayer-1]));
			numberOfPlayer--;
		}
	}
	
	/**
	 * This method calculates the round robin. It first roll the dice to determine
	 * who goes first. The player who rolls the highest number starts the game. Then
	 * the play order goes clockwise from the starting player. The game starts after
	 * the order of play has been determined.
	 */
	public void determinePlayersStartingOrder()
	{
		int[] diceRolls = new int[PlayerModel.getPlayerModel().getNumberOfPlayer()];

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
				Player temp = PlayerModel.getPlayerModel().getPlayers().remove(0);
				PlayerModel.getPlayerModel().addPlayer(temp);
			}
		}
	}

	/**
	 * This method assigns countries to players
	 */
	public void assignCountriesToPlayers() 
	{
		boolean[] countryOccupied = new boolean[Country.MAX_NUMBER_OF_COUNTRIES];
		int i = 0;
		while (i < Country.MAX_NUMBER_OF_COUNTRIES) 
		{
			for (Player p : PlayerModel.getPlayerModel().getPlayers()) 
			{
				int random = (int) (Math.random() * Country.MAX_NUMBER_OF_COUNTRIES);
				while (countryOccupied[random]) 
				{
					random = (int) (Math.random() * Country.MAX_NUMBER_OF_COUNTRIES);
				}
				if (!countryOccupied[random])
				{
					MapModel.getMapModel().getCountries().get(random).setRuler(p);
					MapModel.getMapModel().getCountries().get(random).setIsOccupied(true);
					MapModel.getMapModel().getCountries().get(random).setArmyCount(1);

					countryOccupied[random] = true;
					p.getOccupiedCountries().add(MapModel.getMapModel().getCountries().get(random));
					i++;
				}
				if (i >= Country.MAX_NUMBER_OF_COUNTRIES)
				{
					break;
				}
			}
		}

		boolean[] armiesRemaining = new boolean[PlayerModel.getPlayerModel().getNumberOfPlayer()];
		boolean done = false;

		while (!done) 
		{
			for (int ii = 0; ii < PlayerModel.getPlayerModel().getNumberOfPlayer(); ii++) 
			{
				if (PlayerModel.getPlayerModel().getPlayers().get(ii).armiesLeft() > 0) 
				{
					int random = (int) (Math.random()
							* PlayerModel.getPlayerModel().getPlayers().get(ii).numbOccupied());
					PlayerModel.getPlayerModel().getPlayers().get(ii).getOccupiedCountries().get(random)
							.setArmyCount(1);
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
			if (countP == PlayerModel.getPlayerModel().getNumberOfPlayer())
			{
				done = true;
			}
		}
	}



	/**
	 * This method sets the starting armies during initialization based on number of
	 * PlayerModel.getPlayerModel().
	 */
	public void calcStartingArmies() 
	{
		for (Player player : PlayerModel.getPlayerModel().getPlayers()) 
		{
			player.setStartingPoints(calcStartingArmiesHelper(PlayerModel.getPlayerModel().getNumberOfPlayer()));
		}
	}

	/**
	 * This is a helper method for setStartingPoints. Based on the number of players
	 * it returns inital army count.
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