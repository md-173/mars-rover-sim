/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: ProbeState
 * Purpose: State interface for probes
 */
package edu.curtin.mars.model.state;

import edu.curtin.mars.model.Probe;

public interface ProbeState
{
    void makeMove(Probe p, double newDestLat, double newDestLon);

    void makeMeasure(Probe p, String type, int duration);

    void simulateSol(Probe p);

    String stateName();
}