/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: ProbeFactory
 * Purpose: Factory that creates drones or rovers, allowing for mock factory in tests
 */
package edu.curtin.mars.model.factory;

import java.util.logging.Logger;
import java.util.logging.Level;
import edu.curtin.mars.model.Probe;
import edu.curtin.mars.model.Drone;
import edu.curtin.mars.model.Rover;
import edu.curtin.mars.model.factory.InvalidProbeException;

public class ProbeFactory
{
    private static final Logger logger = 
        Logger.getLogger(ProbeFactory.class.getName());

    public Probe createProbe(String type, String id, double lat, double lon) throws InvalidProbeException
    {
        Probe probe = null;
        switch(type) 
        {
            case "DRONE":
                probe = new Drone(id, lat ,lon);
                logger.fine(() -> "New drone created in factory: " + id);
                break;
            case "ROVER":
                probe = new Rover(id, lat, lon);
                logger.fine(() -> "New rover created in factory: " + id);
                break;
            // Handle wrong format of drone type being created
            default:
                throw new InvalidProbeException("Probe type doesn't exist: " + type);
        }
        return probe;
    }
}