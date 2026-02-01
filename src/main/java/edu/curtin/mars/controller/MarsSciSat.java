/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: MarsSciSat
 * Purpose: Subject of obsever pattern that links earth operations to the mars probes
 */
package edu.curtin.mars.controller;

import java.util.*;
import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import edu.curtin.mars.model.Probe;
import edu.curtin.mars.model.factory.ProbeFactory;
import edu.curtin.mars.observer.EventObserver;
import edu.curtin.mars.observer.HistoryLogger;
import edu.curtin.mars.observer.StatusLogger;
import edu.curtin.mars.model.factory.InvalidProbeException;


public class MarsSciSat
{
    private static final Logger logger = Logger.getLogger(MarsSciSat.class.getName());
    // Map of all probes
    private Map<String, Probe> probes = new LinkedHashMap<>();
    private List<EventObserver> observers = new ArrayList<>();
    private int curSol = 1;
    private ProbeFactory pFactory;
    private StatusLogger statusLog;
    private HistoryLogger historyLog;

    public MarsSciSat(ProbeFactory factory, StatusLogger statusLogger, HistoryLogger historyLogger)
    {
        this.pFactory = factory;
        this.statusLog = statusLogger;
        this.historyLog = historyLogger;
        observers.add(historyLog);
        observers.add(statusLog);
        logger.info("MarsSciSat constructed");
    }

    public Map<String, Probe> getProbes() 
    {
        return probes;
    }

    public void run() 
    {
        // Simulate a passing sol for each probe
        for (Probe p : probes.values())
        {
            p.simulateSol();
        }
        logger.info(() -> "Sol number " + curSol + " simulated succesfully");
        
        // Notify the history and status observers of any changes during sol
        notifyObservers();
       
        curSol++;
    }

    // Parse the messages from CommsGenerator 
    public void parseMsg(String msg) 
    {
        logger.info(() -> "Message recieved from earth: " + msg);
        String[] splitMsg = msg.split(" ");
        
        // Handle incorrect length of communication msg
        if (splitMsg.length < 2 || splitMsg.length > 4)
        {
            System.out.println("TO EARTH: MESSAGE ERROR: Command length is incorrect. Msg = " + msg);
            logger.warning(() -> "Communication msg is to short or long: " + msg);
        }
        else
        {
            String probeId = splitMsg[0].toUpperCase();
            String[] splitId = probeId.split("-");
            String probeType = splitId[0];
            String commandType = splitMsg[1];
            Probe p;

            // Handle the probe placement commands
            if(commandType.equals("at"))
            {
                parseAtCommand(splitMsg, probeType, probeId, msg);
            }
            else
            {
                p = probes.get(probeId);
                // Handle incorrect probe names being communicated
                if (p == null) 
                {
                    System.out.println("TO EARTH: MESSAGE ERROR: Probe " + probeId + 
                    " does not exist");
                    logger.warning(() -> "Communication msg has non-existant probe: " + msg);
                }
                else
                {
                    switch(commandType) 
                    {
                        // Handle Move commands
                        case "move":
                            parseMoveCommand(splitMsg, probeId, msg, p);
                            break;
                        // Handle Measure commands
                        case "measure":
                            parseMeasureCommand(splitMsg, probeId, msg, p);
                            break;
                        case "status": 
                            // Handle status commands of the wrong length
                            if(splitMsg.length == 2)
                            {
                                statusLog.printProbeStatus(p);
                                logger.info(() -> "Status command sent to " + probeId);
                            }
                            else
                            {
                                System.out.println("TO EARTH: MESSAGE ERROR: Status command of incorrect length");
                                logger.warning(() -> "Status communication msg has invalid number of parameters: " + msg);
                            }
                            break;
                        case "history":
                            // Handle status commands of the wrong length
                            if(splitMsg.length == 2)
                            {
                                historyLog.printProbeHistory(p);
                                logger.info(() -> "History command sent to " + probeId);
                            }
                            else
                            {
                                System.out.println("TO EARTH: MESSAGE ERROR: History command of incorrect length");
                                logger.warning(() -> "History communication msg has invalid number of parameters: " + msg);
                            }
                            break;
                        default:
                            System.out.println("TO EARTH: MESSAGE ERROR: Command for " + probeId +
                                " does not exist. Msg = " + msg);
                            logger.warning(() -> "Communication msg command does not exist: " + msg);
                    }
                }
            }
        }
    }

    public void addObserver(EventObserver observer) 
    {
        observers.add(observer);
    }

    public void removeObserver(EventObserver observer) 
    {
        observers.remove(observer);
    }
    
    // Polymorphic updating of the observers data
    public void notifyObservers() 
    {
        for (EventObserver observer : observers) 
        {
            observer.update(probes, curSol);
            logger.fine(() -> "Observer: " + observer.getClass().getSimpleName() + " notified of sol: " +
                curSol + " activities");
        }
    }

    public void parseAtCommand(String[] splitMsg, String probeType, String probeId, String msg)
    {
        // Catch non double and incorrect probe type creation
        try
        {
            double lat = Double.parseDouble(splitMsg[2]);
            double lon = Double.parseDouble(splitMsg[3]);
            probes.put(probeId, pFactory.createProbe(probeType, probeId, lat, lon));
            logger.fine(() -> "Probe: " + probeId + " stored in satellite list. Probe Type: " + probeType + 
                ". Created at coords: " + lat + ", " + lon);
        }
        catch (NumberFormatException e)
        {
            System.out.println("TO EARTH: MESSAGE ERROR: Coordinates for probe " + probeId + 
                " placement are incorrect");
            logger.warning(() -> "Create probe communication msg has invalid coordinates: " + msg);
        }
        catch(InvalidProbeException e)
        {
            System.out.println("TO EARTH: MESSAGE ERROR: " + e.getMessage());
            logger.warning(() -> "Create probe communication msg has invalid probe type: " + msg);
        }
    }

    public void parseMoveCommand(String[] splitMsg, String probeId, String msg, Probe p)
    {
        // Show error on incorrect length
        if(splitMsg.length == 4)
        { 
            // Catch non double move destination coords
            try
            {
                double destLat = Double.parseDouble(splitMsg[2]);
                double destLon = Double.parseDouble(splitMsg[3]);
                p.makeMove(destLat, destLon);
                logger.info(() -> "Move command sent to " + probeId + ". Towards: " + 
                    destLat + ", " + destLon);
            }
            catch (NumberFormatException e)
            {
                System.out.println("TO EARTH: MESSAGE ERROR: Command for " + probeId +
                    " has incorrect coordinates: " + msg);
                logger.warning(() -> "Movement communication msg has incorrect coordinates: " + msg);
            }
        }
        else
        {
            System.out.println("TO EARTH: MESSAGER ERROR: Move command of incorrect length");
            logger.warning(() -> "Movement communication msg has incorrect number of parameters: " + msg);
        }
    }

    public void parseMeasureCommand(String[] splitMsg, String probeId, String msg, Probe p)
    {
        // Show error for incorrect length
        if(splitMsg.length == 4)
        {
            // Catch non integer Measurement commands
            try
            {
                String measurementType = splitMsg[2];
                if (isValidMeasurement(measurementType))
                {
                    int duration = Integer.parseInt(splitMsg[3]);
                    p.makeMeasure(measurementType, duration);
                    logger.info(() -> "Measure command sent to " + probeId + ". Measurement: " + 
                        measurementType + ". Duration: " + duration);
                }
                else
                {
                    System.out.println("TO EARTH: MESSAGE ERROR: Measurement type does not exist: " + measurementType);
                    logger.warning(() -> "Communication msg has invalid measurement type: " + msg);
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println("TO EARTH: MESSAGE ERROR: Command for " + probeId +
                    " has incorrect format for measurement length: " + msg);
                logger.warning(() -> "Measurement communication msg has invalid measurement duration: " + msg);
            }
        }
        else
        {
            System.out.println("TO EARTH: MESSAGER ERROR: Measure command of incorrect length");
            logger.warning(() -> "Measurement communication msg has invalid number of parameters: " + msg);
        }
    }

    private boolean isValidMeasurement(String m)
    {
        return m.equals("temperature") || m.equals("pressure") || m.equals("wind-speed") || m.equals("visibility") ||
            m.equals("dust-concentration") || m.equals("magnetic-flux");
    }
}