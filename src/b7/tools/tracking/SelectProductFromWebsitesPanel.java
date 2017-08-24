package b7.tools.tracking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.*;
import java.awt.*;

/**
 * JPanel that can be used to create appropriate buttons
 * for user to select from a list of websites to show
 * products of, and then select one of those products
 */
public class SelectProductFromWebsitesPanel extends JPanel {

    // ButtonGroups for selecting website / selecting product
    private ButtonGroup selectWebsiteButtonGroup, selectProductButtonGroup;

    // JPanels which display the buttons in the button groups for websites / products
    private JPanel selectWebsitePanel, selectProductFromWebsitePanel;

    // JScrollPanes which hold the contents of selecting website / product from website
    private JScrollPane selectWebsiteScroller, selectProductFromWebsiteScroller;

    /**
     * Constructs a new SelectProductFromWebsitesPanel
     */
    public SelectProductFromWebsitesPanel() {
        super();
        setLayout(new GridLayout(0, 2));

        // Construct the button groups
        selectWebsiteButtonGroup = new ButtonGroup();
        selectProductButtonGroup = new ButtonGroup();

        // Panel where users pick which website to list products for
        selectWebsitePanel = new JPanel();
        selectWebsitePanel.setLayout(new GridLayout(0, 1));
        selectWebsiteScroller = new JScrollPane(selectWebsitePanel);  // Make panel "scrollable"

        // Panel where users pick which product from chosen website to graph
        selectProductFromWebsitePanel = new JPanel();
        selectProductFromWebsitePanel.setLayout(new GridLayout(0, 1));
        selectProductFromWebsiteScroller = new JScrollPane(selectProductFromWebsitePanel);  // Make panel "scrollable"

        // Add both "scrollable" sub-panels to this main panel
        add(selectWebsiteScroller);
        add(selectProductFromWebsiteScroller);
    }

    /**
     * Changes the radio buttons associated with the select website button group
     * @param newWebsites list of the radio buttons to use as the website choices
     */
    protected void changeSelectWebsiteButtonGroupButtons(List<JRadioButton> newWebsites) {
        if(newWebsites != null && newWebsites.size() > 0) {
            // Remove old contents of panel
            removeAll();

            // Set up new scroller with new buttons
            selectWebsiteButtonGroup = new ButtonGroup();
            selectWebsitePanel = new JPanel();
            selectWebsitePanel.setLayout(new GridLayout(0, 1));
            selectWebsiteScroller = new JScrollPane(selectWebsitePanel);  // Make panel "scrollable"

            // Add new buttons
            for(JRadioButton button : newWebsites) {
                selectWebsiteButtonGroup.add(button);
                selectWebsitePanel.add(button);
            }

            // Add back the two main panels
            add(selectWebsiteScroller);
            add(selectProductFromWebsiteScroller);
        }
    }

    /**
     * Changes the radio buttons associated with the select product from website button group
     * @param newProducts list of the radio buttons to use as the product choices
     */
    protected void changeSelectProductButtonGroupButtons(List<JRadioButton> newProducts) {
        if(newProducts != null && newProducts.size() > 0) {
            // Remove old contents of panel
            removeAll();

            // Set up new scroller with new buttons
            selectProductButtonGroup = new ButtonGroup();
            selectProductFromWebsitePanel = new JPanel();
            selectProductFromWebsitePanel.setLayout(new GridLayout(0, 1));
            selectProductFromWebsiteScroller = new JScrollPane(selectProductFromWebsitePanel);  // Make panel "scrollable"

            // Add new buttons
            for(JRadioButton button : newProducts) {
                selectProductButtonGroup.add(button);
                selectProductFromWebsitePanel.add(button);
            }

            // Add back the two main panels
            add(selectWebsiteScroller);
            add(selectProductFromWebsiteScroller);
        }
    }
}
