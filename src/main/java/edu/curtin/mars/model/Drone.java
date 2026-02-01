/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: Drone
 * Purpose: Drone sub class of probe, handles move speed and naming
 */
package edu.curtin.mars.model;

public class Drone extends Probe 
{
    private static final double MAX_SOL_DISTANCE = 0.018;

    public Drone(String id, double lat, double lon) 
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
        return "DRONE";
    }
}