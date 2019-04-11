/**
 * Necessary for implement benevolent strategy for benevolent player
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


public class Benevolent implements Strategy 
{
	/**
	 * This method is used to do the attack for benevolent player
	 * The benevolent player doesn't do the attack
	 * 
	 * @param p The player who is going to attack
	 */
	@Override
	public void attack(Player p) 
	{
		ActionModel.getActionModel().addAction("======= Rule =======");
		ActionModel.getActionModel().addAction("skips attack");
		ActionModel.getActionModel().addAction("==================");
	}

	/**
	 * This method is used to do the reinforcement for benevolent player
	 * The benevolent player will reinforce his weakest countries one by one
	 * 
	 * @param p The player who is going to reinforce
	 */
	@Override
	public void reinforce(Player p) 
	{
		
		ActionModel.getActionModel().addAction("======= Rule =======");
		ActionModel.getActionModel().addAction("reinforce weakest");
		ActionModel.getActionModel().addAction("==================");

		ArrayList<Country> weakestCountries = p.getWeakestCountries();
		int Armyinput = p.getReinforcementArmy();

		if (weakestCountries.size() == 1) 
		{
			
			int initialArmy = weakestCountries.get(0).getArmyCount();
			weakestCountries.get(0).setArmyCount(Armyinput);
			ActionModel.getActionModel().addAction("added " + Armyinput + " to " + weakestCountries.get(0).getName() + "(" + initialArmy + ")");
			System.out.println(p.getName()+":added " + Armyinput + " to " + weakestCountries.get(0).getName() + "(" + initialArmy + ")");
			return;
		}
		int army = Armyinput;
		while (army > 0) 
		{
			for (Country c : weakestCountries) 
			{
				int initialArmy = c.getArmyCount();
				c.setArmyCount(1);
				ActionModel.getActionModel().addAction("added " + 1 + " to " + c.getName() + "(" + initialArmy + ")");
				System.out.println(p.getName()+":added " + 1 + " to " + c.getName() + "(" + initialArmy + ")");
				army--;
				if (army == 0) 
				{
					return;
				}
			}
		}
	}
	
	/**
	 * This method is used to do the fortification for benevolent player
	 * The benevolent player will fortify one of his weakest countries
	 * 
	 * @param p The player who is going to fortify
	 */
	
	@Override
	public void fortify(Player p) 
	{
		
		ActionModel.getActionModel().addAction("======= Rule =======");
		ActionModel.getActionModel().addAction("fortify weakest");
		ActionModel.getActionModel().addAction("==================");

		ArrayList<Country> weakestCountries = p.getWeakestCountries();
		
		int Armyinput=0;
		ArrayList<Country> fortify = new ArrayList<Country>();
		System.out.println(p.isAnyCountriesConnected());
		if(p.isAnyCountriesConnected()) 
		{
			for(Country c: weakestCountries) 
			{
				ArrayList<Country> connected = new ArrayList<Country>();
				connected = p.getCountriesArrivedbyPath(c, c, connected);
				if(!connected.isEmpty()) 
				{
					for(Country connect : connected) 
					{
						if(connect.getArmyCount()>1&&connect.getArmyCount()>Armyinput) 
						{
							Armyinput = connect.getArmyCount();
							fortify.clear();
							fortify.add(c);
							fortify.add(connect);
						}
					}
				}
				
			}
			if(fortify.size()==0) 
			{
				ActionModel.getActionModel().addAction("Weakest country doesn't ");
				ActionModel.getActionModel().addAction("  have enough army");
				ActionModel.getActionModel().addAction("  for fortification ");
				return;
			}
			else 
			{
				
				ActionModel.getActionModel().addAction(" move " + (Armyinput-1) + " army from ");
				ActionModel.getActionModel().addAction("       " +  fortify.get(1).getName() + "(" + fortify.get(1).getArmyCount() + ")"+ " to ");
				ActionModel.getActionModel().addAction( "       " +  fortify.get(0).getName() + "(" + fortify.get(0).getArmyCount() + ")");
				System.out.println(p.getName()+": move " + (Armyinput-1) + " army from "+ fortify.get(1).getName() + "(" + fortify.get(1).getArmyCount() + ")"+ " to " + fortify.get(0).getName() + "(" + fortify.get(0).getArmyCount() + ")");
				fortify.get(0).setArmyCount(Armyinput-1);	
				fortify.get(1).reduceArmyCount(Armyinput-1);
			}
		}
	}
	
	/**
	 * This method is used to do the set up for benevolent player
	 * The benevolent player will put his starting armies to his countries one by one until all armies are put
	 * 
	 * @param p The player who is going to setup
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
			ActionModel.getActionModel().addAction("- Benevolent distributes army evenly");
			ActionModel.getActionModel().addAction("==================");
			int army = p.getStartingPoints();
			while (army > 0) 
			{
				for (Country c : p.getOccupiedCountries()) 
				{
					c.setArmyCount(1);
					army--;
					if (army == 0) 
					{
						p.setStartingPoints(0);
						return;
					}
				}
			}	
		}
	}

	/**
	 * Benevolent player does not conquer because it doesn't attack
	 * @param c1 The country where the player moves army from
	 * @param c2 The country where the player moves army to
	 * @param armyInput The number of armies to move
	 */
	@Override
	public void conquer(Country c1, Country c2, int armyInput) 
	{
		// TODO Auto-generated method stub
		
	}
}