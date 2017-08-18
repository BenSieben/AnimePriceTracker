package b7.tools.tracking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Class to make a GUI interface for the Anime
 * Price Tracker program
 */
public class AnimePriceTrackerGUI extends JFrame implements WindowListener {

    // Flag to keep track of when the window closes
    private volatile boolean hasClosed = false;

    public AnimePriceTrackerGUI() {
        super("Anime Price Tracker");

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(1280, 720));
        setMinimumSize(new Dimension(640, 360));
        setLocationRelativeTo(null);

        addWindowListener(this);

        requestFocus();
        pack();
        setVisible(true);

        hasClosed = false;
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
     * can tell when window has closed)
     * @param e not used
     */
    @Override
    public void windowClosing(WindowEvent e) {
        hasClosed = true;
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
