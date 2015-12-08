/**
 * Simulator.java
 *
 * CMSC 412
 * Final Project
 *
 * Alan Johnson
 * 20 July 15
 * NetBeans IDE 8.0.2
 */

import java.util.Random;
import javax.swing.*;

/**
 * Tests the OS class by commanding it to create 10 processes and starting them.
 *
 * @author Alan Johnson
 * @version 1.0
 */
public class Simulator {

    /**
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {

        //                       ----  Setup  ----
        System.out.println("Alan Johnson, CMSC 412, Final Project\n");

        OS os = startOptions();
        Scheduler.Mode mode = getMode();
        System.out.print("\n-----------------------------");
        
        os.addProcess("Process 1");
        
        int numThreads = 0;
        do {
        String input = JOptionPane.showInputDialog(null, "Process 1 contains one thread already." 
                                                       + "\nHow many additional"
                                                       + " threads should be"
                                                       + " loaded?");
        
        try {
            numThreads = Integer.valueOf(input);
            if (numThreads <= 0) throw new Exception();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Please enter a positive integer.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        
        } while (numThreads <= 0);
        
        Random random = new Random();
        for (int i = 0; i < numThreads;) {
            String name = "Thread " + ++i;
            int cpu = random.nextInt(20);
            int io = random.nextInt(20);
            
            os.addThread(name, 1, 0, cpu, io);
        }

        String results = os.startSim(mode);
        
        JOptionPane.showMessageDialog(null, results, "Results", JOptionPane.INFORMATION_MESSAGE);

    }
    
    private static Scheduler.Mode getMode () {
        Object[] options = {"FCFS", "SJF"};
        String prompt = "Please choose the schedule method (Default: FCFS)"
                + "\n First Come, First Served (FCFS)"
                + "\n Shortest Job First (SJF)";
        
        int n = JOptionPane.showOptionDialog(null, prompt, null,
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);
        
        switch (n) {
            case 0:
                System.out.print("\nUsing 'First Come, First Served'");
                return Scheduler.Mode.FCFS;
                
            case 1:
                System.out.print("\nUsing 'Shortest Job First'");
                return Scheduler.Mode.FCFS;
        }
        
        return Scheduler.Mode.FCFS;
    }  //  end method getMode()

    /**
     * Asks the user to input the name of the simulated operating system. The
     * request is repeated until a valid string is entered.
     * 
     * A new <code>OS</code> instance is created using that name and returned by the method.
     *
     * @return     <code>OS</code> object representing input data chosen by user
     */
    private static OS startOptions() {

        String name = "";

        do {

            name = JOptionPane.showInputDialog(null, "Please input the name of your OS");

        } while (name.equals(""));
        
        System.out.print("\nInitializing OS: " + name);

        return new OS(name);

    }  // end startOptions() method

}  //  end class Simulator
