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
        String currentCoordinates = "(" + x + ", " + y + ")";
        g.drawString(currentCoordinates, 100, 100);
        // TODO implement drawing actual graph
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
