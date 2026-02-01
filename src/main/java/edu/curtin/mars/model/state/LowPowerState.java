/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: LowPowerState
 * Purpose: Concrete state for when probes are LOW-POWER
 */
package edu.curtin.mars.model.state;

import edu.curtin.mars.model.Probe;

public class LowPowerState implements ProbeState
{
    // When low power and requested to move change to MOVING state towards new destination
    @Override
    public void makeMove(Probe p, double newDestLat, double newDestLon)
    {
        p.setState(Probe.MOVING);
        p.setDestCoords(newDestLat, newDestLon);
    }

    // When low power and requested to measure change state to MEASURING and add the new measurement task
    @Override
    public void makeMeasure(Probe p, String type, int duration)
    {
        p.setState(Probe.MEASURING);
        p.addMeasurementTask(type, duration);
    }

    @Override
    public void simulateSol(Probe p)
    {
    }

    @Override
    public String stateName() {
        return "LOW-POWER";
    }
}