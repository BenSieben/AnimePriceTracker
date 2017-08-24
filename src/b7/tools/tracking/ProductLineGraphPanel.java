package b7.tools.tracking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Class for illustrating a given Product
 * onto a line graph (JPanel)
 */
public class ProductLineGraphPanel extends JPanel implements MouseMotionListener {

    // The product to graph
    private Product currentProduct;

    private int x;
    private int y;

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

        // Original pixel size of window (to scale the graph to panel re-sizing)
        final double defaultWidth = 629.0;
        final double defaultHeight = 621.0;

        // Find current width / height of panel to determine scaling factors for width / height
        double widthFactor = getWidth() / defaultWidth;
        double heightFactor = getHeight() / defaultHeight;

        // TODO implement drawing actual graph
        String currentCoordinates = "(" + x + ", " + y + ")";
        g.drawString(currentCoordinates, (int)(100 * widthFactor), (int)(100 * heightFactor));
        String productName = "No product selected";
        if(currentProduct != null) {
            productName = currentProduct.getProductName();
        }
        g.drawString(productName, (int)(100 * widthFactor), (int)(200 * heightFactor));
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
