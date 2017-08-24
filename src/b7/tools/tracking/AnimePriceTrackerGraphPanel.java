package b7.tools.tracking;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * JPanel that draws a basic GUI for drawing
 * Product object information onto a line graph
 * to show price history graph
 */
public class AnimePriceTrackerGraphPanel extends JPanel {

    // SelectProductFromWebsitesPanel used to select a product to graph
    private SelectProductFromWebsitesPanel selectProductFromWebsitesPanel;

    // LineGraphPanel used to draw product graphs
    private ProductLineGraphPanel productLineGraphPanel;

    // Message label for giving messages to user
    private JLabel messageLabel;

    /**
     * Constructs a new AnimePriceTrackerGraphPanel
     */
    public AnimePriceTrackerGraphPanel() {
        super();
        setLayout(new BorderLayout());

        // Make JPanel to hold main components (interaction objects / graphing area)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 2));

        // Make selection panels
        JPanel mainSelectProductFromWebsitesPanel = new JPanel();
        mainSelectProductFromWebsitesPanel.setLayout(new BorderLayout());
        JLabel selectProductFromWebsitesLabel = new JLabel("Select product from website(s)");
        selectProductFromWebsitesPanel = new SelectProductFromWebsitesPanel();
        mainSelectProductFromWebsitesPanel.add(selectProductFromWebsitesLabel, BorderLayout.NORTH);
        mainSelectProductFromWebsitesPanel.add(selectProductFromWebsitesPanel, BorderLayout.CENTER);

        // Make graphing panel that graphs last-chosen product
        JPanel mainProductLineGraphPanel = new JPanel();
        mainProductLineGraphPanel.setLayout(new BorderLayout());
        JLabel productLineGraphLabel = new JLabel("View graph of product price history");
        productLineGraphPanel = new ProductLineGraphPanel();
        mainProductLineGraphPanel.add(productLineGraphLabel, BorderLayout.NORTH);
        mainProductLineGraphPanel.add(productLineGraphPanel, BorderLayout.CENTER);

        // Add main components to main panel
        mainPanel.add(mainSelectProductFromWebsitesPanel);
        mainPanel.add(mainProductLineGraphPanel);

        // Make message label
        messageLabel = new JLabel("Select a website to get products from");
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);
    }

    /**
     * Sets the Product being graphed by the ProductLineGraphPanel to be the given product
     * @param product the new Product to graph
     */
    protected void changeProductLineGraphPanelProduct(Product product) {
        productLineGraphPanel.setCurrentProduct(product);
    }

    /**
     * Changes the radio buttons associated with the select website button group on the select product from websites panel
     * @param newWebsites list of the radio buttons to use as the new website choices
     */
    protected void changeSelectWebsiteButtonGroupButtons(java.util.List<JRadioButton> newWebsites) {
        selectProductFromWebsitesPanel.changeSelectWebsiteButtonGroupButtons(newWebsites);
    }

    /**
     * Changes the radio buttons associated with the select product from website button group on the select product from websites panel
     * @param newProducts list of the radio buttons to use as the new product choices
     */
    protected void changeSelectProductButtonGroupButtons(List<JRadioButton> newProducts) {
        selectProductFromWebsitesPanel.changeSelectProductButtonGroupButtons(newProducts);
    }
}
