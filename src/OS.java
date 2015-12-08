/**
 * OS.java
 *
 * CMSC 412
 * Final Project
 *
 * Alan Johnson
 * 20 July 15
 * NetBeans IDE 8.0.2
 */

import java.awt.*;
import javax.swing.*;
import static javax.swing.ScrollPaneConstants.*;



/**
 * A <code>OS</code> object owns a <code>Scheduler</code>, two
 * <code>Resource</code> instances and an extension of <code>JFrame</code>.
 *
 * The <code>OS</code> is responsible for creating a new <code>Scheduler</code>
 * instance.  The <code>CPU</code> and <code>IO</code>
 * <code>Resource</code>-instances are also created and passed to the new
 * <code>Scheduler</code>-instance.
 *
 * When the <code>startSim()</code> method is called, it passes this message to
 * its <code>Scheduler</code>-instance along with the chosen scheduling policy.
 *
 * @author     Alan Johnson
 * @version    1.0
 */
public class OS {

    //                       ----  Instance Variables  ----

    private Scheduler scheduler;
    private final String name;
    private final Resource CPU, IO;

    //                      ----  GUI Components ----

    private final OSFrame osFrame;
    
    //                       ----  Constructors  ----
    
    /**
     * @param _name      Name of new <code>OS</code> object
     */
    OS(String _name) {
       
        if (_name != null)
            name = _name;
        else
            name = "No_Name";

         CPU = new Resource("CPU");
        IO = new Resource("IO");
        
        scheduler = new Scheduler(this, CPU, IO);
        osFrame = new OSFrame();
        
        

    }

void addProcess (String s) {
    PCB newPCB = new PCB(s, CPU, IO);
    
    MyThread newThread = scheduler.addProcess(newPCB);
    
    osFrame.updateProgressScrollPane();
}
  

/**
 * 
 * @param s
 * @param id
 * @param procID
 * @param cpu
 * @param io 
 */
void addThread (String s, int id, int procID, int cpu, int io) {
    scheduler.addThread(s, id, procID, cpu, io);
    
    osFrame.updateProgressScrollPane();
}
 

String startSim(Scheduler.Mode mode) {
    return scheduler.startSim(mode);
}


    /**
     * @return     <code>String</code> representation
     */
    @Override public String toString() { return "OS"; }  //  end toString() method



    /**
     * <p>Visually represents the owning OS object's data in a list format.</p>
     *
     * @author      Alan Johnson
     * @version     1.0
     */
    private class OSFrame extends JFrame {
        private static final long serialVersionUID = 1L;

       private JScrollPane jobProgressScrollPane;
        


        /**
         * 
         */
        OSFrame() {

            this.setTitle(name);

            //             -----  Header -----

            add(new JLabel("OS"));
            
            
            //             -----  Data : Set up jobProgressScrollPane -----
            jobProgressScrollPane = buildThreadProgressList();
            jobProgressScrollPane.setBorder(BorderFactory.createTitledBorder("Threads"));
            
            add(jobProgressScrollPane, BorderLayout.CENTER);
            

            //                 ----  Finalize Window  ----
            int width = 450; int height = 800;
            setMinimumSize(new Dimension(width, height));
            setLocationRelativeTo(null);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);

        }  //  end OSFrame constructor
        
        
        
        private void updateProgressScrollPane() {
            jobProgressScrollPane.remove(this);
            
            
            JList<JPanel> list = new JList<JPanel>() {
                private static final long serialVersionUID = 1L;
                @Override public Dimension getPreferredSize() {
                    int width = 250;
                    int rows = scheduler.getThreadCount();
                    int height = 68 * rows;
                    return new Dimension(width, height);
                }
            };
            
            list.setLayout(new FlowLayout());
            list.setLayoutOrientation(JList.VERTICAL);
            
            jobProgressScrollPane.setViewportView(list);
            
            //  iterate through PCBList, find MyThread objects and add their
            //  panes:
            
            for (PCB p : scheduler.PCBList) {
                for (MyThread t : p.threadList) {
                    
                    if (t.getPanel() == null) System.out.println("\n        Fail!");
                            
                    list.add(t.getPanel());
                }
            }  //  for each PCB instance in PCBList
            
            list.validate();
            
            jobProgressScrollPane.getVerticalScrollBar().setUnitIncrement(100);
            jobProgressScrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
            
        }
        
        /**
         * Creates a <code>JScrollPane</code> that contains a <code>JList</code>
         * with each <code>MyThread</code> object's <code>JPanel</code>.  The
         * <code>JList</code> overrides <code>getPreferredSize()</code> so that
         * the returned size is based on the number of <code>MyThread</code>
         * panels to be displayed.
         * 
         * @return      A <code>JScrollPane</code> containing a <code>JList</code>
         *              with every <code>MyThread</code> panel
         */
        private JScrollPane buildThreadProgressList() {
            JList<JPanel> list = new JList<JPanel>() {
                private static final long serialVersionUID = 1L;
                @Override public Dimension getPreferredSize() {
                    int width = 350;
                    int rows = scheduler.PCBList.size();
                    int height = 50 * rows;
                    return new Dimension(width, height);
                }
            };
            
            list.setLayout(new FlowLayout());
            list.setLayoutOrientation(JList.VERTICAL);
            
            JScrollPane pane = new JScrollPane(list);
            pane.getVerticalScrollBar().setUnitIncrement(100);
            pane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
            
            //  iterate through PCBList, find MyThread objects and add their
            //  panes:
            for (PCB p : scheduler.PCBList) {
                for (MyThread t : p.threadList) {
                    
                    if (t.getPanel() == null) System.out.println("\n        Fail!");
                            
                    list.add(t.getPanel());
                }
            }  //  for each PCB instance in PCBList
            
            list.validate();
            pane.validate();
            
            return pane;
            
        }  // end method buildThreadProgressList()

  
        
    }  //  end class OSFrame



}  //  end class OS
