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
            drawGraph(g, widthFactor, heightFactor);
        }
    }

    /**
     * Draws the line graph for the currentProduct
     * @param g the Graphics object from paintComponent
     * @param widthFactor the width factor computed in paintComponent
     * @param heightFactor the height factor computed in paintComponent
     */
    private void drawGraph(Graphics g, double widthFactor, double heightFactor) {
        // Change font of Graphics
        Font originalFont = g.getFont();
        Font newFont = new Font("Arial", Font.BOLD, (int)(12 * heightFactor));
        g.setFont(newFont);

        // Create Graphics2D object from Graphics g
        // Set 5 px thickness
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(2));

        // Some constants for where x-axis / y-axis start and stop drawing
        final int X_AXIS_START_X = 60;
        final int X_AXIS_END_X = 540;
        final int Y_AXIS_START_Y = 60;
        final int Y_AXIS_END_Y = 540;
        final int GRAPH_WIDTH = X_AXIS_END_X - X_AXIS_START_X;
        final int GRAPH_HEIGHT = Y_AXIS_END_Y - Y_AXIS_START_Y;

        // Draw x-axis
        g2d.drawLine((int)(X_AXIS_START_X * widthFactor), (int)(Y_AXIS_END_Y * heightFactor), (int)(X_AXIS_END_X * widthFactor), (int)(Y_AXIS_END_Y * heightFactor));
        g.drawString("Date", (int)(550 * widthFactor), (int)(545 * heightFactor));

        // Draw y-axis
        g2d.drawLine((int)(X_AXIS_START_X * widthFactor), (int)(Y_AXIS_START_Y * heightFactor), (int)(X_AXIS_START_X * widthFactor), (int)(Y_AXIS_END_Y * heightFactor));
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

        if(earliestDate.compareTo(latestDate) == 0) {
            // Handle special case where earliest and latest date are the same (and so price is also the same)
            // Draw x-axis and y-axis tick mark in center of x-axis and y-axis lines
            g.drawLine((int)((X_AXIS_START_X - 5) * widthFactor),
                    (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2) * heightFactor),
                    (int)((X_AXIS_START_X + 5) * widthFactor),
                    (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2) * heightFactor));
            g.drawLine((int)((X_AXIS_START_X + GRAPH_WIDTH / 2) * widthFactor),
                    (int)((Y_AXIS_END_Y - 5) * heightFactor),
                    (int)((X_AXIS_START_X + GRAPH_WIDTH / 2) * widthFactor),
                    (int)((Y_AXIS_END_Y + 5) * heightFactor));
            g.drawLine((int)((X_AXIS_START_X + GRAPH_WIDTH / 2) * widthFactor),
                    (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2) * heightFactor),
                    (int)((X_AXIS_START_X + GRAPH_WIDTH / 2) * widthFactor),
                    (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2) * heightFactor));

            // Reset font back to original font
            g.setFont(originalFont);
            return;
        }

        if(lowestPrice == highestPrice) {
            // TODO Handle special case where lowest and highest price are the same


            // Reset font back to original font
            g.setFont(originalFont);
            return;
        }

        // TODO Draw lowest / highest price and date values onto graph
        // Normal case: multiple prices and multiple dates
        // Draw x-axis and y-axis tick marks denoting highest / lowest prices and dates for the current product
        g.drawString(lowestPriceString, (int)((X_AXIS_START_X - 55) * widthFactor), (int)((Y_AXIS_END_Y - 15) * heightFactor));
        g2d.drawLine((int)((X_AXIS_START_X - 5) * widthFactor), (int)((Y_AXIS_END_Y - 20) * heightFactor), (int)((X_AXIS_START_X + 5) * widthFactor), (int)((Y_AXIS_END_Y - 20) * heightFactor));

        g.drawString(highestPriceString, (int)((X_AXIS_START_X - 55) * widthFactor), (int)((Y_AXIS_START_Y + 25) * heightFactor));
        g2d.drawLine((int)((X_AXIS_START_X - 5) * widthFactor), (int)((Y_AXIS_START_Y + 20) * heightFactor), (int)((X_AXIS_START_X + 5) * widthFactor), (int)((Y_AXIS_START_Y + 20) * heightFactor));

        g.drawString(earliestDate, (int)((X_AXIS_START_X - 15) * widthFactor), (int)((Y_AXIS_END_Y + 20) * heightFactor));
        g2d.drawLine((int)((X_AXIS_START_X + 20) * widthFactor), (int)((Y_AXIS_END_Y - 5) * heightFactor), (int)((X_AXIS_START_X + 20) * widthFactor), (int)((Y_AXIS_END_Y + 5) * heightFactor));

        g.drawString(latestDate, (int)((X_AXIS_END_X - 55) * widthFactor), (int)((Y_AXIS_END_Y + 20) * heightFactor));
        g2d.drawLine((int)((X_AXIS_END_X - 20) * widthFactor), (int)((Y_AXIS_END_Y - 5) * heightFactor), (int)((X_AXIS_END_X - 20) * widthFactor), (int)((Y_AXIS_END_Y + 5) * heightFactor));

        g.setFont(originalFont);
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
