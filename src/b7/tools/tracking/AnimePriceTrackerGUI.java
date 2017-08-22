package b7.tools.tracking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Class to make a GUI interface for the Anime
 * Price Tracker program
 */
public class AnimePriceTrackerGUI extends JFrame implements WindowListener {

    // Reference to original System.out OutputStream / System.err OutputStream
    private final OutputStream originalOutputStream = System.out;
    private final OutputStream originalErrStream = System.err;

    // The OutputStream we use to override the default System.out and System.err (and PrintStream that uses it)
    private ByteArrayOutputStream frameOutputStream;
    private PrintStream frameOutputStreamCapture;

    // Flag to keep track of when the window closes
    private volatile boolean hasClosed = false;

    // Various swing components used in the GUI
    private JTabbedPane tabPane;
    private AnimePriceTrackerBasicOperationsPanel basicOperationsPanel;

    /**
     * Constructs a new AnimePriceTrackerGUI
     */
    public AnimePriceTrackerGUI() {
        super("Anime Price Tracker");

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        addWindowListener(this);

        // Construct JPanels to construct the complete GUI in this JFrame

        // Master JPanel that holds all other panels
        JPanel masterPanel = new JPanel();
        masterPanel.setLayout(new GridLayout(1, 1));

        // Change System.out / System.err to be our own output stream so we can capture things written to System.out
        frameOutputStream = new ByteArrayOutputStream();
        frameOutputStreamCapture = new PrintStream(frameOutputStream);
        System.setOut(frameOutputStreamCapture);
        System.setErr(frameOutputStreamCapture);

        frameOutputStreamCapture.println("Hi");
        System.out.println(frameOutputStream.toString());

        // Make tabbed pane to let users have tabs to switch between
        tabPane = new JTabbedPane();

        // TODO Create tab for basic CLI operations and add it to the tabPane
        basicOperationsPanel = new AnimePriceTrackerBasicOperationsPanel(frameOutputStream);
        tabPane.addTab("Basic Commands", null, basicOperationsPanel,
                "Perform commands that can also be found on the command line interface");

        // Add action listeners for the buttons on the basic operations panel
        basicOperationsPanel.addParseBaseRightStufPageButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO actually perform action
                System.out.println("addParseBaseRightStufPageButtonActionListener");
            }
        });
        basicOperationsPanel.addParseBaseRightStufPageButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO actually perform action
                System.out.println("addParseBaseRightStufPageButtonActionListener");
            }
        });
        basicOperationsPanel.addVisitAllSentaiFilmworksPagesButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO actually perform action
                System.out.println("addVisitAllSentaiFilmworksPagesButtonActionListener");
            }
        });
        basicOperationsPanel.addVisitAllRightStufPagesButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO actually perform action
                System.out.println("addVisitAllSentaiFilmworksPagesButtonActionListener");
            }
        });
        basicOperationsPanel.addUpdateCrawlDataButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO actually perform action
                System.out.println("addUpdateCrawlDataButtonActionListener");
            }
        });
        basicOperationsPanel.addMakeCsvsButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO actually perform action
                System.out.println("addMakeCsvsButtonActionListener");
            }
        });

        // TODO Create tab for graphing price history of products
        tabPane.addTab("Graph Product History", null, new JPanel(),
                "Show graphs of product history");

        // Making a pretend tab to add for testing things out
        JPanel testButtonPanel = new JPanel();
        final JButton testButton = new JButton("Press me!");
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Below command will freeze the GUI until it is completed
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        testButton.setEnabled(false);
                        RightStufCrawler rightStufCrawler = new RightStufCrawler();
                        try {
                            sleep(5000);
                        }
                        catch(InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        rightStufCrawler.parseBasePage();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                testButton.setText("Done!");
                                testButton.setEnabled(true);
                            }
                        });
                    }
                };
                thread.start();
                // The below code causes GUI to freeze up until thread created just above finishes
                /*SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            thread.join();
                        }
                        catch(InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                });*/
            }
        });
        testButtonPanel.add(testButton);

        tabPane.addTab("Sample Tab", null, testButtonPanel,
                "Sample tab which is for testing purposes");

        // Add tabPane to this frame so it shows up
        add(tabPane);

        setPreferredSize(new Dimension(1280, 720));
        setMinimumSize(new Dimension(640, 360));
        setLocationRelativeTo(null);

        requestFocus();
        pack();
        setVisible(true);

        hasClosed = false;
    }

    /**
     * Sets enabling of all components in the GUI to be set to the given argument
     * (true = enable everything, false = disable everything)
     * @param enable true to enable all components, false to disable all components
     */
    private void setEnablingOfAllComponents(boolean enable) {
        tabPane.setEnabled(enable);
        basicOperationsPanel.setEnablingOfAllComponents(enable);
    }

    /**
     * Returns current status of whether or not the JFrame of the GUI has been closed
     * @return current status of whether or not the JFrame of the GUI has been closed
     */
    public boolean hasClosed() {
        return hasClosed;
    }

    /**
     * Method that gets fired when the GUI window is closed
     * (sets hasClosed field to true so controlling programs
     * can tell when window has closed and resets the
     * output / error streams of System back to their default streams)
     * @param e not used
     */
    @Override
    public void windowClosing(WindowEvent e) {
        hasClosed = true;
        System.setOut(new PrintStream(originalOutputStream));
        System.setErr(new PrintStream(originalErrStream));
        frameOutputStreamCapture.close();
        try {
            frameOutputStream.close();
        }
        catch(IOException ex) {
            // Do nothing if IOException occurs when trying to close stream
        }
    }

    /**
     * Not used, required by WindowListener interface
     * @param e not used
     */
    @Override
    public void windowOpened(WindowEvent e) { }
    /**
     * Not used, required by WindowListener interface
     * @param e not used
     */
    @Override
    public void windowClosed(WindowEvent e) { }
    /**
     * Not used, required by WindowListener interface
     * @param e not used
     */
    @Override
    public void windowIconified(WindowEvent e) { }
    /**
     * Not used, required by WindowListener interface
     * @param e not used
     */
    @Override
    public void windowDeiconified(WindowEvent e) { }
    /**
     * Not used, required by WindowListener interface
     * @param e not used
     */
    @Override
    public void windowActivated(WindowEvent e) { }
    /**
     * Not used, required by WindowListener interface
     * @param e not used
     */
    @Override
    public void windowDeactivated(WindowEvent e) { }
}
