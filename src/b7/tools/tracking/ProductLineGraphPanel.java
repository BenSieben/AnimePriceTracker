package b7.tools.tracking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;

/**
 * Class for illustrating a given Product
 * onto a line graph (JPanel)
 */
public class ProductLineGraphPanel extends JPanel implements MouseMotionListener {

    // The product to graph
    private Product currentProduct;

    // Current coordinates of mouse
    private int x, y;

    /**
     * Constructs a new ProductLineGraphPanel with no product to graph
     */
    public ProductLineGraphPanel() {
        this(null);
    }

    /**
     * Constructs a new ProductLineGraphPanel with the given product to graph
     * @param product the product to graph
     */
    public ProductLineGraphPanel(Product product) {
        super();
        setBackground(Color.LIGHT_GRAY);
        currentProduct = product;
        addMouseMotionListener(this);
    }

    /**
     * Sets the product to graph to be the argument Product
     * @param product the new Product to graph
     */
    public void setCurrentProduct(Product product) {
        currentProduct = product;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Original pixel size of window (to scale the graph to any panel re-sizing)
        final double defaultWidth = 629.0;
        final double defaultHeight = 621.0;

        // Find current width / height of panel to determine scaling factors for width / height
        double widthFactor = getWidth() / defaultWidth;
        double heightFactor = getHeight() / defaultHeight;

        // TODO implement drawing actual graph
        String currentCoordinates = "(" + x + ", " + y + ")";
        g.drawString(currentCoordinates, (int)(10 * widthFactor), (int)(600 * heightFactor));
        String productName = "No product selected";
        if(currentProduct != null) {
            // Max length of what we will draw of title
            final int MAX_TITLE_LENGTH = 65;

            // Retrieve product name and trim it down if it is too long
            productName = currentProduct.getProductName();
            if(productName.length() > MAX_TITLE_LENGTH) {
                productName = productName.substring(0, MAX_TITLE_LENGTH - 3) + "...";  // -3 because we include "..."
            }
        }

        // Draw product title
        Font originalFont = g.getFont();
        Font titleFont = new Font("Arial", Font.BOLD, (int)(20 * heightFactor));
        g.setFont(titleFont);
        g.drawString(productName, (int)(5 * widthFactor), (int)(25 * heightFactor));
        g.setFont(originalFont);

        // Continue drawing graph if current product is not null
        if(currentProduct != null) {
            // Draw the different components of the graph
            drawAxes(g, widthFactor, heightFactor);
        }
    }

    /**
     * Draws the x-axis (dates) and y-axis (prices) for the currentProduct
     * @param g the Graphics object from paintComponent
     * @param widthFactor the width factor computed in paintComponent
     * @param heightFactor the height factor computed in paintComponent
     */
    private void drawAxes(Graphics g, double widthFactor, double heightFactor) {
        // Draw x-axis
        g.drawLine((int)(40 * widthFactor), (int)(540 * heightFactor), (int)(540 * widthFactor), (int)(540 * heightFactor));
        g.drawString("Date", (int)(550 * widthFactor), (int)(545 * heightFactor));

        // Draw y-axis
        g.drawLine((int)(60 * widthFactor), (int)(60 * heightFactor), (int)(60 * widthFactor), (int)(560 * heightFactor));
        g.drawString("Price ($)", (int)(40 * widthFactor), (int)(50 * heightFactor));

        // Compute lowest / highest price and date values
        List<PriceDateInfo> currentProductHistory = currentProduct.getPriceHistory();
        String lowestPriceString = null, highestPriceString = null;
        double lowestPrice = Double.MAX_VALUE;
        double highestPrice = Double.MIN_VALUE;
        for(int i = 0; i < currentProductHistory.size(); i++) {
            PriceDateInfo currentInfo = currentProductHistory.get(i);

            if(lowestPrice > currentInfo.getPrice()) {
                lowestPrice = currentInfo.getPrice();
                lowestPriceString = currentInfo.formattedPrice(null);
            }

            if(highestPrice < currentInfo.getPrice()) {
                highestPrice = currentInfo.getPrice();
                highestPriceString = currentInfo.formattedPrice(null);
            }
        }

        String earliestDate = currentProductHistory.get(0).getStartDate();
        String latestDate = currentProductHistory.get(currentProductHistory.size() - 1).getEndDate();

        // TODO Draw lowest / highest price and date values onto graph
        if(earliestDate.compareTo(latestDate) == 0) {
            // TODO Handle special case where earliest and latest date are the same (and so price is also the same)
            return;
        }

        if(lowestPrice == highestPrice) {
            // TODO Handle special case where lowest and highest price are the same
            return;
        }

        // Normal case: multiple prices and multiple dates
        g.drawString(lowestPriceString, (int)(5 * widthFactor), (int)(540 * heightFactor));
        g.drawLine((int)(55 * widthFactor), (int)(530 * heightFactor), (int)(65 * widthFactor), (int)(530 * heightFactor));

        g.drawString(highestPriceString, (int)(5 * widthFactor), (int)(75 * heightFactor));
        g.drawLine((int)(55 * widthFactor), (int)(70 * heightFactor), (int)(65 * widthFactor), (int)(70 * heightFactor));

        g.drawString(earliestDate, (int)(65 * widthFactor), (int)(560 * heightFactor));
        g.drawLine((int)(70 * widthFactor), (int)(535 * heightFactor), (int)(70 * widthFactor), (int)(545 * heightFactor));

        g.drawString(latestDate, (int)(525 * widthFactor), (int)(560 * heightFactor));
        g.drawLine((int)(530 * widthFactor), (int)(535 * heightFactor), (int)(530 * widthFactor), (int)(545 * heightFactor));
    }

    /**
     * Activated when mouse is moved over panel
     * @param e MouseEvent with information about the trigger
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        this.x = e.getX();
        this.y = e.getY();
        repaint();
    }

    /**
     * Not used; required by MouseMotionListener
     * @param e Not used
     */
    @Override
    public void mouseDragged(MouseEvent e) { }
}
