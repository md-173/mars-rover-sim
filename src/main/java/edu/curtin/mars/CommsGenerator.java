package edu.curtin.mars;
import java.util.*;
import java.util.function.*;
import java.io.*;
import java.nio.file.*;
import java.util.logging.Logger;

/**
 * DO NOT ADD ANYTHING TO THIS FILE. This is just a utility to supply data to your Assignment 2
 * simulation app. Your code must work with the ORIGINAL version of this file.
 *
 * You need to create a new CommsGenerator object, then call 'nextMessage()' periodically, which
 * returns the next message in a queue of randomly-generated messages.
 *
 * This code randomly generates messages. Some of the messages are invalid! To help with
 * debugging, you can:
 * (1) supply a seed for the random number generator to the constructor, to generate the same
 *     sequence of messages each time. Otherwise, the sequence will always be different.
 * (2) call 'setErrorProbability(n)' to change the frequency of invalid messages. They will be
 *     disabled altogether if n == 0.0.
 */
public class CommsGenerator
{
    private static final String[] MEASUREMENT_TYPES = {
        "temperature", "pressure", "wind-speed", "visibility", "dust-concentration", "magnetic-flux"
    };

    private static final double MOVE_P = 0.2;
    private static final double MEASURE_P = 0.2;
    private static final double HISTORY_P = 0.2;
    private static final double STATUS_P = 0.2;
    private static final double SCREW_UP_P = 0.05;

    private static final int N_ROVERS = 5;
    private static final int N_DRONES = 3;
    private static final int N_PROBES = N_ROVERS + N_DRONES;

    private static final double LAT_MIN = -30.0;
    private static final double LAT_MAX = 30.0;
    private static final double LONG_MIN = 90.0;
    private static final double LONG_MAX = 120.0;

    private static final double MAX_DELTA = 0.2;
    private static final int MIN_MEASUREMENT_DURATION = 1;
    private static final int MAX_MEASUREMENT_DURATION = 20;

    private long lastTime = System.currentTimeMillis();
    private double errorProb = SCREW_UP_P;
    private Random rand;

    private boolean init = false;
    private double[] curLat = new double[N_PROBES];
    private double[] curLong = new double[N_PROBES];
    
    @SuppressWarnings("PMD.LooseCoupling")  // We call LinkedList.poll(), which List doesn't have.
    private LinkedList<String> messages = new LinkedList<>();
    
    
    private final double randDouble(double min, double max)
    {
        return rand.nextDouble() * (max - min) + min;
    }

    private final int randInt(int minIncl, int maxExcl)
    {
        return rand.nextInt(maxExcl - minIncl) + minIncl;
    }

    private final String randString(int len)
    {
        var sb = new StringBuilder();
        for(int i = 0; i < len; i++)
        {
            sb.append((char)(rand.nextInt(0x5f) + 0x20));
        }
        return sb.toString();
    }

    private final String label(int p)
    {
        return (p < N_ROVERS) ? ("rover-" + p) : ("drone-" + (p - N_ROVERS));
    }

    private final double clamp(double n, double min, double max)
    {
        if(max < min)
        {
            throw new IllegalArgumentException("min cannot be higher than max");
        }
        return Math.min(Math.max(n, min), max);
    }

    public CommsGenerator(long seed)
    {
        this.rand = new Random(seed);
    }
    
    public CommsGenerator()
    {
        this.rand = new Random();
    }
    
    public void setErrorProbability(double errorProb)
    {
        if(errorProb < 0.0 || 0.5 < errorProb)
        {
            throw new IllegalArgumentException(
                "The error probability is limited to 0-0.5. (It is applied repeatedly, and hence values approaching 1 would create a real mess.)");
        }
        this.errorProb = errorProb;
    }

    /**
     * Retrieves the next input message, generated randomly as needed.
     */
    public String nextMessage()
    {
        if(!init)
        {
            init = true;
            for(int p = 0; p < N_PROBES; p++)
            {
                curLat[p] = randDouble(LAT_MIN, LAT_MAX);
                curLong[p] = randDouble(LONG_MIN, LONG_MAX);
                messages.add(String.format("%s at %.6f %.6f", label(p), curLat[p], curLong[p]));
            }
        }
    
        long thisTime = System.currentTimeMillis();
        for(long t = lastTime + 999L; t < thisTime; t += 1000L)
        {
            var newMessages = new ArrayList<String>();
        
            while(rand.nextDouble() < MOVE_P)
            {
                int p = rand.nextInt(N_PROBES);
                curLat[p]  = clamp(curLat[p]  + randDouble(-MAX_DELTA, MAX_DELTA), LAT_MIN, LAT_MAX);
                curLong[p] = clamp(curLong[p] + randDouble(-MAX_DELTA, MAX_DELTA), LONG_MIN, LONG_MAX);
                newMessages.add(String.format("%s move %.6f %.6f", label(p), curLat[p], curLong[p]));
            }
            
            while(rand.nextDouble() < MEASURE_P)
            {
                newMessages.add(String.format("%s measure %s %d",
                    label(rand.nextInt(N_PROBES)),
                    MEASUREMENT_TYPES[rand.nextInt(MEASUREMENT_TYPES.length)],
                    randInt(MIN_MEASUREMENT_DURATION, MAX_MEASUREMENT_DURATION + 1)));
            }

            while(rand.nextDouble() < HISTORY_P)
            {
                newMessages.add(String.format("%s history", label(rand.nextInt(N_PROBES))));
            }

            while(rand.nextDouble() < STATUS_P)
            {
                newMessages.add(String.format("%s status", label(rand.nextInt(N_PROBES))));
            }

            while(rand.nextDouble() < errorProb)
            {
                var parts = new ArrayList<String>();
                if(rand.nextDouble() < 0.5)
                {
                    parts.add(label(rand.nextInt(N_PROBES)));
                }
                if(rand.nextDouble() < 0.5)
                {
                    parts.add(rand.nextDouble() < 0.5 ? "move" : "measure");
                }
                while(rand.nextDouble() < 0.5)
                {
                    parts.add(String.valueOf(rand.nextDouble()));
                }
                parts.add(randString(rand.nextInt(10) + 1));
                newMessages.add(String.join(" ", parts));
            }
            
            Collections.shuffle(newMessages);
            messages.addAll(newMessages);
        }
        lastTime = thisTime;
        return messages.poll();
    }
}
