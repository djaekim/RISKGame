/**
 * Necessary for implement cheater strategy for cheater player
 * Including the implementation of setup, reinforcement, attack and fortification.
 * 
 * @author Tianyi
 * @author Nat
 * @version 3.0
 */
package com.risk.model.strategy;

import java.util.ArrayList;

import com.risk.model.ActionModel;
import com.risk.model.map.Country;
import com.risk.model.player.Player;

public class Cheater implements Strategy
{
	
	/**
	 * This method is used to do the attack for cheater player
	 * The cheater player occupies all his neighbor enemies and put one army to each new occupied country
	 * 
	 * @param p The player who is going to attack
	 */
	@Override
	public void attack(Player p) 
	{
		
		ActionModel.getActionModel().addAction("======= Rule =======");
		ActionModel.getActionModel().addAction("conquers all neighbouring");
		ActionModel.getActionModel().addAction("    enemies");
		ActionModel.getActionModel().addAction("==================");
		ArrayList<Country> occupy = p.getOccupiedCountries();
		ArrayList<Country> conquer = new ArrayList<Country>();
		for (int i = 0; i< occupy.size(); i++)
        {
			Country c = occupy.get(i);
            for (Country cNeighbors : c.getConnectedEnemyArrayList())
            {
            	cNeighbors.getRuler().removeCountry(cNeighbors);
				if (cNeighbors.getRuler().getOccupiedCountries().size()==0) 
				{
					cNeighbors.getRuler().setPlayerLost(true);
					p.moveCards(cNeighbors.getRuler());
					System.out.println(cNeighbors.getRuler()+" lost");
				}
				cNeighbors.reduceArmyCount(cNeighbors.getArmyCount());
				cNeighbors.setRuler(p);
				conquer.add(cNeighbors);
				conquer(c,cNeighbors,1);
				
				ActionModel.getActionModel().addAction(c.getName()+" conquers "+cNeighbors.getName());
				System.out.println(c.getName()+" conquers "+cNeighbors.getName());
            }
        }
		for(Country c: conquer) 
		{
			p.addCountry(c);
		}
	}
	
	/**
	 * This method is used to do the reinforcement for cheater player
	 * The cheater player gets his armies of countries doubled at reinforcement phase
	 * 
	 * @param p The player who is going to reinforce
	 */
	@Override
	public void reinforce(Player p) 
	{
		
		ActionModel.getActionModel().addAction("======= Rule =======");
		ActionModel.getActionModel().addAction("doubles all army");
		ActionModel.getActionModel().addAction("==================");
		for (Country c : p.getOccupiedCountries())
        {
			int army = c.getArmyCount();
			c.setArmyCount(army);
			ActionModel.getActionModel().addAction("added " + (army) + " to " + c.getName() + "(" + army + ")");
			System.out.println("added " + (army) + " to " + c.getName() + "(" + army + ")");
        }
		
	}
	
	/**
	 * This method is used to do the fortification for cheater player
	 * The cheater player gets his armies of countries which have neighbor enemy doubled at fortification phase
	 * 
	 * @param p The player who is going to fortify
	 */
	@Override
	public void fortify(Player p) 
	{
		
		ActionModel.getActionModel().addAction("======= Rule =======");
		ActionModel.getActionModel().addAction("Doubles army on country");
		ActionModel.getActionModel().addAction(" that have enemies around");
		ActionModel.getActionModel().addAction("==================");
		for (Country c : p.getOccupiedCountries())
        {
			if(!c.getConnectedEnemyArrayList().isEmpty()) 
			{
				int army = c.getArmyCount();
				c.setArmyCount(army);
				ActionModel.getActionModel().addAction("added " + (army) + " to " + c.getName() + "(" + army + ")");
				System.out.println(p.getName()+": added " + (army) + " to " + c.getName() + "(" + army + ")");
			}
        }
	}
	
	/**
	 * This method is used to get a random number within the given limit
	 * 
	 * @param limit The limit of numbers, like the amount of armies
	 * @return The random number 
	 */
	public int getRandomNumber(int limit)
	{
		return (int) (Math.random() * limit);
	}
	
	/**
	 * This method is used to do the set up for cheater player
	 * The cheater player randomly assigns armies to his countries at set up phase
	 * 
	 * @param p The player who is going to set up
	 */
	@Override
	public void setup(Player p) 
	{
		if (p.getStartingPoints() == 0)
		{
			return;
		}
		else
		{
			ActionModel.getActionModel().addAction("");
			ActionModel.getActionModel().addAction("======= Rule =======");
			ActionModel.getActionModel().addAction("- Cheater randomly assigns army");
			ActionModel.getActionModel().addAction("==================");
			int army = p.getStartingPoints();
			for(int i=0; i<army; i++) 
			{
				Country random = p.getOccupiedCountries().get(getRandomNumber(p.getOccupiedCountries().size()));
		        random.setArmyCount(1);
			}
			p.setStartingPoints(0);		
		}
	}

	/**
	 * This method moves army between 2 country during conquer for cheater
	 * @param c1 The country where the player moves army from
	 * @param c2 The country where the player moves army to
	 * @param armyInput The number of armies to move
	 */
	@Override
	public void conquer(Country c1, Country c2, int armyInput) 
	{
		c2.setArmyCount(armyInput);
	}


}
