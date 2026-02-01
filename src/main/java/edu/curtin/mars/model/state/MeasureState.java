/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: MeasureState
 * Purpose: Concrete state for when probes are MEASURING
 */
package edu.curtin.mars.model.state;

import edu.curtin.mars.model.Probe;

public class MeasureState implements ProbeState
{
    // When measuring and requested to move clear measurements and change to MOVING state towards new destination
    @Override
    public void makeMove(Probe p, double newDestLat, double newDestLon)
    {
        p.clearMeasurementList();
        p.setState(Probe.MOVING);
        p.setDestCoords(newDestLat, newDestLon);
    }

    // When measuring and requested to measure add the new measurement type when already measuring
    @Override
    public void makeMeasure(Probe p, String type, int duration)
    {
        p.addMeasurementTask(type, duration);
    }

    // On simulate take the measurements and decrement the list of measurements needed
    @Override
    public void simulateSol(Probe p)
    {
        p.takeMeasurements();
        p.decrementMeasurements();
    }

    @Override
    public String stateName() {
        return "MEASURING";
    }
}
