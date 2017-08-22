package b7.tools.tracking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;

/**
 * JPanel that draws a basic Gui for performing basic commands
 * that can also be performed from the command line interface
 * in AnimePriceTracker
 */
public class AnimePriceTrackerBasicOperationsPanel extends JPanel {

    // Constant for how often to update the text area
    public static final int TEXT_AREA_REFRESH_PERIOD = 500;

    // Buttons on the panel to let users perform actions
    /*
    public static final int PARSE_BASE_SENTAI_FILMWORKS_PAGE = 1;
    public static final int PARSE_BASE_RIGHT_STUF_PAGE = 2;
    public static final int VISIT_ALL_SENTAI_FILMWORKS_PAGES = 3;
    public static final int VISIT_ALL_RIGHT_STUF_PAGES = 4;
    public static final int UPDATE_CRAWL_DATA = 5;
    public static final int MAKE_CSVS = 6;
     */
    private JButton parseBaseSentaiFilmworksPageButton,
            parseBaseRightStufPageButton,
            visitAllSentaiFilmworksPagesButton,
            visitAllRightStufPagesButton,
            updateCrawlDataButton,
            makeCsvsButton;

    // Text area to let users see output
    private JTextArea outputTextArea;

    // Timer responsible for printing contents of text stream into the text area
    private Timer outputTextAreaTimer;

    /**
     * Constructs a new AnimePriceTrackerBasicOperationsPanel
     * @param textAreaStream stream that is used to print messages into the output text area (set to
     *                       null to manually update text via changeOutputTextAreaContents())
     */
    public AnimePriceTrackerBasicOperationsPanel(ByteArrayOutputStream textAreaStream) {
        super();
        setLayout(new GridLayout(0, 2));

        // Create button panel to hold buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());

        // Label for button panel
        JLabel buttonPanelLabel = new JLabel("Operations");

        // Create a panel which holds all the buttons
        JPanel buttonCollectionPanel = new JPanel();
        buttonCollectionPanel.setLayout(new GridLayout(0, 1));

        // Create all buttons for button collection panel
        parseBaseSentaiFilmworksPageButton = new JButton("Parse base Sentai Filmworks page");
        buttonCollectionPanel.add(parseBaseSentaiFilmworksPageButton);
        parseBaseRightStufPageButton = new JButton("Parse base Right Stuf page");
        buttonCollectionPanel.add(parseBaseRightStufPageButton);
        visitAllSentaiFilmworksPagesButton = new JButton("Visit all Sentai Filmworks pages");
        buttonCollectionPanel.add(visitAllSentaiFilmworksPagesButton);
        visitAllRightStufPagesButton = new JButton("Visit all Right Stuf pages");
        buttonCollectionPanel.add(visitAllRightStufPagesButton);
        updateCrawlDataButton = new JButton("Update crawl data");
        buttonCollectionPanel.add(updateCrawlDataButton);
        makeCsvsButton = new JButton("Generate CSVs from crawl data");
        buttonCollectionPanel.add(makeCsvsButton);

        // Add label / button collection to button panel
        buttonPanel.add(buttonPanelLabel, BorderLayout.NORTH);
        buttonPanel.add(buttonCollectionPanel, BorderLayout.CENTER);

        // Create text area panel for showing output
        JPanel textAreaPanel = new JPanel();
        textAreaPanel.setLayout(new BorderLayout());

        // Create label for text area
        JLabel textAreaLabel = new JLabel("Output");

        // Set up the text area with a scroll pane attached to it
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);  // Make it so the output text area cannot be edited by users
        JScrollPane textAreaScrollPane = new JScrollPane(outputTextArea);

        // Add label / text area to text area panel
        textAreaPanel.add(textAreaLabel, BorderLayout.NORTH);
        textAreaPanel.add(textAreaScrollPane, BorderLayout.CENTER);

        // Add final button panel / text area panel to main panel
        add(buttonPanel);
        add(textAreaPanel);

        // Update the outputTextArea with given argument stream
        changeTextAreaStream(textAreaStream);
    }

    /**
     * Updates the panel to relay output from the given newTextAreaStream
     * @param newTextAreaStream the new stream to send out to the output text area
     */
    protected void changeTextAreaStream(ByteArrayOutputStream newTextAreaStream) {
        if(newTextAreaStream != null) {
            // Stop previous output text area timer if it exists
            if(outputTextAreaTimer != null) {
                outputTextAreaTimer.stop();
            }

            // Set up new timer to track output from the new text area stream
            final ByteArrayOutputStream textStream = newTextAreaStream;
            outputTextAreaTimer = new Timer(TEXT_AREA_REFRESH_PERIOD, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Print out contents of textAreaStream
                    changeOutputTextAreaContents(textStream.toString());
                }
            });
            outputTextAreaTimer.start();
        }
    }

    /**
     * Changes the outputTextArea's contents to the given new content
     * @param newContent the text to show in the text area
     */
    protected void changeOutputTextAreaContents(String newContent) {
        outputTextArea.setText(newContent);
    }

    /**
     * Clears the output text area's current contents
     */
    protected void clearOutputTextAreaContents() {
        outputTextArea.setText("");
    }

    /**
     * Sets enabling of all components in the GUI to be set to the given argument
     * (true = enable everything, false = disable everything)
     * @param enable true to enable all components, false to disable all components
     */
    protected void setEnablingOfAllComponents(boolean enable) {
        parseBaseSentaiFilmworksPageButton.setEnabled(enable);
        parseBaseRightStufPageButton.setEnabled(enable);
        visitAllSentaiFilmworksPagesButton.setEnabled(enable);
        visitAllRightStufPagesButton.setEnabled(enable);
        updateCrawlDataButton.setEnabled(enable);
        makeCsvsButton.setEnabled(enable);
    }

    /**
     * Adds new ActionListener for the parseBaseSentaiFilmworksPageButton JButton
     * @param listener new ActionListener to add to the button
     */
    protected void addParseBaseSentaiFilmworksPageButtonActionListener(ActionListener listener) {
        if(listener != null) parseBaseSentaiFilmworksPageButton.addActionListener(listener);
    }

    /**
     * Adds new ActionListener for the parseBaseRightStufPageButton JButton
     * @param listener new ActionListener to add to the button
     */
    protected void addParseBaseRightStufPageButtonActionListener(ActionListener listener) {
        if(listener != null) parseBaseRightStufPageButton.addActionListener(listener);
    }

    /**
     * Adds new ActionListener for the visitAllSentaiFilmworksPagesButton JButton
     * @param listener new ActionListener to add to the button
     */
    protected void addVisitAllSentaiFilmworksPagesButtonActionListener(ActionListener listener) {
        if(listener != null) visitAllSentaiFilmworksPagesButton.addActionListener(listener);
    }

    /**
     * Adds new ActionListener for the visitAllRightStufPagesButton JButton
     * @param listener new ActionListener to add to the button
     */
    protected void addVisitAllRightStufPagesButtonActionListener(ActionListener listener) {
        if(listener != null) visitAllRightStufPagesButton.addActionListener(listener);
    }

    /**
     * Adds new ActionListener for the updateCrawlDataButton JButton
     * @param listener new ActionListener to add to the button
     */
    protected void addUpdateCrawlDataButtonActionListener(ActionListener listener) {
        if(listener != null) updateCrawlDataButton.addActionListener(listener);
    }

    /**
     * Adds new ActionListener for the makeCsvsButton JButton
     * @param listener new ActionListener to add to the button
     */
    protected void addMakeCsvsButtonActionListener(ActionListener listener) {
        if(listener != null) makeCsvsButton.addActionListener(listener);
    }
}
