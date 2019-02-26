/**
 *
 */
package com.risk.utilities;

import com.risk.exceptions.CannotFindException;
import com.risk.exceptions.CountLimitException;
import com.risk.map.Continent;
import com.risk.map.Country;
import com.risk.model.MapModel;
import java.util.ArrayList;

/**
 * @author DKM
 *
 */
public class Validate {

    MapModel map;
    static int counter = 0;
    static ArrayList<Country> countriesModelValidationList = new ArrayList<>();

    public Validate(MapModel m)
    {
        map = m;
    }

    public void continentChecks() throws CannotFindException, CountLimitException
    {
        for (Continent cont : map.getContinents())
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
                    CannotFindException ex2 = new CannotFindException(cont.getName()
                            + " is not predefined. Size of continent is not known. Please resolve this issue.");
                    throw ex2;
            }

            if (maxCount != count)
            {
                CountLimitException ex3 = new CountLimitException(cont.getName(), count, maxCount);
                throw ex3;
            }
        }
    }
    
    /**
     * Given a Country, this method recursively goes thru this country's neighbours and adds then to countriesModelValidationList
     * If (countriesModelValidationList.size() == map.getCountries().size()) then from a given country, 
     * we can travel to all other countries, so map is connected
     */
    public void mapConnected(Country origin)
    {
        // if ever this method enters an infinite recussion
        // All precautions have been taken, but just incase
        if (counter == 1000)
        {
            return;
        }
        counter++;
        
        //if all countries have been added
        if (countriesModelValidationList.size() == map.getCountries().size())
        {
            return;
        }
        
        //if country is already checked before
        for (Country a : countriesModelValidationList)
        {
            if (a.getName() == origin.getName())
            {
                return;
            }
        }
        countriesModelValidationList.add(origin);

        //Recursively call next connected country
        for (Country a : origin.getConnectedCountries())
        {
            mapConnected(a);
        }
    }
}
