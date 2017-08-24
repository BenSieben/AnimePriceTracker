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
    private AnimePriceTrackerGraphPanel graphPanel;

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

        // Make tabbed pane to let users have tabs to switch between
        tabPane = new JTabbedPane();

        // Create tab for basic CLI operations and add it to the tabPane
        basicOperationsPanel = new AnimePriceTrackerBasicOperationsPanel(frameOutputStream);
        tabPane.addTab("Basic Commands", null, basicOperationsPanel,
                "Perform commands that can also be found on the command line interface");

        // TODO Create tab for graphing price history of products
        graphPanel = new AnimePriceTrackerGraphPanel();
        tabPane.addTab("Graph Product History", null, graphPanel,
                "Show graphs of product history");

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
     * Changes the message in the basic operations panel to the given new content
     * @param newContent the text to place on the message label
     */
    protected void changeBasicOperationMessageLabel(String newContent) {
        basicOperationsPanel.changeMessageLabelContents(newContent);
    }

    /**
     * Sets up System.out / System.err to redirect to custom streams that go
     * to the text area in the basic operations panel and disables components
     * on the GUI until stopRunBasicOperation() is called
     */
    protected void startRunBasicOperation() {
        // Disable all GUI components and set up System.out / System.err to stream to output text area
        basicOperationsPanel.clearOutputTextAreaContents();
        setEnablingOfAllComponents(false);
        modifyOutputStreams(false);
        basicOperationsPanel.changeTextAreaStream(frameOutputStream);
    }

    /**
     * Restores System.out / System.err to their default values (going to console output) and
     * restores functionality of GUI
     */
    protected void stopRunBasicOperation() {
        // Re-enable all GUI components and reset System.out / System.err streams to defaults
        setEnablingOfAllComponents(true);
        modifyOutputStreams(true);
        basicOperationsPanel.stopOutputTextAreaTimer();
    }

    /**
     * Adds new ActionListener for the parseBaseSentaiFilmworksPageButton JButton on basic operations panel
     * @param listener new ActionListener to add to the button
     */
    protected void addParseBaseSentaiFilmworksPageButtonActionListener(ActionListener listener) {
        basicOperationsPanel.addParseBaseSentaiFilmworksPageButtonActionListener(listener);
    }

    /**
     * Adds new ActionListener for the parseBaseRightStufPageButton JButton on basic operations panel
     * @param listener new ActionListener to add to the button
     */
    protected void addParseBaseRightStufPageButtonActionListener(ActionListener listener) {
        basicOperationsPanel.addParseBaseRightStufPageButtonActionListener(listener);
    }

    /**
     * Adds new ActionListener for the visitAllSentaiFilmworksPagesButton JButton on basic operations panel
     * @param listener new ActionListener to add to the button
     */
    protected void addVisitAllSentaiFilmworksPagesButtonActionListener(ActionListener listener) {
        basicOperationsPanel.addVisitAllSentaiFilmworksPagesButtonActionListener(listener);
    }

    /**
     * Adds new ActionListener for the visitAllRightStufPagesButton JButton on basic operations panel
     * @param listener new ActionListener to add to the button
     */
    protected void addVisitAllRightStufPagesButtonActionListener(ActionListener listener) {
        basicOperationsPanel.addVisitAllRightStufPagesButtonActionListener(listener);
    }

    /**
     * Adds new ActionListener for the updateCrawlDataButton JButton on basic operations panel
     * @param listener new ActionListener to add to the button
     */
    protected void addUpdateCrawlDataButtonActionListener(ActionListener listener) {
        basicOperationsPanel.addUpdateCrawlDataButtonActionListener(listener);
    }

    /**
     * Adds new ActionListener for the makeCsvsButton JButton on basic operations panel
     * @param listener new ActionListener to add to the button
     */
    protected void addMakeCsvsButtonActionListener(ActionListener listener) {
        basicOperationsPanel.addMakeCsvsButtonActionListener(listener);
    }

    /**
     * Changes System.out / System.err to different streams
     * @param setBackDefaultStreams true to change System.out / System.err to their default values,
     *                              false to change System.out / System.err to a new stream used
     *                              to send output to the basic operations panel text area
     */
    private void modifyOutputStreams(boolean setBackDefaultStreams) {
        // Remove existing frame output stream if it exists
        if(frameOutputStreamCapture != null && frameOutputStream != null) {
            frameOutputStreamCapture.close();
            try {
                frameOutputStream.close();
            }
            catch(IOException ex) {
                // Do nothing if IOException occurs when trying to close stream
            }
        }

        // Depending on boolean argument, we set System.out / System.err to defaults or custom ones
        if(setBackDefaultStreams) {
            // Set back default System.out / System.err streams
            System.setOut(new PrintStream(originalOutputStream));
            System.setErr(new PrintStream(originalErrStream));
        }
        else {
            // Change System.out / System.err to be our own output stream so we can capture things written to System.out
            frameOutputStream = new ByteArrayOutputStream();
            frameOutputStreamCapture = new PrintStream(frameOutputStream);
            System.setOut(frameOutputStreamCapture);
            System.setErr(frameOutputStreamCapture);
        }
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
        modifyOutputStreams(true);
        dispose();
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
