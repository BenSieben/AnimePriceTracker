package b7.tools.tracking;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * Class to run JUnit tests on the
 * b7.tools.tracking.PriceDateInfo
 */
public class PriceDateInfoTest {

    /**
     * Test to make sure the getCurrentDateString()
     * method from the PriceDate functions correctly
     */
    @Test
    public void testGetCurrentDateString() {
        String currentDate = PriceDateInfo.getCurrentDateString();
        assertNotNull(currentDate);
    }

    @Test
    public void testGetDateString() {
        long millisecondsSince19700101 = 0;
        String getDateStringResult = PriceDateInfo.getDateString(millisecondsSince19700101);
        // Due to time zones, the result could be last day of 1969 or first dat of 1970
        String expectedResult1 = "1969-12-31";
        String expectedResult2 = "1970-01-01";
        boolean foundMatch;  // Flag to let us know if we got a match or not

        // Test the result with the expected results
        foundMatch = expectedResult1.equals(getDateStringResult);
        if(!foundMatch) {
            foundMatch = expectedResult2.equals(getDateStringResult);
        }
        assertTrue(foundMatch);

    }

    @Test
    public void testSetStartDateBadInput() {
        PriceDateInfo priceDateInfo = new PriceDateInfo();
        try {
            priceDateInfo.setStartDate("12.2.17");
            fail("Bad date did throw exception");
        }
        catch(Exception ex) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetStartDateGoodInput() {
        PriceDateInfo priceDateInfo = new PriceDateInfo();
        try {
            String newDate = "2017-08-07";
            priceDateInfo.setStartDate(newDate);
            assertEquals(newDate, priceDateInfo.getStartDate());
        }
        catch(Exception ex) {
            fail();
        }
    }
}
