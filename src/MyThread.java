/**
 * MyThread.java
 *
 * CMSC 412
 * Final Project
 *
 * Alan Johnson
 * 20 July 15
 * NetBeans IDE 8.0.2
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Simulates an thread created by an operating system scheduler. It also
 * contains an integer static value that represents the id to be used for the
 * next <code>MyThread</code> object created, in the case that the constructor
 * gets an erroneous id.
 *
 * @author Alan Johnson
 * @version 1.0
 */
public class MyThread implements Runnable {

    //                       ----  Instance Variables  ----
    private final int id, processID;
    private int cpuTime, ioTime;
    boolean cpuComplete = false,
                   ioComplete = false;
    private final String name;
    private PCB.Status status;
    private PCB owner;
    private Resource CPU, IO;
    private final MyThreadPanel threadPanel;

    //                       ----  Constructors  ----
    MyThread(String _name, int _id, PCB _owner, int _cpuTime, int _ioTime, Resource _CPU,
            Resource _IO) {
        super();

        id = _id;
        owner = _owner;
        processID = owner.getID();
        cpuTime = _cpuTime;
        ioTime = _ioTime;

        if (_name != null) {
            name = _name;
        } else {
            name = String.format("Thread %d", id);
        }

        if (_CPU != null) {
            CPU = _CPU;
        }

        if (_IO != null) {
            IO = _IO;
        }

        threadPanel = new MyThreadPanel(this);

    }

    MyThread(int _id, PCB _owner, int _cpuTime, int _ioTime, Resource _CPU, Resource _IO) {
        this(null, _id, _owner, _cpuTime, _ioTime, _CPU, _IO);
    }

    //                       ----  Other Methods  ----
    
    /**
     * @return 
     */
    int getCPUTime () { return cpuTime; }
    
    /**
     * 
     * @return 
     */
    int getIOTime () { return ioTime; }
    
    /**
     * Returns the value of <code>processID</code>.
     *
     * @return the value of the <code>processID</code>
     */
    int getProcessID() {
        return processID;
    }  //  end method getID()
    
    /**
     *
     * @return
     */
    JPanel getPanel() {
        return threadPanel;
    }  //  end method getPanel()

    /**
     *
     */
    @Override
    public void run() {

        int totalTime = cpuTime + ioTime;
        
        if (cpuTime > 0) {
            threadPanel.modeLabel.setText("CPU");
            runActions(CPU, cpuTime);
            cpuTime = 0;
            threadPanel.modeLabel.setText("");
            threadPanel.progressBar.setValue((int) ((((float) (totalTime - (cpuTime + ioTime))) / ((float) totalTime)) * 100));
        }
        cpuComplete = true;

        if (ioTime > 0) {
            threadPanel.modeLabel.setText("I/O");
            runActions(IO, ioTime);
            ioTime = 0;
            threadPanel.modeLabel.setText("");
            threadPanel.progressBar.setValue((int) ((((float) (totalTime - (cpuTime + ioTime))) / ((float) totalTime)) * 100));
        }
        ioComplete = true;
        System.out.printf("\nEnding   \"%s\"", name);
        
        threadPanel.progressBar.setValue(100);

    }  //  end method run()

    /**
     *
     * @param lock
     * @param runTime
     */
    private void runActions(Resource lock, int runTime) {
        
        //  Prep:
        Random random = new Random();
        long time = System.currentTimeMillis();
        long startTime = time;
        long stopTime = time + 1000 * runTime;
        double duration = stopTime - time;

        //  Acquire lock on resource:
        if (lock != null) {
            synchronized (lock) {
                while (lock.isBusy) {
                    threadPanel.setStatus(PCB.Status.WAITING);
                    threadPanel.modeLabel.setText("");
                    try {
                        lock.wait();
                    } catch (Exception e) {
                    }
                }  //  end while
                lock.isBusy = true;
            }  //  end synchronized(lock)

            //  Do CPU- or I/O-burst:
            while (time < stopTime) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
                threadPanel.setStatus(PCB.Status.RUNNING);
                threadPanel.modeLabel.setText(lock.toString());
                time += 100;
                threadPanel.processBar.setValue((int) (((time - startTime) / duration) * 100));
            }  //  end while time remaining

            //  Display completion:
            threadPanel.processBar.setValue(100);
            threadPanel.setStatus(PCB.Status.DONE);

            //  Unlock resource
            synchronized (lock) {
                lock.isBusy = false;
                lock.notifyAll();
            }  // end synchronized

        }  // end if (lock not null)

    }  //  end method runActions()

    /**
     *
     * @param p
     */
    void setOwner(PCB p) {
        if (p != null) {
            owner = p;
        }
    }
    
    /**
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return name;

    }  //  end method toString()

    /**
     *
     */
    private class MyThreadPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        
        JLabel statusLabel = new JLabel(),
                modeLabel = new JLabel();
        JProgressBar progressBar, processBar;
        MyThread owner;
        
        /**
         *
         * @param t
         */
        public MyThreadPanel(MyThread t) {
            super();
            if (t != null) {
                owner = t;
            }
            FlowLayout layout = new FlowLayout();
            setSize(400, 40);

            //  1st Panel : Name
            setLayout(layout);
            JPanel panel = new JPanel();
            JLabel label = new JLabel(String.format("%s (%s)", owner.name, owner.owner.getName()));
            this.setBorder(BorderFactory.createTitledBorder(owner.toString()));
            label.getInsets().set(1, 1, 1, 1);
            label.setPreferredSize(new Dimension(130, 20));
            panel.add(label);
            panel.setPreferredSize(new Dimension(140, 30));
            add(panel);

            //  2nd Panel : Progress
            panel = new JPanel();
            progressBar = new JProgressBar();
            progressBar.setStringPainted(true);
            progressBar.setPreferredSize(new Dimension(50, 20));
            panel.add(progressBar);
            panel.setPreferredSize(new Dimension(60, 30));
            add(panel);

            //  3rd Panel : Status
            panel = new JPanel();
            
            statusLabel.setPreferredSize(new Dimension(80, 20));
            panel.add(statusLabel);
            panel.setPreferredSize(new Dimension(85, 30));
            add(panel);

            //  4th Panel : Mode
            panel = new JPanel();
            panel.setLayout(new GridLayout(2, 1));
            modeLabel.setPreferredSize(new Dimension(60, 20));
            panel.add(modeLabel, 0, 0);
            panel.setPreferredSize(new Dimension(60, 30));
            processBar = new JProgressBar();
            processBar.setStringPainted(true);
            processBar.setPreferredSize(new Dimension(50, 20));
            panel.add(processBar, 0, 1);
            add(panel);

        }  //  end MyThread constructor

        /**
         *
         * @param st
         */
        void setStatus(PCB.Status st) {

            switch (st) {
                case RUNNING:
                    statusLabel.setBackground(Color.green);
                    statusLabel.setText("Running");
                    break;
                
                case WAITING:
                    statusLabel.setBackground(Color.orange);
                    statusLabel.setText("Waiting");
                    break;

                case DONE:
                    statusLabel.setBackground(Color.red);
                    statusLabel.setText("Done");
                    break;
            }  //  end switch
            
        }  //  end method setStatus()
        
    }  //  end class MyThreadPanel

}  //  end class MyThread
