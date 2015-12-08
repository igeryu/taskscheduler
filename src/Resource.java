/**
 * Resource.java
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
 * Represents an resource owned by a <code>PCB</code> object. It also contains
 * an integer static value that represents the id to be used for the next
 * <code>Resource</code> object created, in the case that the constructor gets
 * an erroneous id.
 *
 * @author Alan Johnson
 * @version 1.0
 */
public class Resource {

    //                       ----  Class Variable  ----
    private static int nextID = 0;

    //                       ----  Instance Variables  ----
    private int id;
    private String name;
    boolean isBusy = false;

    //                       ----  Constructors  ----
    /**
     *
     * @param n
     */
    Resource(String n) {

        super();

        id = nextID++;

        if (n != null) {
            name = n;
        } else {
            name = String.format("Resource %d", id);
        }

    }  //  end Constructor (String)

//                       ----  Other Methods  ----

    /**
     *
     * @return String representation
     */
    @Override
    public String toString() {

        return name;

    }  //  end toString() method

}  //  end class Resource
