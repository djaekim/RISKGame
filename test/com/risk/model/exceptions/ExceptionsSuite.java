/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.risk.model.exceptions;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Natt
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
{
    com.risk.model.exceptions.DuplicatesExceptionTest.class, com.risk.model.exceptions.CannotFindExceptionTest.class, com.risk.model.exceptions.CountLimitExceptionTest.class
})
public class ExceptionsSuite {

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }
    
}