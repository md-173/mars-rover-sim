/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: InvalidProbeException
 * Purpose: Exception type for when a non existant probe type is passed to Probe Factory
 */
package edu.curtin.mars.model.factory;

public class InvalidProbeException extends Exception
{
    public InvalidProbeException(String msg)
    {
        super(msg);
    }
}