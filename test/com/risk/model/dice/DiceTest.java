/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.risk.model.dice;

import com.risk.model.map.Country;
import com.risk.model.player.Player;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Natt
 * @author Tianyi
 */
public class DiceTest {
	private Country country1;
	private Country country2;
	private Player p1;
	private Player p2;
    
    public DiceTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    	p1 = new Player("Green");
    	p2 = new Player("Red");
    	country1 = new Country("China");
    	country2 = new Country("Siam");
    	p1.addCountry(country1);
    	p2.addCountry(country2);
    	country1.setRuler(p1);
    	country2.setRuler(p2);
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of roll method, of class Dice.
     */
    @Test
    public void testRoll()
    {
        int result = Dice.roll();
        assertTrue(0<result&&result<=6);
    }

    /**
     * Test of setRollLimt method, of class Dice.
     */
    @Test
    public void testSetRollLimt()
    {
        country1.setArmyCount(5);
        country2.setArmyCount(1);
        int[] expResult = new int[2];
        expResult[0]=3;
        expResult[1]=1;
        int[] result = Dice.setRollLimt(country1, country2);
        assertArrayEquals(expResult, result);
    }
    
    /**
     * Test of setRollLimt method, of class Dice.
     */
    @Test
    public void testSetRollLimt2()
    {
        country1.setArmyCount(3);
        country2.setArmyCount(1);
        int[] expResult = new int[2];
        expResult[0]=2;
        expResult[1]=1;
        int[] result = Dice.setRollLimt(country1, country2);
        assertArrayEquals(expResult, result);
    }
    
    /**
     * Test of setRollLimt method, of class Dice.
     */
    @Test
    public void testSetRollLimt3()
    {
        country1.setArmyCount(5);
        country2.setArmyCount(3);
        int[] expResult = new int[2];
        expResult[0]=3;
        expResult[1]=2;
        int[] result = Dice.setRollLimt(country1, country2);
        assertArrayEquals(expResult, result);
    }
    
}