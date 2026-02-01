/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: MoveState
 * Purpose: Concrete state for when probes are MOVING
 */
package edu.curtin.mars.model.state;

import edu.curtin.mars.model.Probe;

public class MoveState implements ProbeState
{
    // When moving and requested to move set new destination coords
    @Override
    public void makeMove(Probe p, double newDestLat, double newDestLon)
    {
        p.setDestCoords(newDestLat, newDestLon);
    }

    // When moving and requested to measure change to MEASURING state, add the measurement task
    // and set the destination coords to null
    @Override
    public void makeMeasure(Probe p, String type, int duration)
    {
        p.setState(Probe.MEASURING);
        p.addMeasurementTask(type, duration);
        p.setDestCoords(null, null);
    }

    // Move towards destination when sol simulated
    @Override
    public void simulateSol(Probe p)
    {
        p.moveTowardsDest();
    }

    @Override
    public String stateName()
    {
        return "DRIVING";
    }
}