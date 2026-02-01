/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: StatusLogger
 * Purpose: Observerer that keeps track of every probes current status, allowing
 * for status calls/logging.
 */
package edu.curtin.mars.observer;

import java.util.*;
import java.io.PrintWriter;

import edu.curtin.mars.observer.EventObserver;
import edu.curtin.mars.model.Probe;
import java.util.logging.Logger;
import java.util.logging.Level;

public class StatusLogger implements EventObserver 
{
    private static final Logger logger = 
        Logger.getLogger(StatusLogger.class.getName());

    // Map of each probe to its current status
    private Map<String, String> curStatusOfAll = new HashMap<>();
    private PrintWriter writer;

    public StatusLogger(PrintWriter w) 
    {
        this.writer = w;
    }

    //Update each probs status when observers are notified after a sol completes,
    // printing the status to file.
    @Override
    public void update(Map<String, Probe> probes, int curSol) 
    {
        writer.println("SOL " + curSol);
        for (Probe p : probes.values())
        {
            String status = String.format("%s at %.6f %.6f, %s", p.getId(), 
                p.getLat(), p.getLon(), p.getState().stateName());
            curStatusOfAll.put(p.getId(), status);
            writer.println("    " + status);
        }
        
        writer.flush();

        logger.fine(() -> "Observer printed the status of all probes to file");
    }

    // Allow for printing of status when earth requests
    public void printProbeStatus(Probe p) 
    {
        String status = String.format("%s at %.6f %.6f, %s", p.getId(), 
                p.getLat(), p.getLon(), p.getState().stateName());
        System.out.println(status);
        logger.fine(() -> "Observer printed the status of probe: " + p.getId());

    }
}

    