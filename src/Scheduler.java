/**
 * Scheduler.java
 * 
 * CMSC 412
 * Final Project
 * 
 * Alan Johnson
 * 20 July 15
 * NetBeans IDE 8.0.2
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>A <code>Scheduler</code> object owns an <code>ArrayList</code> of
 * <code>PCB</code> objects and a pointer to its owner (a <code>OS</code>
 * instance).</p>
 * 
 * <p>The owning <code>OS</code> object passes new <code>Resource</code> and
 * <code>MyThread</code> objects to the <code>Scheduler</code> as they are
 * added.  The <code>Scheduler</code> is then responsible for handing those
 * objects to their appropriate owner(s).</p>
 * 
 * @author      Alan Johnson
 * @version     1.0
 */
public class Scheduler {
    
    //                       ----  Instance Variables  ----
    
    OS owner;
    final ArrayList<PCB> PCBList;
    private Resource CPU, IO;
    private ArrayList<MyThread> threadList;
    private HashMap <String, Long> resultsList;
    private int processCount;
    
    enum Mode {FCFS, SJF};
    public Mode mode = Mode.FCFS;
    
    
    
    //                       ----  Constructor  ----
    
    /**
     * @param _owner      <code>OS</code> object that will own this <code>Scheduler</code>
     * @param _CPU
     * @param _IO
     */
    Scheduler (OS _owner, Resource _CPU, Resource _IO) {
        PCBList = new ArrayList<>();
        threadList = new ArrayList<>();
        resultsList = new HashMap<>();
        setOwner(_owner);
        
        CPU = _CPU;
        IO = _IO;
    }
    
    
    
    //                       ----  Other Methods  ----
    
    
    MyThread addProcess(PCB p) {
        PCBList.add(p);
        
        MyThread newThread = new MyThread(0, p, 5, 5, CPU, IO);
        p.addThread(newThread);
        threadList.add(newThread);
        processCount++;
        
        return newThread;
    }
    
  
    
    /**
     * Attempts to add supplied <code>MyThread</code> to a <code>PCB</code>
     * within this <code>Scheduler</code>.  If the appropriate
     * <code>PCB</code> cannot be found (index mismatch), the method returns
     * <code>false</code>.  Otherwise, the <code>MyThread</code> is added and
     * the method returns <code>true</code>.
     * 
     * @param thread     <code>MyThread</code> object to be added
     * @return        <code>Boolean</code> value to indicate whether the
     *                <code>MyThread</code> was successfully added.
     */
    boolean addThread(MyThread thread) {
        
        for (PCB p : PCBList) {
            
            if (p.getID() == thread.getProcessID()) {
            
                if (p.addThread(thread)) {
                    threadList.add(thread);
                    processCount++;
                    return true;
                }
        
            }
        }
        
        return false;
    }  //  end method addThread()
    
    /**
 * 
 * @param s
 * @param id
 * @param procID
 * @param cpu
 * @param io 
 */
boolean addThread (String s, int id, int procID, int cpu, int io) {
    
    for (PCB p : PCBList) {
        if (p.getID() == procID) {
            MyThread thread = new MyThread( s, id, p, cpu, io, CPU, IO);
            
            threadList.add(thread);
            return p.addThread(thread);
        }
    }
    return false;
}  //  end method addThread (String, int, int, int, int)
    
    private MyThread nextShortestJob() {
        if (threadList.size() > 0) {

            int shortestTime = 9999;
            MyThread shortestThread = threadList.get(0);

            for (MyThread t : threadList) {
                if ((t.getCPUTime() + t.getIOTime()) < shortestTime) {
                    shortestTime = t.getCPUTime() + t.getIOTime();
                    shortestThread = t;
                }  // found new shorter thread

            }  //  iterate through all threads in threadList

            threadList.remove(shortestThread);
            return shortestThread;

        }  //  threadList isn't empty

        return null;

    }  //  end method nextShortestJob () :: MyThread
    
    /**
     * Sets <code>owner</code> attribute to point towards the supplied
     * <code>OS</code> object.
     * 
     * @param os      <code>OS</code> object to own this <code>Scheduler</code>
     */
    public final void setOwner(OS os) {
        
        if (os != null) { owner = os; }
        
    }  //  end setOwner() method
    
    /**
     * 
     * @return 
     */
    int getThreadCount () { return threadList.size(); }
    
    private String longestWaitTime () {
        long longestTime = 0;
        String process = "";
        
        Iterator it = resultsList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            
            long time = (Long) entry.getValue();
            if (longestTime < time) {
                longestTime = time;
                process = (String) entry.getKey();
            }  //  new shortest time
        }  //  iterate through resultsList
        
        return (longestTime / 1000) + "s (" + process + ")";
        
    }  //  end method shortestWaitTime ()
    
    
    private String shortestWaitTime () {
        long shortestTime = 999999;
        String process = "";
        
        Iterator it = resultsList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            
            long time = (Long) entry.getValue();
            if (shortestTime > time) {
                shortestTime = time;
                process = (String) entry.getKey();
            }  //  new shortest time
        }  //  iterate through resultsList
        
        return (shortestTime / 1000) + "s (" + process + ")";
        
    }  //  end method shortestWaitTime ()
    
    public String startSim(Mode mode) {
        long     time = 0,
             stopTime,
              runTime;
        MyThread thread = threadList.get(0);
        
        switch (mode) {
            case FCFS:
                time = System.currentTimeMillis();
                for (MyThread t : threadList) {

                    long tempTime = (System.currentTimeMillis() - time) / 1000;
                    System.out.printf("\nStarting \"%s\" at %d (duration: %ds)", t.toString(), tempTime, ((t.getCPUTime() + t.getIOTime()) / 1));
                    (new Thread(t, t.toString())).start();

                    while (!t.cpuComplete) {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                        }
                    }

                    stopTime = System.currentTimeMillis();
                    runTime = stopTime - time;
                    resultsList.put(t.toString(), runTime);
                    thread = t;
                }
                break;

            case SJF:
                time = System.currentTimeMillis();
                while (threadList.size() > 0) {
                    thread = nextShortestJob();

                    long tempTime = (System.currentTimeMillis() - time) / 1000;
                    System.out.printf("\nStarting \"%s\" at %d (duration: %ds)", thread.toString(), tempTime, ((thread.getCPUTime() + thread.getIOTime()) / 1));
                    (new Thread(thread, thread.toString())).start();

                    while (!thread.cpuComplete) {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                        }
                    }

                    stopTime = System.currentTimeMillis();
                    runTime = stopTime - time;
                    resultsList.put(thread.toString(), runTime);

                }
                
                break;
                
        }  //  switch (mode)
        
        while (!thread.ioComplete) {
            try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                        }
        }
        
        stopTime = System.currentTimeMillis();
        runTime = stopTime - time;
        
        long average = runTime / processCount;
        
        String results =   "Processes : " + getThreadCount()
                         + "\nScheduling Policy : " + (mode == Mode.FCFS ? "FCFS" : "SJF")
                         + "\nTotal Run Time : " + (runTime / 1000) + "s"
                         + "\nShortest Wait Time : " + shortestWaitTime()
                         + "\nLongest Wait Time : " + longestWaitTime();
        
        return results;
        
        
    }  //  end method startSim(Mode)
    
    
    
    /**
     * 
     * @return     <code>String</code> representation
     */
    @Override public String toString() { return "Scheduler"; }  //  end toString() method
    
    
    
}  //  end class Scheduler
