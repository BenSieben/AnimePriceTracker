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

        List<JRadioButton> sampleWebsites = new ArrayList<JRadioButton>();
        List<JRadioButton> sampleProducts = new ArrayList<JRadioButton>();
        for(int i = 0; i < 2; i++) {
            JRadioButton button = new JRadioButton("website " + i);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Change");
                    List<JRadioButton> newList = new ArrayList<>();
                    newList.add(new JRadioButton("changed"));
                    changeSelectWebsiteButtonGroupButtons(newList);
                }
            });
            sampleWebsites.add(button);
        }
        for(int i = 0; i < 100; i++) {
            sampleProducts.add(new JRadioButton("product " + i));
        }
        changeSelectWebsiteButtonGroupButtons(sampleWebsites);
        changeSelectProductButtonGroupButtons(sampleProducts);
    }

    /**
     * Changes the radio buttons associated with the select website button group
     * @param newWebsites list of the radio buttons to use as the website choices
     */
    protected void changeSelectWebsiteButtonGroupButtons(List<JRadioButton> newWebsites) {
        // TODO properly swap out buttons for new one (or come up with alternate GUI strategy)
        if(newWebsites != null && newWebsites.size() > 0) {
            // Remove old contents
            selectWebsiteScroller.remove(selectWebsitePanel);
            remove(selectWebsiteScroller);
            remove(selectProductFromWebsiteScroller);
            selectWebsiteButtonGroup = new ButtonGroup();

            // Set up new scroller with new buttons
            selectWebsitePanel = new JPanel();
            selectWebsitePanel.setLayout(new GridLayout(0, 1));
            selectWebsiteScroller = new JScrollPane(selectWebsitePanel);  // Make panel "scrollable"

            // Remove old buttons
            /*Enumeration<AbstractButton> oldButtons = selectWebsiteButtonGroup.getElements();
            while(oldButtons.hasMoreElements()) {
                JRadioButton currentButton = (JRadioButton)oldButtons.nextElement();
                System.out.println("OK");
                selectWebsiteButtonGroup.remove(currentButton);
                selectWebsitePanel.remove(currentButton);
            }*/

            // Add new buttons
            for(JRadioButton button : newWebsites) {
                selectWebsiteButtonGroup.add(button);
                selectWebsitePanel.add(button);
            }

            // Add back the two main panels
            add(selectWebsiteScroller);
            add(selectProductFromWebsiteScroller);
            requestFocus();
        }
    }

    /**
     * Changes the radio buttons associated with the select product from website button group
     * @param newProducts list of the radio buttons to use as the product choices
     */
    protected void changeSelectProductButtonGroupButtons(List<JRadioButton> newProducts) {
        if(newProducts != null && newProducts.size() > 0) {
            // Remove old buttons
            Enumeration<AbstractButton> oldButtons = selectProductButtonGroup.getElements();
            while(oldButtons.hasMoreElements()) {
                selectProductButtonGroup.remove(oldButtons.nextElement());
            }

            // Add new buttons
            selectProductButtonGroup.clearSelection();
            for(JRadioButton button : newProducts) {
                selectProductButtonGroup.add(button);
                selectProductFromWebsitePanel.add(button);
            }

            repaint();
        }
    }
}
