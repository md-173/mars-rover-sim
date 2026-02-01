/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: Drone
 * Purpose: Rover sub class of probe, handles move speed and naming
 */
package edu.curtin.mars.model;

public class Rover extends Probe 
{
    private static final double MAX_SOL_DISTANCE = 0.004;

    public Rover(String id, double lat, double lon) 
    {
        super(id, lat, lon);
    }

    @Override
    public double getMaxSolDistance() 
    {
        return MAX_SOL_DISTANCE;
    }

    @Override
    public String getProbeType() 
    { 
        return "ROVER";
    }
}