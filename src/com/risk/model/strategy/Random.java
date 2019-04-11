/**
 * Necessary for implement random strategy for random player
 * Including the implementation of setup, reinforcement, attack and fortification.
 * 
 * @author Tianyi
 * @author Nat
 * @version 3.0
 */
package com.risk.model.strategy;

import java.util.*;

import com.risk.model.ActionModel;
import com.risk.model.DeckModel;
import com.risk.model.map.Country;
import com.risk.model.player.Player;

public class Random implements Strategy 
{
	/**
	 * This method is used to do the attack for cheater player The cheater player
	 * occupies all his neighbor enemies and put one army to each new occupied
	 * country
	 * 
	 * @param p The player who is going to attack
	 */
	@Override
	public void attack(Player p) 
	{

		ActionModel.getActionModel().addAction("======= Rule =======");
		ActionModel.getActionModel().addAction("attacks random number ");
		ActionModel.getActionModel().addAction(" of times random country");
		ActionModel.getActionModel().addAction("==================");
		ArrayList<Country> countryWithEnoughArmy = new ArrayList<Country>();
		for (Country c : p.getOccupiedCountries()) 
		{
			if (c.getArmyCount() > 1 && (!c.getConnectedEnemyArrayList().isEmpty())) 
			{
				countryWithEnoughArmy.add(c);
			}
		}
		if (!countryWithEnoughArmy.isEmpty()) 
		{
			Country attack = countryWithEnoughArmy.get(getRandomNumber(countryWithEnoughArmy.size()));
			Country defend = attack.getConnectedEnemyArrayList()
					.get(getRandomNumber(attack.getConnectedEnemyArrayList().size()));
			ActionModel.getActionModel().addAction("Selected random attacker: " + attack.getName());
			ActionModel.getActionModel().addAction("Selected random defender: " + defend.getName());
			System.out.println("Random country to attack: " + attack.getName() + " " + attack.getArmyCount());
			System.out.println("Random country to defend: " + defend.getName() + " " + defend.getArmyCount());
			boolean occupy = false;
			boolean continues = (int) (Math.random() * 2 + 1) == 1 ? true : false;
			int time = 0;
			int attacktime = getRandomNumber(500);
			
			do {
				int result[] = p.setRollLimit(attack, defend);
				int randomAttack = getRandomNumber(result[0]) + 1;
				int randomDefend = getRandomNumber(result[1]) + 1;
				int[] dattack = p.rollResult(randomAttack);
				int[] ddefend = p.rollResult(randomDefend);
				int rolltime = Integer.min(randomAttack, randomDefend);
				for (int i = 0; i < rolltime; i++) 
				{
					if (dattack[i] > ddefend[i]) 
					{
						p.attack(attack, defend, 1);
						System.out.println("removed " + 1 + " from " + defend.getName());
						if (defend.getArmyCount() == 0) 
						{
							System.out.println(
									p.getName() + " conquer " + defend.getRuler().getName() + " " + defend.getName());
							defend.getRuler().removeCountry(defend);
							if (defend.getRuler().getOccupiedCountries().size() == 0) 
							{
								defend.getRuler().setPlayerLost(true);
								ActionModel.getActionModel().addAction("defender (" + defend.getRuler() + ") lost");
								System.out.println(defend.getRuler() + " lost");
								p.moveCards(defend.getRuler());
							}
							defend.setRuler(p);
							p.addCountry(defend);
							occupy = true;
							conquer(attack, defend, result[0]);
							break;
						}
					} else 
					{
						p.attack(attack, defend, 3);
						System.out.println("attacker lost 1 army");
						ActionModel.getActionModel().addAction(attack.getName() + " lost 1 army");

					}
				}

			} while (time < attacktime && !occupy && attack.getArmyCount() > 1);

			if(occupy)
			{
				DeckModel.getCardModel().sendCard(p);
			}
		}
	}

	/**
	 * This method is used to do the reinforcement for random player
	 * The random assigns a random number of armies to one of its countries randomly
	 * 
	 * @param p The player who is going to reinforce
	 */
	@Override
	public void reinforce(Player p) 
	{
		ActionModel.getActionModel().addAction("======= Rule =======");
		ActionModel.getActionModel().addAction("fortifies a random ");
		ActionModel.getActionModel().addAction(" 	 country");
		ActionModel.getActionModel().addAction("==================");
		int Armyinput = getRandomNumber(70) + 1;
		if (p.exchangeCardForComputer()) 
		{
			Armyinput += p.calculateReinforcementFromCards();
		}
		Country random = p.getOccupiedCountries().get(getRandomNumber(p.getOccupiedCountries().size()));
		int initialArmy = random.getArmyCount();
		random.setArmyCount(Armyinput);
		ActionModel.getActionModel()
				.addAction("added " + Armyinput + " to " + random.getName() + "(" + initialArmy + ")");
		System.out.println(p.getName() + ": added " + Armyinput + " to " + random.getName() + "(" + initialArmy + ")");
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
	 * This method is used to do the fortification for random player
	 * The random player chooses two countries which are connected to each other and have enough armies
	 * And moves random armies from one to another country
	 * 
	 * @param p The player who is going to fortify
	 */
	@Override
	public void fortify(Player p) 
	{

		ActionModel.getActionModel().addAction("======= Rule =======");
		ActionModel.getActionModel().addAction("Fortifies at random a ");
		ActionModel.getActionModel().addAction(" 	random country");
		ActionModel.getActionModel().addAction("==================");
		ArrayList<Country> countriesHaveConnectedPath = new ArrayList<Country>();
		ArrayList<Country> fortify = new ArrayList<Country>();
		System.out.println(p.isAnyCountriesConnected());
		if (p.isAnyCountriesConnected()) 
		{
			for (Country c : p.getOccupiedCountries()) 
			{
				ArrayList<Country> connected = new ArrayList<Country>();
				connected = p.getCountriesArrivedbyPath(c, c, connected);
				if (!connected.isEmpty() && c.getArmyCount() > 1) 
				{
					countriesHaveConnectedPath.add(c);
				}

			}
			Country randomFrom = countriesHaveConnectedPath.get(getRandomNumber(countriesHaveConnectedPath.size()));
			fortify = p.getCountriesArrivedbyPath(randomFrom, randomFrom, fortify);
			Country randomTo = fortify.get(getRandomNumber(fortify.size()));
			int armyInput = randomFrom.getArmyCount() - 1;
			armyInput = getRandomNumber(armyInput) + 1;
			randomFrom.reduceArmyCount(armyInput);
			randomTo.setArmyCount(armyInput);
			ActionModel.getActionModel().addAction(
					" move " + armyInput + " army from " + randomFrom.getName() + " to " + randomTo.getName());
			System.out.println(p.getName() + ": move " + armyInput + " army from " + randomFrom.getName() + " to "
					+ randomTo.getName());
		} else 
		{
			ActionModel.getActionModel().addAction("No connection, can't reinforce");
		}

	}

	/**
	 * This method is used to do the setup for random player
	 * The random player puts one army to one of its countries randomly each time, until all its armies are put
	 * 
	 * @param p The player who is going to setup
	 */
	@Override
	public void setup(Player p) 
	{
		if (p.getStartingPoints() == 0) 
		{
			return;
		} else 
		{
			ActionModel.getActionModel().addAction("");
			ActionModel.getActionModel().addAction("======= Rule =======");
			ActionModel.getActionModel().addAction("- Random adds army randomly");
			ActionModel.getActionModel().addAction("==================");
			int army = p.getStartingPoints();
			for (int i = 0; i < army; i++) 
			{
				Country random = p.getOccupiedCountries().get(getRandomNumber(p.getOccupiedCountries().size()));
				random.setArmyCount(1);
			}
			p.setStartingPoints(0);
		}
	}

	/**
	 * This method moves army between 2 country during conquer for random
	 * 
	 * @param c1 The country where the player moves army from
	 * @param c2 The country where the player moves army to
	 * @param armyInput The number of armies to move
	 */
	@Override
	public void conquer(Country c1, Country c2, int armyInput) 
	{
		// TODO Auto-generated method stub
		int army = c1.getArmyCount() - 1 -armyInput;
		int armyinput = armyInput + getRandomNumber(army);
		c1.reduceArmyCount(armyinput);
		c2.setArmyCount(armyinput);
		Player p = c1.getRuler();
		ActionModel.getActionModel()
				.addAction(c1.getName() + " conquers " + c2.getName());
		System.out.println(
				p.getName() + ": " + c1.getName() + " conquers " + c2.getName());
	}
}
