/**
 * PCB.java
 * 
 * CMSC 412
 * Final Project
 * 
 * Alan Johnson
 * 20 July 15
 * NetBeans IDE 8.0.2
 */

import java.util.ArrayList;

/**
 *
 * @author      Alan Johnson
 * @version     1.0
 */
public class PCB {
    
    //                       ----  Class Variable  ----
    
    private static int nextID = 0;
    
    //                       ----  Instance Variables  ----

    private String name;
    private int id;
    ArrayList<MyThread> threadList;
    private Resource CPU, IO;

    public enum Status {

        RUNNING, WAITING, DONE
    };
    private Status status;

    
    //                       ----  Constructors  ----
    
    PCB (String _name, Resource _CPU, Resource _IO) {
        super();
            
        id = nextID++;
        
        if (_name != null)
            name = _name;
        else name = String.format("Process %d", id);
        
        name = String.format("Process %d", id);
        threadList = new ArrayList<>();
        
        CPU = _CPU;
        IO = _IO;
    }
    
    
    
    
    
        
    /**
     * Returns the value of <code>name</code>.
     * 
     * @return      the value of <code>name</code>
     */
    String getName(){ return name; }
    
    /**
     * 
     * @param t
     * @return 
     */
    boolean addThread(MyThread t) {
       if (t != null) {
           t.setOwner(this);
           threadList.add(t);
           return true;
       } 
       
       return false;
    }  //  end method addThread(MyThread)
    
    
    /**
     * Returns the value of <code>id</code>.
     * 
     * @return      the value of the <code>id</code>
     */
    int getID() { return id; }  //  end getID() method

}  //  end class PCB
