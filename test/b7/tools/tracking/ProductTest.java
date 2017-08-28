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
        expectedProduct.addNewPriceDateInfo(new PriceDateInfo("2017-08-08", "2017-08-09", 19.99));
        expectedProduct.addNewPriceDateInfo(new PriceDateInfo("2017-09-08", "2017-09-09", 19.99));

        // Check that the two products are equivalent
        assertEquals(expectedProduct.toString(), product.toString());
    }

    @Test
    public void testAddNewPriceDateInfo1() {
        String productName = "Example Product";
        String productURL = "http://www.example.com/products/sample-product";
        Product product = new Product(productName, productURL);

        // Create some PriceDateInfo objects to add to the Product
        PriceDateInfo p1 = new PriceDateInfo("2017-08-01", "2017-08-02", 9.99);
        PriceDateInfo p2 = new PriceDateInfo("2017-08-02", "2017-08-03", 9.99);

        // Add all the PriceDateInfo objects to the Product
        product.addNewPriceDateInfo(p1);
        product.addNewPriceDateInfo(p2);

        // Create expected Product
        Product expectedProduct = new Product(productName, productURL);
        expectedProduct.addNewPriceDateInfo(new PriceDateInfo("2017-08-01", "2017-08-03", 9.99));

        // Check expected Product and actual Product are the same
        assertEquals(expectedProduct.toString(), product.toString());
    }

    @Test
    public void testAddNewPriceDateInfo2() {
        String productName = "Example Product";
        String productURL = "http://www.example.com/products/sample-product";
        Product product = new Product(productName, productURL);

        // Create some PriceDateInfo objects to add to the Product
        PriceDateInfo p1 = new PriceDateInfo("2017-08-01", "2017-08-02", 9.99);
        PriceDateInfo p2 = new PriceDateInfo("2017-08-02", "2017-08-03", 15.00);

        // Add all the PriceDateInfo objects to the Product
        product.addNewPriceDateInfo(p1);
        product.addNewPriceDateInfo(p2);

        // Create expected Product
        Product expectedProduct = new Product(productName, productURL);
        expectedProduct.addNewPriceDateInfo(new PriceDateInfo("2017-08-01", "2017-08-01", 9.99));
        expectedProduct.addNewPriceDateInfo(new PriceDateInfo("2017-08-02", "2017-08-03", 15.00));

        // Check expected Product and actual Product are the same
        assertEquals(expectedProduct.toString(), product.toString());
    }

    @Test
    public void testAddNewPriceDateInfo3() {
        String productName = "Example Product";
        String productURL = "http://www.example.com/products/sample-product";
        Product product = new Product(productName, productURL);

        // Create some PriceDateInfo objects to add to the Product
        PriceDateInfo p1 = new PriceDateInfo("2017-08-01", "2017-08-05", 9.99);
        PriceDateInfo p2 = new PriceDateInfo("2017-08-09", "2017-08-12", 9.99);

        // Add all the PriceDateInfo objects to the Product
        product.addNewPriceDateInfo(p1);
        product.addNewPriceDateInfo(p2);

        // Create expected Product
        Product expectedProduct = new Product(productName, productURL);
        expectedProduct.addNewPriceDateInfo(new PriceDateInfo("2017-08-01", "2017-08-05", 9.99));
        expectedProduct.addNewPriceDateInfo(new PriceDateInfo("2017-08-09", "2017-08-12", 9.99));

        // Check expected Product and actual Product are the same
        assertEquals(expectedProduct.toString(), product.toString());
        assertTrue(expectedProduct.getPriceHistory().size() == 2);
    }

    @Test
    public void testAddNewPriceDateInfo4() {
        String productName = "Example Product";
        String productURL = "http://www.example.com/products/sample-product";
        Product product = new Product(productName, productURL);

        // Create some PriceDateInfo objects to add to the Product
        PriceDateInfo p1 = new PriceDateInfo("2017-08-01", "2017-08-05", 9.99);
        PriceDateInfo p2 = new PriceDateInfo("2017-08-09", "2017-08-12", 9.99);
        PriceDateInfo p3 = new PriceDateInfo("2017-08-13", "2017-08-19", 9.99);

        // Add all the PriceDateInfo objects to the Product
        product.addNewPriceDateInfo(p1);
        product.addNewPriceDateInfo(p2);
        product.addNewPriceDateInfo(p3);

        // Create expected Product
        Product expectedProduct = new Product(productName, productURL);
        expectedProduct.addNewPriceDateInfo(new PriceDateInfo("2017-08-01", "2017-08-05", 9.99));
        expectedProduct.addNewPriceDateInfo(new PriceDateInfo("2017-08-09", "2017-08-19", 9.99));

        // Check expected Product and actual Product are the same
        assertEquals(expectedProduct.toString(), product.toString());
        assertTrue(expectedProduct.getPriceHistory().size() == 2);
    }
}
