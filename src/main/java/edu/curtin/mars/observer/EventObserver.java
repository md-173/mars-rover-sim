/**
 * Name: Michael Durkan 
 * ID: 17378383
 * File: EventObserver
 * Purpose: Interface for the observers, allowing for polymporphic updates after sols
 */
package edu.curtin.mars.observer;

import java.util.*;
import edu.curtin.mars.model.Probe;
import edu.curtin.mars.controller.MarsSciSat;

public interface EventObserver 
{
    void update(Map<String, Probe> probes, int curSol);
}