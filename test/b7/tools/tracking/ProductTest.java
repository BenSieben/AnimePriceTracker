package b7.tools.tracking;

import org.junit.*;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Class to run JUnit tests on the
 * b7.tools.tracking.Product
 */
public class ProductTest {

    // Test that we cannot externally modify the productPriceHistory by adding new elements manually
    @Test
    public void testProductToString() {
        Product product = new Product("Sample Product", "http://www.example.com/products/sample-product");

        PriceDateInfo samplePriceDateInfo = new PriceDateInfo("2017-08-08", "2017-08-09", 19.99);
        product.addNewPriceDateInfo(samplePriceDateInfo);
        String oldProductString = product.toString();

        List<PriceDateInfo> productPriceHistory = product.getPriceHistory();
        productPriceHistory.add(new PriceDateInfo("2017-08-15", "2017-08-20", 19.99));
        String newProductString = product.toString();;

        assertEquals(oldProductString, newProductString);
    }

    @Test
    public void testProduct1() {
        // Make a product and test a basic PriceDateInfo merge
        Product product = new Product("Sample Product", "http://www.example.com/products/sample-product");

        PriceDateInfo samplePriceDateInfo = new PriceDateInfo("2017-08-08", "2017-08-09", 19.99);
        product.addNewPriceDateInfo(samplePriceDateInfo);

        samplePriceDateInfo = new PriceDateInfo("2017-09-08", "2017-09-09", 19.99);
        product.addNewPriceDateInfo(samplePriceDateInfo);

        // Make a product which has the expected results from merge
        Product expectedProduct = new Product("Sample Product", "http://www.example.com/products/sample-product");
        expectedProduct.addNewPriceDateInfo(new PriceDateInfo("2017-08-08", "2017-09-09", 19.99));

        // Check that the two products are equivalent
        assertEquals(expectedProduct.toString(), product.toString());
    }
}
