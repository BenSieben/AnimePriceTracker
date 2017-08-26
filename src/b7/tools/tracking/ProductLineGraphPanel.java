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

        // Draw actual graph
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
        Graphics2D g2d = (Graphics2D)g;
        Stroke regularStroke = new BasicStroke(2.0f);  // 2px thick solid line
        // Create dotted kind of stroke to use when partitioning different price date info objects in current product history
        final float[] dashArray = {10.0f};
        Stroke dottedStroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dashArray, 0.0f);
        g2d.setStroke(regularStroke);

        // Some constants for where x-axis / y-axis start and stop drawing
        final int X_AXIS_START_X = 60;
        final int X_AXIS_END_X = 540;

        final int Y_AXIS_START_Y = 60;
        final int Y_AXIS_END_Y = 540;

        final int GRAPH_WIDTH = X_AXIS_END_X - X_AXIS_START_X;
        final int GRAPH_HEIGHT = Y_AXIS_END_Y - Y_AXIS_START_Y;

        // Where we draw tick marks on y-axis for normal product
        final int TOP_PRICE_TICK_Y = Y_AXIS_START_Y + 20;
        final int BOT_PRICE_TICK_Y = Y_AXIS_END_Y - 20;

        // Where we draw tick marks on x-axis for normal product
        final int START_DATE_TICK_X = X_AXIS_START_X + 20;
        final int END_DATE_TICK_X = X_AXIS_END_X - 20;

        // Distance between the ticks in horizontal and vertical measurements
        final int TICK_WIDTH = END_DATE_TICK_X - START_DATE_TICK_X;
        final int TICK_HEIGHT = BOT_PRICE_TICK_Y - TOP_PRICE_TICK_Y;

        // Constant for line color on line graph
        final Color lineColor = new Color(10, 93, 201);
        final Color dashedLineColor = new Color(234, 115, 4);
        Color originalColor = g.getColor();

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
            g.drawString(lowestPriceString,
                    (int)((X_AXIS_START_X - 55) * widthFactor),
                    (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2 + 5) * heightFactor));
            g.drawLine((int)((X_AXIS_START_X - 5) * widthFactor),
                    (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2) * heightFactor),
                    (int)((X_AXIS_START_X + 5) * widthFactor),
                    (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2) * heightFactor));

            g.drawString(earliestDate,
                    (int)((X_AXIS_START_X + GRAPH_WIDTH / 2 - 35) * widthFactor),
                    (int)((Y_AXIS_END_Y + 20) * heightFactor));
            g.drawLine((int)((X_AXIS_START_X + GRAPH_WIDTH / 2) * widthFactor),
                    (int)((Y_AXIS_END_Y - 5) * heightFactor),
                    (int)((X_AXIS_START_X + GRAPH_WIDTH / 2) * widthFactor),
                    (int)((Y_AXIS_END_Y + 5) * heightFactor));

            g.setColor(lineColor);
            g.drawLine((int)((X_AXIS_START_X + GRAPH_WIDTH / 2) * widthFactor),
                    (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2) * heightFactor),
                    (int)((X_AXIS_START_X + GRAPH_WIDTH / 2) * widthFactor),
                    (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2) * heightFactor));

            // Reset graphics back to original font / color
            g.setFont(originalFont);
            g.setColor(originalColor);
            return;
        }

        if(lowestPrice == highestPrice) {
            // Handle special case where lowest and highest price are the same
            // Draw y-axis tick mark for the one price the product has
            g.drawString(lowestPriceString,
                    (int)((X_AXIS_START_X - 55) * widthFactor),
                    (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2 + 5) * heightFactor));
            g.drawLine((int)((X_AXIS_START_X - 5) * widthFactor),
                    (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2) * heightFactor),
                    (int)((X_AXIS_START_X + 5) * widthFactor),
                    (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2) * heightFactor));

            // Draw x-axis tick marks for start / end dates
            g.drawString(earliestDate, (int)((X_AXIS_START_X - 15) * widthFactor), (int)((Y_AXIS_END_Y + 20) * heightFactor));
            g2d.drawLine((int)(START_DATE_TICK_X * widthFactor), (int)((Y_AXIS_END_Y - 5) * heightFactor), (int)(START_DATE_TICK_X * widthFactor), (int)((Y_AXIS_END_Y + 5) * heightFactor));

            g.drawString(latestDate, (int)((X_AXIS_END_X - 55) * widthFactor), (int)((Y_AXIS_END_Y + 20) * heightFactor));
            g2d.drawLine((int)(END_DATE_TICK_X * widthFactor), (int)((Y_AXIS_END_Y - 5) * heightFactor), (int)(END_DATE_TICK_X * widthFactor), (int)((Y_AXIS_END_Y + 5) * heightFactor));

            // Draw line graph (single, flat line)
            g.setColor(lineColor);
            g2d.drawLine((int)(START_DATE_TICK_X * widthFactor), (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2) * heightFactor), (int)(END_DATE_TICK_X * widthFactor), (int)((Y_AXIS_START_Y + GRAPH_HEIGHT / 2) * heightFactor));

            // Reset graphics back to original font / color
            g.setFont(originalFont);
            g.setColor(originalColor);
            return;
        }

        // Draw lowest / highest price and date values onto graph
        // Normal case: multiple prices and multiple dates

        // Find difference in lowest / highest prices and dates (to scale our line graph drawing accurately)
        double totalPriceDifference = highestPrice - lowestPrice;
        long earliestDateInMillis = PriceDateInfo.findMillisFromDateString(earliestDate);
        long latestDateInMillis = PriceDateInfo.findMillisFromDateString(latestDate);

        final long MILLISECONDS_IN_A_DAY = 1000L * 60 * 60 * 24;  // 1000 ms/s * 60 s/m * 60 m/h * 24 h/day
        long totalDateDifferenceInMillis = latestDateInMillis - earliestDateInMillis + MILLISECONDS_IN_A_DAY;  // Add milliseconds in a day because last day goes up till just before midnight next day

        // Draw x-axis and y-axis tick marks denoting highest / lowest prices and dates for the current product
        g.drawString(lowestPriceString, (int)((X_AXIS_START_X - 55) * widthFactor), (int)((Y_AXIS_END_Y - 15) * heightFactor));
        g2d.drawLine((int)((X_AXIS_START_X - 5) * widthFactor), (int)(BOT_PRICE_TICK_Y * heightFactor), (int)((X_AXIS_START_X + 5) * widthFactor), (int)(BOT_PRICE_TICK_Y * heightFactor));

        g.drawString(highestPriceString, (int)((X_AXIS_START_X - 55) * widthFactor), (int)((Y_AXIS_START_Y + 25) * heightFactor));
        g2d.drawLine((int)((X_AXIS_START_X - 5) * widthFactor), (int)(TOP_PRICE_TICK_Y * heightFactor), (int)((X_AXIS_START_X + 5) * widthFactor), (int)(TOP_PRICE_TICK_Y * heightFactor));

        // Make the "ticks" for the dates wider, as one date is not just a point in time, but a range in time
        g.drawString(earliestDate, (int)((X_AXIS_START_X - 15) * widthFactor), (int)((Y_AXIS_END_Y + 20) * heightFactor));
        g2d.drawLine((int)(START_DATE_TICK_X * widthFactor), (int)((Y_AXIS_END_Y - 5) * heightFactor), (int)(START_DATE_TICK_X * widthFactor), (int)((Y_AXIS_END_Y + 5) * heightFactor));

        // Use MILLISECONDS_IN_A_DAY to find tick mark for start of next day after starting day
        int earliestDateEndTickX = (int)((START_DATE_TICK_X + ((1.0 * MILLISECONDS_IN_A_DAY / totalDateDifferenceInMillis) * TICK_WIDTH)) * widthFactor);
        g2d.drawLine(earliestDateEndTickX, (int)((Y_AXIS_END_Y - 5) * heightFactor), earliestDateEndTickX, (int)((Y_AXIS_END_Y + 5) * heightFactor));

        g.drawString(latestDate, (int)((X_AXIS_END_X - 55) * widthFactor), (int)((Y_AXIS_END_Y + 20) * heightFactor));
        g2d.drawLine((int)(END_DATE_TICK_X * widthFactor), (int)((Y_AXIS_END_Y - 5) * heightFactor), (int)(END_DATE_TICK_X * widthFactor), (int)((Y_AXIS_END_Y + 5) * heightFactor));

        // Use MILLISECONDS_IN_A_DAY to find tick mark for start of next day after starting day
        int latestDateEndTickX = (int)((END_DATE_TICK_X - ((1.0 * MILLISECONDS_IN_A_DAY / totalDateDifferenceInMillis) * TICK_WIDTH)) * widthFactor);
        g2d.drawLine(latestDateEndTickX, (int)((Y_AXIS_END_Y - 5) * heightFactor), latestDateEndTickX, (int)((Y_AXIS_END_Y + 5) * heightFactor));

        // Loop through all price date info objects in current product history to graph them
        for (int i = 0; i < currentProductHistory.size(); i++) {
            PriceDateInfo currentInfo = currentProductHistory.get(i);

            // Find y-coordinate to draw current info at
            double currentPriceDifference = currentInfo.getPrice() - lowestPrice;
            int lineYCoordinate = (int)((BOT_PRICE_TICK_Y - ((currentPriceDifference / totalPriceDifference) * TICK_HEIGHT)) * heightFactor);

            // Find x-coordinates to draw current info at
            long currentStartDateDifference = PriceDateInfo.findMillisFromDateString(currentInfo.getStartDate()) - earliestDateInMillis;
            long currentEndDateDifference = PriceDateInfo.findMillisFromDateString(currentInfo.getEndDate()) - earliestDateInMillis + MILLISECONDS_IN_A_DAY;  // Add milliseconds in a day because last day goes up till just before midnight next day
            int lineXStartCoordinate = (int)((START_DATE_TICK_X + ((1.0 * currentStartDateDifference / totalDateDifferenceInMillis) * TICK_WIDTH)) * widthFactor);
            int lineXEndCoordinate = (int)((START_DATE_TICK_X + ((1.0 * currentEndDateDifference / totalDateDifferenceInMillis) * TICK_WIDTH)) * widthFactor);

            g.setColor(lineColor);
            g2d.drawLine(lineXStartCoordinate, lineYCoordinate, lineXEndCoordinate, lineYCoordinate);

            if(i != currentProductHistory.size() - 1) {
                // Draw vertical line to partition different price date info segments
                g2d.setStroke(dottedStroke);
                g.setColor(dashedLineColor);
                g2d.drawLine(lineXEndCoordinate, (int)(TOP_PRICE_TICK_Y * heightFactor), lineXEndCoordinate, (int)(BOT_PRICE_TICK_Y * heightFactor));

                g2d.setStroke(regularStroke);
                g.setColor(originalColor);
            }
        }

        // Reset graphics back to original font / color
        g.setFont(originalFont);
        g.setColor(originalColor);
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
