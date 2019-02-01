/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.risk.run;

import com.risk.army.Player;
import com.risk.exceptions.CannotFindException;
import com.risk.exceptions.CountLimitException;
import com.risk.exceptions.DuplicatesException;
import com.risk.map.Continent;
import com.risk.map.Country;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Natheepan
 */
public class Run {

    public static void main(String[] args)
    {
        ArrayList<Continent> continents = new ArrayList<>();
        ArrayList<Country> countries = new ArrayList<>();
        try
        {
            Scanner input = new Scanner(new File("src/com/risk/run/inputtext/input.txt"));
            while (input.hasNextLine())
            {
                String text = input.nextLine();
                if (text.equalsIgnoreCase("SET NEIGHBORS"))
                {
                    while (input.hasNextLine())
                    {
                        text = input.nextLine();
                        String nameOfCountry1 = text.substring(0, text.indexOf(","));
                        String nameOfCountry2 = text.substring(text.indexOf(",") + 1, text.length());

                        for (Country c : countries)
                        {
                            if (c.getName().equalsIgnoreCase(nameOfCountry1))
                            {
                                for (Country c2 : countries)
                                {
                                    if (c2.getName().equalsIgnoreCase(nameOfCountry2))
                                    {
                                        c.getConnectedCountries().add(c2);
                                        c2.getConnectedCountries().add(c);
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    String nameOfContinent = text.substring(0, text.indexOf(","));
                    String nameOfCountry = text.substring(text.indexOf(",") + 1, text.length());
//                    System.out.println(nameOfContinent + " " + nameOfCountry);
                    boolean countryExists = false;
                    for (Country country : countries)
                    {
                        if (country.getName().equalsIgnoreCase(nameOfCountry))
                        {
                            countryExists = true;
                        }
                    }
                    if (!countryExists)
                    {
                        boolean continentExists = false;

                        Country c = new Country(nameOfCountry, nameOfContinent);
                        countries.add(c);

                        for (Continent cont : continents)
                        {
                            if (cont.getName().equalsIgnoreCase(nameOfContinent))
                            {
                                continentExists = true;
                                cont.getCountries().add(c);
                            }
                        }

                        if (!continentExists)
                        {
                            continents.add(new Continent(nameOfContinent, 10));
                            continents.get(continents.size() - 1).getCountries().add(c);
                        }
                    }
                    else
                    {
                        DuplicatesException ex1 = new DuplicatesException("Country: " + nameOfCountry);
                        throw ex1;
                    }
                }
            }
            for (Continent cont : continents)
            {
                int count = cont.getCountries().size();
                int maxCount = 0;

                switch (cont.getName())
                {
                    case "North America":
                        maxCount = Continent.MAX_NUMBER_OF_COUNTRIES_IN_NORTH_AMERICA;
                        break;
                    case "South America":
                        maxCount = Continent.MAX_NUMBER_OF_COUNTRIES_IN_SOUTH_AMERICA;
                        break;
                    case "Europe":
                        maxCount = Continent.MAX_NUMBER_OF_COUNTRIES_IN_EUROPE;
                        break;
                    case "Asia":
                        maxCount = Continent.MAX_NUMBER_OF_COUNTRIES_IN_ASIA;
                        break;
                    case "Africa":
                        maxCount = Continent.MAX_NUMBER_OF_COUNTRIES_IN_AFRICA;
                        break;
                    case "Australia":
                        maxCount = Continent.MAX_NUMBER_OF_COUNTRIES_IN_AUSTRALIA;
                        break;
                    default:
                        CannotFindException ex2 = new CannotFindException(cont.getName() + " is not predefined. Size of continent is not known. Please resolve this issue.");
                        throw ex2;
                }

                if (maxCount != count)
                {
                    CountLimitException ex3 = new CountLimitException(cont.getName(), count, maxCount);
                    throw ex3;
                }
            }

            // FOR DEBUGGING PURPOSE -------------------------------------------------------------------------------------
//            int y = 0;
//            for (Continent cont : continents)
//            {
//                for (Country country : cont.getCountries())
//                {
//                    for (Country c : country.getConnectedCountries())
//                    {
//                        y++;
//                        System.out.printf("%-3s For Continent %-13s %s%-21s is neighbored by: %s%n", y, cont.getName(), "The country: ", country.getName(), c.getName());
//                    }
//                }
//            }
            //-------------------------------------------------------------------------------------------------------------
            ArrayList<Player> players = new ArrayList<>();
            players.add(new Player("Red"));
            players.add(new Player("Blue"));
            players.add(new Player("Green"));
            players.add(new Player("Yellow"));
            players.add(new Player("Orange"));
            players.add(new Player("Purple"));
            
            Player.setStartingPoints(players.size());
            
            boolean[] countryOccupied = new boolean[Country.MAX_NUMBER_OF_COUNTRIES];
            int i = 0;
            while (i < Country.MAX_NUMBER_OF_COUNTRIES)
            {
                for (Player p : players)
                {
                    int random = (int) (Math.random() * Country.MAX_NUMBER_OF_COUNTRIES);
                    while (countryOccupied[random])
                    {
                        random = (int) (Math.random() * Country.MAX_NUMBER_OF_COUNTRIES);
                    }
                    if (!countryOccupied[random])
                    {
                        countries.get(random).setRuler(p);
                        countries.get(random).setIsOccupied(true);
                        i++;
                        countries.get(random).setArmyCount(1);

                        countryOccupied[random] = true;
                        p.getOccupiedCountries().add(countries.get(random));
                    }
                    if(i>= Country.MAX_NUMBER_OF_COUNTRIES) break;
                }
            }
            
            boolean[] armiesRemaining = new boolean[players.size()];
            boolean done = false;
            while (!done)
            {
                for (int ii = 0; ii < players.size(); ii++)
                {
                    if(players.get(ii).playersLeft()>0){
                    int random = (int) (Math.random() * players.get(ii).getOccupiedCountries().size());
                    players.get(ii).getOccupiedCountries().get(random).setArmyCount(1);}
                    else armiesRemaining[ii] = true;
                }
                int countP = 0;
                for(boolean d: armiesRemaining)
                {
                    if(d) countP++;
                }
                if(countP==players.size()) done = true;
            }
            // FOR DEBUGGING PURPOSE -------------------------------------------------------------------------------------

            int totalArmy = 0;
            for (Player p : players)
            {
                int s = 0;
                for (Country t : p.getOccupiedCountries())
                {
                    totalArmy += t.getArmyCount();
                    s += t.getArmyCount();
                }
                System.out.println(p.getName() + " has " + s);
            }
            int z = 0;
            for (Player p : players)
            {
                System.out.println(p.getName() + " " + p.getOccupiedCountries().size());
            }
            for (Player p : players)
            {
                System.out.printf("%-3s The Player %-6s %-42s", z, p.getName(), " has occupied the following countries: ");
                for (Country country : p.getOccupiedCountries())
                {
                    z++;
                    System.out.print(country.getName() + " , ");
                }
                System.out.println();
            }
            //-------------------------------------------------------------------------------------------------------------
            // FOR DEBUGGING PURPOSE -------------------------------------------------------------------------------------
            int q = 0;
            for (Continent cont : continents)
            {
                for (Country country : cont.getCountries())
                {
                    q++;
                    System.out.printf("%-3s For Continent %-13s %s%-21s is occupied by: %s has an army of %d%n", q, cont.getName(), "The country: ", country.getName(), country.getRuler().getName(), country.getArmyCount());
                }
            }
            //-------------------------------------------------------------------------------------------------------------

        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (DuplicatesException ex)
        {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (CountLimitException ex)
        {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (CannotFindException ex)
        {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
