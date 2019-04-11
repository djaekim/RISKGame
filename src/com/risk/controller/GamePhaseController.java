/**
 * This file is necessary for changing the phases of the RISK game
 *
 * @author DKM
 * @version 3.0
 *
 */
package com.risk.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import com.risk.model.ActionModel;
import com.risk.model.GamePhaseModel;
import com.risk.model.MapModel;
import com.risk.model.PlayerPhaseModel;
import com.risk.model.map.Continent;
import com.risk.model.map.Country;
import com.risk.model.player.Player;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GamePhaseController implements Observer, Initializable 
{

    ReinforcementController rController;
    AttackController aController;
    FortificationController fController;
    SetUpController sController;
    ComputerController cpuController;
    
    @FXML
    SaveProgressController saveController;

    ObservableList<XYChart.Series<String, Integer>> barData = FXCollections.observableArrayList();
    ObservableList<Data<String, Integer>> data = FXCollections.observableArrayList();
    XYChart.Series<String, Integer> serie = new XYChart.Series<String, Integer>();
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

    ArrayList<Player> piePlayersList = new ArrayList<>();
    ArrayList<Player> barPlayersList = new ArrayList<>();

    Scene scene;
    Parent root;
    String view = "setup";
    Stage primaryStage;

    @FXML
    Pane mainPane;

    @FXML
    AnchorPane savePane;
    
    @FXML
    TextField playerID;

    @FXML
    TextField phaseID;

    @FXML
    private PieChart worldDomination1;

    @FXML
    private BarChart<String, Integer> worldDomination2;

    @FXML
    private ListView<Continent> worldDomination3;

    @FXML
    private CategoryAxis X;

    @FXML
    private NumberAxis y;

    @FXML
    ListView<String> actionMessage;
    
    @FXML
    AnchorPane phaseDominationViewID;

    @FXML
    AnchorPane winnerPane;

    @FXML
    Text winnerID;
    
    @FXML
    Button saveID;
    
    @FXML
    Button newID;
    
    PauseTransition pause;
    

    /**
     * This is a constructor for GamePhaseController
     * @param stage stage of our javafx scene
     */
    public GamePhaseController(Stage stage)
    {
        GamePhaseModel.getGamePhaseModel().addObserver(this);   
        primaryStage = stage;
    }
    
    /**
     * This is a default constructor
     */
    public GamePhaseController()
    {
    	
    }

    /**
     * this method saves the progress of the current game
     */
    @FXML
    public void saveProgress()
    {
    	savePane.setVisible(true);
    	saveController.showSaveProgress();
    }
    
    /**
     * This method creates a new game
     */
    @FXML
    public void newGame()
    {
    	GamePhaseModel.getGamePhaseModel().setPhase("startup");
    	newID.setVisible(false);
    }

    /**
     * This method sets a delay in the phase transition
     * @param phase name of the phase
     * @param second time delay
     */
    public void delayNextPhase(String phase, int second)
    {
    	pause = new PauseTransition(Duration.seconds(second));
        pause.setOnFinished(event -> 
        {
           if (phase.equals("attack"))
           {
        	   GamePhaseModel.getGamePhaseModel().setPhase("attack"); 
           } 
           else if (phase.equals("fortification"))
           {
        	   GamePhaseModel.getGamePhaseModel().setPhase("fortification"); 
           } 
           else if (phase.equals("next"))
           {
        	   ActionModel.getActionModel().addAction(PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getName() + " has finished move");
        	   PlayerPhaseModel.getPlayerModel().setNextPlayer();
               GamePhaseModel.getGamePhaseModel().setPhase("reinforcement");   
           }
        });
        pause.play();
    }
    
    /**
     * This method sets a delay in player strategies
     * @param phase name of the phase 
     * @param second time delay
     */
    public void delayPhaseAction(String phase, int second)
    {
    	pause = new PauseTransition(Duration.seconds(second));
        pause.setOnFinished(event -> 
        {
       	
           if (phase.equals("reinforced"))
           {
        	   PlayerPhaseModel.getPlayerModel().getCurrentPlayer().reinforceStrategy(PlayerPhaseModel.getPlayerModel().getCurrentPlayer());
        	 
        	
           }        
           else if (phase.equals("attacked"))
           {
        	   PlayerPhaseModel.getPlayerModel().getCurrentPlayer().attackStrategy(PlayerPhaseModel.getPlayerModel().getCurrentPlayer());

        	  
               
           } 
           else if (phase.equals("fortified"))
           {
        	   PlayerPhaseModel.getPlayerModel().getCurrentPlayer().fortifyStrategy(PlayerPhaseModel.getPlayerModel().getCurrentPlayer());
        	
           } 
        });
        pause.play();
    }

   
    /**
     * This method receives notification from the changes in the state from the
     * GamePhaseModel, then changes the scene to the next phase.
     */
    @SuppressWarnings("unchecked")
	@Override
    public void update(Observable o, Object phase)
    {
    
        view = (String) phase;
        if (!view.equals("setup") && !view.equals("startup"))
        {	
        	saveID.setVisible(true);
        	phaseDominationViewID.setVisible(true);
        }	
        if (view.equals("setup"))
        {
            try
            {
	        	 if (PlayerPhaseModel.getPlayerModel().getCurrentPlayer().isComputerPlayer())
	             {
        	        
        	        boolean isAnyPlayerPlacedAllArmies = true;
        	        for (Player p : PlayerPhaseModel.getPlayerModel().getPlayers())
        	        {
        	            if (p.getStartingPoints() != 0)
        	            {
        	                isAnyPlayerPlacedAllArmies = false;
        	            }
        	        } 
        	        
        	        if (isAnyPlayerPlacedAllArmies)
        	        {
        	        	System.out.println("next player set after setup is : " + PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getName());
                        GamePhaseModel.getGamePhaseModel().setPhase("setup complete");
                        GamePhaseModel.getGamePhaseModel().setPhase("reinforcement");
        	        } 
        	        else
        	        {
        	        	
    	        		PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getStrategy().setup(PlayerPhaseModel.getPlayerModel().getCurrentPlayer());
    	        		PlayerPhaseModel.getPlayerModel().setNextPlayer();
//    	        		GamePhaseModel.getGamePhaseModel().setPhase("setup complete");
    	        		GamePhaseModel.getGamePhaseModel().setPhase("setup");
        	        }	        		
	             } 
	        	 
	        	 else 
	        	 {
	        		 ActionModel.getActionModel().addAction("==== RULE =====");
	        		 ActionModel.getActionModel().addAction("place 1 army");
	        		 ActionModel.getActionModel().addAction("=============");
	        		 phaseID.setText(view);
	                 playerID.setText(PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getName());
	                 mainPane.getChildren().clear();
	                 mainPane.getChildren().add(FXMLLoader.load(getClass().getResource("/com/risk/view/SetUp.fxml")));
	                 GamePhaseModel.getGamePhaseModel().setPhase("setup complete");
	                 phaseDominationViewID.setVisible(true);
	                 
	        	 }
               
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if (view.equals("backToAttack"))
        {
        	phaseID.setText("attacking");
        }
        else if (view.equals("Conquering"))
        {
        	phaseID.setText(view);
        }
        else if (view.equals("startup"))
        {
        	saveController.closeSaveProgress();
        	saveID.setVisible(true);
        	phaseDominationViewID.setVisible(false);
        	restartGame();            
        }
        else if (view.equals("winner"))
        {
            phaseID.setText(view);
            winnerID.setText(PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getName() + " is the winner");
            mainPane.getChildren().clear();
            phaseDominationViewID.setVisible(false);
            saveID.setVisible(false);
            winnerPane.setVisible(true);
        }
        else if (view.equals("setup complete"))
        {
            phaseID.setText(view);
            HashSet<String> s = new HashSet<String>();
            for (Continent continent : MapModel.getMapModel().getContinents())
            {
                for (Country country : continent.getCountries())
                {
                    s.add(country.getRuler().getName());
                }
                if (s.size() == 1)
                {
                    continent.setRuler(continent.getCountries().get(0).getRuler());
                }
                else
                {
                    continent.setRuler(null);
                }
                s.clear();
            }
            worldDomination2.getData().setAll(serie);
            worldDomination3.setItems(MapModel.getMapModel().getContinents());
            updateContinentDominationView();
        }
        
        else if (view.equals("reinforcement"))
        {
            try
            {
                if (PlayerPhaseModel.getPlayerModel().getCurrentPlayer().isComputerPlayer())
                {
                	
                	ActionModel.getActionModel().addAction("  ");
                	
      			 	ActionModel.getActionModel().addAction("{{ CPU is reinforcing }}");
                	phaseID.setText(view);
                    playerID.setText(PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getName());
                    mainPane.getChildren().clear();
                    
                    mainPane.getChildren().add(FXMLLoader.load(getClass().getResource("/com/risk/view/ComputerView.fxml")));
                     
                    delayPhaseAction("reinforced",2);
                    delayNextPhase("attack", 5);

                     
                }  
                else 
                {
                	  
                	  ActionModel.getActionModel().addAction("  ");
                	  ActionModel.getActionModel().addAction("Reinforce Start:");
            		  ActionModel.getActionModel().addAction("======= Rule =======");
            		  ActionModel.getActionModel().addAction("[1] Exchange card first");
            		  ActionModel.getActionModel().addAction("[2] Select a country to");
            		  
            		  ActionModel.getActionModel().addAction("    reinforce");
            		  ActionModel.getActionModel().addAction("[3] Put number of army");
            		  ActionModel.getActionModel().addAction("[3] Click reinforce button");
            		  ActionModel.getActionModel().addAction("==================");
                	 phaseID.setText(view);
                     playerID.setText(PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getName());
                     mainPane.getChildren().clear();
                     mainPane.getChildren().add(FXMLLoader.load(getClass().getResource("/com/risk/view/Reinforcement.fxml")));
                     
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if (view.equals("attack"))
        {
            try
            {
	      		 if (!PlayerPhaseModel.getPlayerModel().getCurrentPlayer().checkIfEnemyAround())
	      		 {

     			   ActionModel.getActionModel().addAction(PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getName() + " skipped attack (no enemey around)");
	      		   GamePhaseModel.getGamePhaseModel().setPhase("fortification");
	      		   
	      		 }
	      		 else if (PlayerPhaseModel.getPlayerModel().getCurrentPlayer().isComputerPlayer())
                 {
	      			      ActionModel.getActionModel().addAction("  ");
	      			 
	      			 	  ActionModel.getActionModel().addAction("{{ CPU is attacking }}");
            			  phaseID.setText(view);
                          playerID.setText(PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getName());
                         
                          mainPane.getChildren().clear();
                          
                          mainPane.getChildren().add(FXMLLoader.load(getClass().getResource("/com/risk/view/ComputerView.fxml")));
                       
                          delayPhaseAction("attacked",2);
                          delayNextPhase("fortification", 5);

                 }
            	 else 
            	 {
            	      ActionModel.getActionModel().addAction("  ");
            		  ActionModel.getActionModel().addAction("Attack Start:");
            		  ActionModel.getActionModel().addAction("======= Rule =======");
            		  ActionModel.getActionModel().addAction("[1] Choose an attacking");
            		  ActionModel.getActionModel().addAction("    country");
            		  ActionModel.getActionModel().addAction("[2] Please choose a defending");
            		  ActionModel.getActionModel().addAction("    country");
            		  ActionModel.getActionModel().addAction("[3] to attack select:");
            		  ActionModel.getActionModel().addAction("    Allout or Single Roll");
            		  ActionModel.getActionModel().addAction("==================");
            		
            		  
            		  phaseID.setText(view);
                      playerID.setText(PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getName());

                      mainPane.getChildren().clear();
                      mainPane.getChildren().add(FXMLLoader.load(getClass().getResource("/com/risk/view/Attack.fxml")));
            	 }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        else if (view.equals("fortification"))
        {
            try
            {
	      		 if (!PlayerPhaseModel.getPlayerModel().getCurrentPlayer().isAnyCountriesConnected())
	      		 {
	      		   ActionModel.getActionModel().addAction(PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getName() + " skipped fortify (no ally around)");
	      		   PlayerPhaseModel.getPlayerModel().setNextPlayer();
	      		   GamePhaseModel.getGamePhaseModel().setPhase("reinforcement");
	      		 }
            	 else if (PlayerPhaseModel.getPlayerModel().getCurrentPlayer().isComputerPlayer())
                 {
            		  ActionModel.getActionModel().addAction("  ");
     			 	  
     			 	 ActionModel.getActionModel().addAction("{{ CPU is fortifying }}");
            		  phaseID.setText(view);
                      playerID.setText(PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getName());
                      mainPane.getChildren().clear();
                      
                      mainPane.getChildren().add(FXMLLoader.load(getClass().getResource("/com/risk/view/ComputerView.fxml")));
                      
                      delayPhaseAction("fortified",2);
                      delayNextPhase("next",5);



                 }
            	 else 
            	 {
            		  ActionModel.getActionModel().addAction("  ");
            	      ActionModel.getActionModel().addAction("Fortify Start:");
            	      ActionModel.getActionModel().addAction("======= Rule =======");
	           		  ActionModel.getActionModel().addAction("[1] Choose a fortify from");
	           		  ActionModel.getActionModel().addAction("    country");
	           		  ActionModel.getActionModel().addAction("[2] Choose a fortify to");
	           		  ActionModel.getActionModel().addAction("    country");
	           		  ActionModel.getActionModel().addAction("[3] Put number of army");
	           		  ActionModel.getActionModel().addAction("    and move");
	           		  ActionModel.getActionModel().addAction("==================");
            		  phaseID.setText(view);
                      playerID.setText(PlayerPhaseModel.getPlayerModel().getCurrentPlayer().getName());
                      mainPane.getChildren().clear();
                      mainPane.getChildren().add(FXMLLoader.load(getClass().getResource("/com/risk/view/Fortification.fxml")));
            	 }
              
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method observes the PlayerPhaseModel when a country has been
     * occupied
     */
    private class playerObserver implements Observer 
    {
        
        @Override
        public void update(Observable o, Object arg)
        {
        	System.out.println("pie chart");
            Country c = (Country) arg;
            Player player = c.getRuler();
            int index = piePlayersList.indexOf(player);

            System.out.println((player.getOccupiedCountries().size() + " / " + MapModel.getMapModel().getCountries().size()));
            if (checkWinner(player))
            {
                GamePhaseModel.getGamePhaseModel().setPhase("winner");
                return;
            }

            if (index == -1)
            {
                piePlayersList.add(player);
                pieChartData.add(new PieChart.Data(player.getName() + " (" + Math.round(player.getOccupiedCountries().size() * 100.0 / MapModel.getMapModel().getCountries().size()) + "%)",
                        player.getOccupiedCountries().size() * 100.0 / 42));
            }
            else
            {
                piePlayersList.set(index, player);
                pieChartData.get(index).setName(player.getName() + " (" + Math.round(player.getOccupiedCountries().size() * 100.0 / MapModel.getMapModel().getCountries().size()) + "%)");
                pieChartData.get(index).setPieValue(player.getOccupiedCountries().size() * 100.0 / 42);
            }
        }


    }

    /**
     * this method checks the winner 
     * @param p player object
     * @return returns true of p has won
     */
    public boolean checkWinner(Player p)
    {

        return p.getOccupiedCountries().size() == MapModel.getMapModel().getCountries().size();

    }

    /**
     *
     * This method observes the PlayerPhaseModel when a country has been
     * occupied
     *
     */
    private class continentObserver implements Observer 
    {

        @Override
        public void update(Observable arg0, Object arg)
        {
            Country country = (Country) arg;

            if (view.equals("attack"))
            {
                boolean occupy = true;
                for (Country c : country.getContinent().getCountries())
                {
                    if (!c.getRuler().getName().equals(country.getRuler().getName()))
                    {
                        occupy = false;
                        break;
                    }
                }

                if (occupy)
                {
                    country.getContinent().setRuler(country.getRuler());
                    updateContinentDominationView();
                }
            }
        }
    }

    /**
     * This method observes the PlayerPhaseModel when a country has been
     * occupied
     *
     * @author DKM
     *
     */
    private class mapObserver implements Observer 
    {

        @SuppressWarnings("unchecked")
		@Override
        public void update(Observable o, Object arg)
        {
        	if (PlayerPhaseModel.getPlayerModel().getCurrentPlayer().isComputerPlayer())
        	{
        		 mainPane.getChildren().clear();
                 
                 try 
                 {
					mainPane.getChildren().add(FXMLLoader.load(getClass().getResource("/com/risk/view/ComputerView.fxml")));
                 } catch (IOException e) 
                 {
					e.printStackTrace();
                 }
        	}
            Country country = (Country) arg;

            Player player = country.getRuler();
            int index = barPlayersList.indexOf(player);

            if (index == -1)
            {
                barPlayersList.add(player);
                serie.getData().add(new XYChart.Data<String, Integer>(player.getName() + " (" + player.getTotalArmy() + ")", (int) player.getTotalArmy()));
            }

            else
            {
                barPlayersList.set(index, player);
                serie.getData().get(index).setXValue(player.getName() + " (" + player.getTotalArmy() + ")");
                serie.getData().get(index).setYValue((int) player.getTotalArmy());
            }
            if (!view.equals("setup"))
            {
                worldDomination2.getData().setAll(serie);
            }
        }
    }

    /**
     * This method is used to update the domination of continent when the owner
     * of continents changes
     *
     */
    public void updateContinentDominationView()
    {
        worldDomination3.setCellFactory(param -> new ListCell<Continent>() 
        {
            @Override
            protected void updateItem(Continent continent, boolean empty)
            {
                super.updateItem(continent, empty);
                if (empty || continent == null || continent.getRuler() == null)
                {

                    setText(null);

                }
                else
                {
                    setText(continent.getName() + "(" + continent.getRuler().getName() + ")");
                }
            }
        });
    }

    /**
     * This method is data binding for connection between controller and UI.
     *
     * @see javafx.fxml.Initializable
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1)
    {
        PlayerPhaseModel.getPlayerModel().addObserver(new playerObserver());
        PlayerPhaseModel.getPlayerModel().addObserver(new continentObserver());
        MapModel.getMapModel().addObserver(new mapObserver());
        actionMessage.setItems(ActionModel.getActionModel().getActions());
        worldDomination1.setData(pieChartData);

        try
        {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(FXMLLoader.load(getClass().getResource("/com/risk/view/MapSelector.fxml")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    
    /**
     * This method restarts the game
     */	
    public void restartGame()
    {
    	System.out.println("restarting game");
    	GamePhaseModel.getGamePhaseModel().deleteObserver(this);
    	ActionModel.getActionModel().getActions().clear();
        barData.clear();
        data.clear();
        serie.getData().clear();
        pieChartData.clear();
        piePlayersList.clear();
        barPlayersList.clear();
        
         
         MapModel.getMapModel().getContinents().clear();
         MapModel.getMapModel().getCountries().clear();
   
    	 PlayerPhaseModel.getPlayerModel().getPlayers().clear();
    	 
    	
	   	 FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/risk/view/StartUp.fxml"));
	     StartUpController sController = new StartUpController(primaryStage);
	     loader.setController(sController);
	     Parent root = null;
			try 
			{
				root = loader.load();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			
	     Screen screen = Screen.getPrimary();
	     Rectangle2D bounds = screen.getVisualBounds();
	     primaryStage.setX((bounds.getWidth() - primaryStage.getWidth()));
	     primaryStage.setY((bounds.getHeight() - primaryStage.getHeight()));
	     primaryStage.setWidth(bounds.getWidth() / 1.25);
	     primaryStage.setHeight(bounds.getHeight() / 1.5);
	     primaryStage.setScene(new Scene(root, 300, 275));
	     primaryStage.show();
	     

	    }

}
