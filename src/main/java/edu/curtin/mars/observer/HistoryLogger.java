/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: HistoryLogger
 * Purpose: Observerer that keeps track of every probes history of activities, allowing
 * for history printing.
 */
package edu.curtin.mars.observer;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import edu.curtin.mars.observer.EventObserver;
import edu.curtin.mars.model.Probe;

public class HistoryLogger implements EventObserver 
{
    private static final Logger logger = 
        Logger.getLogger(HistoryLogger.class.getName());

    // Map of each probe to its history list
    private Map<String, List<String>> history = new HashMap<>();

    // Update each probs history when observers are notified after a sol completes
    @Override
    public void update(Map<String, Probe> probes, int curSol) 
    {
        for (Probe p : probes.values()) 
        {
            String pState = p.getState().stateName();
            if (!pState.equals("LOW-POWER")) 
            {
                if (!history.containsKey(p.getId())) 
                {
                    initializeList(p);
                    logger.fine(() -> "Created new history list for probe: " + p.getId());
                }
                if(pState.equals("MEASURING")) 
                {
                    history.get(p.getId()).add(formatMeasurementRecord(p, curSol));
                    logger.fine(() -> "Created new measurement record for: " + p.getId());
                }
                else 
                {
                    history.get(p.getId()).add(formatMovementRecord(p, curSol));
                    logger.fine(() -> "Created new movement record for: " + p.getId());
                }
            }
        }
    }

    // Create new list for every probe when it records its first activity
    private void initializeList(Probe p) 
    {
        history.put(p.getId(), new ArrayList<>());
        String historyHeader = "TO EARTH: " + p.getId() + " ACTIVITIES";
        history.get(p.getId()).add(historyHeader);
    }

    // Format the current measurement record through the recording list
    private String formatMeasurementRecord(Probe p, int curSol)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("    SOL ").append(curSol).append(":");
        for (String type : p.getMeasurementRecordings().keySet()) 
        {
            Double value = p.getMeasurementRecordings().get(type);
            if (value != null) 
            {
                sb.append(" ").append(type).append("=")
                .append(String.format("%.4f", value));
            }
        }
        return sb.toString();
    }
    
    // Format the movement Record through probs current lat and lon
    private String formatMovementRecord(Probe p, int curSol) 
    {
        return String.format("    SOL %d: %+f %+f", curSol, p.getLat(), p.getLon());
    }

    // Allow for printing of history when earth asks for probe history
    public void printProbeHistory(Probe p) 
    {
        List<String> probeHistory = history.get(p.getId());

        if (probeHistory == null) 
        {
            System.out.println("TO EARTH: MESSAGE ERROR \"Probe " + p.getId() + 
            " has performed no tasks\"");
            logger.info(() -> "No history avaiable to request for probe: " + p.getId());
        }
        else 
        {
            for(String s : probeHistory) 
            {
                System.out.println(s);
            }
            logger.fine(() -> "Observer printed history for probe: " + p.getId());
        }
    }
}