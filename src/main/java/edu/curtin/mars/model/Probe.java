/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: Probe
 * Purpose: Model class for each probe, context of state pattern
 */
package edu.curtin.mars.model;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import edu.curtin.mars.model.state.ProbeState;
import edu.curtin.mars.model.state.LowPowerState;
import edu.curtin.mars.model.state.MeasureState;
import edu.curtin.mars.model.state.MoveState;
import edu.curtin.mars.observer.EventObserver;

public abstract class Probe 
{
    private static final Logger logger = 
        Logger.getLogger(Probe.class.getName());
        
    private String id;
    private double lat;
    private double lon;
    private Double destLat;
    private Double destLon;
    private boolean hasMeasurementsLeft = false;

    // Map of measurement type and the amount of recordings left to do
    private Map<String, Integer> measurementTasks = new HashMap<>();
    // Map of measurement type and the current recording
    private Map<String, Double> measurementRecordings = new HashMap<>();
    
    private Random random = new Random();

    // Create shared state objects for all the probes
    public static final ProbeState LOW_POWER = new LowPowerState();
    public static final ProbeState MOVING = new MoveState();
    public static final ProbeState MEASURING = new MeasureState();

    // Set the starting state
    private ProbeState state = LOW_POWER;

    // Constructor
    public Probe(String id, double lat, double lon) 
    {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        initMeasurementTasks();
        initMeasurementRecordings();
    }

    // Getters

    public String getId() 
    {
        return id;
    }

    public double getLat() 
    {
        return lat;
    }

    public double getLon() 
    {
        return lon;
    }

    public Map<String, Integer> getMeasurementTasks()
    {
        return measurementTasks;
    }

    public double getDestLat() 
    {
        return destLat;
    }

    public double getDestLon() 
    {
        return destLon;
    }

    public ProbeState getState() 
    {
        return state;
    }

    public Map<String, Double> getMeasurementRecordings() 
    {
        return measurementRecordings;
    }

    public boolean getHasMeasurementsLeft() 
    {
        return hasMeasurementsLeft;
    }

    // Setters

    public void setCurCoords(Double latitude, Double longitude)
    {
        this.lat = latitude;
        this.lon = longitude;
    }
    
    public void setDestCoords(Double latitude, Double longitude)
    {
        this.destLat = latitude;
        this.destLon = longitude;
    }

    public void setHasMeasurementsLeft(boolean bool) 
    {
        this.hasMeasurementsLeft = bool;
    }

    // Initialize maps

    private void initMeasurementTasks()
    {
        measurementTasks.put("temperature", 0);
        measurementTasks.put("pressure", 0);
        measurementTasks.put("wind-speed", 0);
        measurementTasks.put("visibility", 0);
        measurementTasks.put("dust-concentration", 0);
        measurementTasks.put("magnetic-flux", 0);
    }

    private void initMeasurementRecordings()
    {
        measurementRecordings.put("temperature", null);
        measurementRecordings.put("pressure", null);
        measurementRecordings.put("wind-speed", null);
        measurementRecordings.put("visibility", null);
        measurementRecordings.put("dust-concentration", null);
        measurementRecordings.put("magnetic-flux", null);
    }

    // Abstract methods for Drone and Rover

    public abstract double getMaxSolDistance();

    public abstract String getProbeType();

    // State functions

    public void setState(ProbeState s)
    {
        this.state = s;
    }

    // Handle movement commands
    public void makeMove(double newDestLat, double newDestLon)
    {
        state.makeMove(this, newDestLat, newDestLon);
        logger.fine(() -> "Probe: " + id + " recieved move command");
    }

    // Handle measurement commands
    public void makeMeasure(String type, int duration)
    {
        state.makeMeasure(this, type, duration);
        logger.fine(() -> "Probe: " + id + " recieved measure command");
    }

    // Handle simulation sol progress
    public void simulateSol()
    {
        state.simulateSol(this);
    }

    public void addMeasurementTask(String type, int duration)
    {
        if (!measurementTasks.containsKey(type)) 
        {
            throw new IllegalArgumentException("Measurement type: " + type + " does not exist.");
        }

        int newDuration = measurementTasks.get(type) + duration;
        measurementTasks.put(type, newDuration);
        hasMeasurementsLeft = true;
        logger.fine(() -> "Probe: " + id + " added new task: " + type);
    }

    // Clear both Measurement Lists
    public void clearMeasurementList() 
    {
        clearMeasurements();
        for (String type : measurementTasks.keySet()) 
        {
            measurementTasks.put(type, 0);
        }
        hasMeasurementsLeft = false;
        logger.fine(() -> "Probe: " + id + " cleared its measurements task and records");
    }

    // Clear the measurement recordings list
    public void clearMeasurements() 
    {
        for (String type : measurementRecordings.keySet()) 
        {
            measurementRecordings.put(type, null);
            logger.fine(() -> "Probe: " + id + " cleared its measurement records");

        }
    }

    // Reduce measurements by one (after sol progresses)
    public void decrementMeasurements() 
    {
        boolean noMeasurements = true;
        for (String type : measurementTasks.keySet()) 
        {
            // Decrement all the measurement tasks that have remaining time
            if (measurementTasks.get(type) > 0 ) 
            {
                int newDuration = measurementTasks.get(type) - 1;
                measurementTasks.put(type, newDuration);
                noMeasurements = false;
            }
        }

        if (noMeasurements) 
        {
            hasMeasurementsLeft = false;
            clearMeasurements();
            setState(LOW_POWER);
            logger.fine(() -> "Probe: " + id + " has no measurement tasks remaining");
        }

        logger.fine(() -> "Probe: " + id + " decremented its measurement tasks");
    }

    // Take random recordings for each measurement task
    public void takeMeasurements() 
    {
        for (String type : measurementRecordings.keySet()) 
        {
            if(measurementTasks.get(type) > 0) 
            {
                double randomMeasure = random.nextDouble();
                measurementRecordings.put(type, randomMeasure);
                logger.fine(() -> "Probe: " + id + " took a measurement of " + type);
            }
        }
    }

    // Move towards destination - Change to low power on arrival
    public void moveTowardsDest() 
    {
        if (destLat == null || destLon == null )
        {
            throw new IllegalStateException("Probe: " + id + "making move with no destination");
        }

        // Linear algebra for movement at correct angle towards destination
        Double latDifference = destLat - lat;
        Double lonDifference = destLon - lon;
        Double distance = Math.hypot(latDifference, lonDifference);
        Double ratio = Math.min(distance, getMaxSolDistance()) / distance;
        lat += latDifference * ratio;
        lon += lonDifference * ratio;

        logger.fine(() -> "Probe: " + id + " moved towards destination");

        // Change to low power if arrived at destination
        if (Math.abs(destLat - lat) < 0.000001 && Math.abs(destLon - lon) < 0.000001) 
        {
            setState(LOW_POWER);
            destLat = null;
            destLon = null;
            logger.info(() -> "Probe: " + id + " arrived destination");
        }
    }
}
