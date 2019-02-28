/**
 * Necessary to create a player
 *
 * @author Natheepan
 *
 *
 */
package com.risk.model.player;

import com.risk.model.card.Card;
import com.risk.model.map.Country;
import java.util.ArrayList;

/**
 *
 * @author Natheepan
 */
public class Player {

    private String name;
    private ArrayList<Country> occupiedCountries = new ArrayList<>();
    private ArrayList<Card> cards = new ArrayList<>();
    private int startingPoints;
    private int reinforcement = 0;

    private boolean playerLost = false;

    /**
     * Constructor for Player class
     *
     * @param name the name of the player in string
     */
    public Player(String name)
    {
        this.name = name;
    }

    /**
     * Sets the number of armies needed to start the game for a player
     *
     * @param i the count of the armies you need to start the game
     */
    public void setStartingPoints(int i)
    {
        startingPoints = i;
    }

    /**
     * Calculates the initial reinforcement at the start of reinforcement phase.
     * Different from getAvailableReinforcement(), which returns the remaining
     * armies after assigning.
     *
     * @return availableReinforcement number of armies
     */
    public int getReinforcementOccupiedTerritory()
    {
        return (int) Math.floor(numbOccupied() / 3);
    }

    public int getReinforcementContinentControl()
    {
        return 0;
    }

    public int getReinforcementTradeInCard()
    {
        return 0;
    }

    public int calculateReinforcement()
    {
        int numbArmies = getReinforcementOccupiedTerritory() + getReinforcementContinentControl() + getReinforcementTradeInCard();
        if (numbArmies < 3)
        {
            reinforcement = 3;
            return reinforcement;
        }
        reinforcement = numbArmies;

        return reinforcement;
    }

    /**
     * Gets the number of armies remaining
     *
     * @return availableReinforement number of armies
     */
    public int getReinforcement()
    {
        return reinforcement;
    }

    /**
     * Sets the number of armies during reinforcement.
     *
     * @param assign number of armies to assign
     * @return returns 1 if you successfully assigned, -1 otherwise
     */
    public boolean setReinforcement(int assign)
    {
        if (assign > getReinforcement())
        {
            return false;
        }
        reinforcement -= assign;
        return true;

    }

    /**
     * gets the name of player
     *
     * @return name name of player
     */
    public String getName()
    {
        return name;
    }

    /**
     * gets the country occupied by player in arrayList
     *
     * @return occupiedCountries returns ArrayList<Country> occupiedCountries
     */
    public ArrayList<Country> getOccupiedCountries()
    {
        return occupiedCountries;
    }

    /**
     * gets the size of occupied country
     *
     * @return returns length of the ArrayList<Country> occupiedCountries
     */
    public int numbOccupied()
    {
        return occupiedCountries.size();
    }

    /**
     * gets country object associated with the name of the string
     *
     * @param name name of the country
     * @return country returns country object if it exists, otherwise null
     */
    public void setOccupiedCountries(ArrayList<Country> occupiedCountries)
    {
        this.occupiedCountries = occupiedCountries;
    }

    /**
     * gets the card associated with the player
     *
     * @return cards returns ArrayList<Card> cards
     */
    public Country getCountry(String name)
    {
        for (Country country : occupiedCountries)
        {
            if (country.getName().equalsIgnoreCase(name))
            {
                return country;
            }
        }
        return null;
    }

    /**
     * Gets the starting number of armies when the game first starts
     *
     * @return startingPoints number of armies
     */
    public ArrayList<Card> getCards()
    {
        return cards;
    }

    /**
     * TODO
     *
     * @param cards
     */
    public void setCards(ArrayList<Card> cards)
    {
        this.cards = cards;
    }

    /**
     * gets the value representing the lost status of player.
     *
     * @return playerLost returns true if player has lost, false otherwise
     */
    public boolean isPlayerLost()
    {
        return playerLost;
    }

    /**
     * sets the lost status of player
     *
     * @param playerLost the boolean value representing what playerLost should
     * be set by
     */
    public void setPlayerLost(boolean playerLost)
    {
        this.playerLost = playerLost;
    }

    /**
     * Used to calculate armies remaining during setup phase when you assign
     * initial army counts
     *
     * @return The remaining army after assigning army from startingPoint
     */
    public int armiesLeft()
    {
        int sum = 0;
        for (Country country : this.occupiedCountries)
        {
            sum += country.getArmyCount();
        }

        return startingPoints - sum;
    }
}
