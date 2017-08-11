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
        String priceDateInfoStartDate = priceDateInfo.getStartDate();
        try {
            priceDateInfo.setStartDate("12.2.17");
            fail("Bad date did throw exception");
        }
        catch(Exception ex) {
            assertEquals(priceDateInfoStartDate, priceDateInfo.getStartDate());
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

    @Test
    public void testSetEndDateBadInput() {
        String startDate = "2017-08-08";
        String newEndDate = "2017-08-07";
        PriceDateInfo priceDateInfo = new PriceDateInfo(startDate, 0);
        try {
            priceDateInfo.setEndDate(newEndDate);
            fail();
        }
        catch(Exception ex) {
            assertEquals(startDate, priceDateInfo.getEndDate());
        }
    }

    @Test
    public void testSetEndDateGoodInput() {
        String startDate = "2017-08-08";
        String newEndDate = "2017-08-09";
        PriceDateInfo priceDateInfo = new PriceDateInfo(startDate, 0);
        try {
            priceDateInfo.setEndDate(newEndDate);
            assertEquals(newEndDate, priceDateInfo.getEndDate());
        }
        catch(Exception ex) {
            fail();
        }
    }

    @Test
    public void testSetPriceBadInput() {
        double priceToSet = -10.33;
        PriceDateInfo priceDateInfo = new PriceDateInfo();
        double previousPrice = priceDateInfo.getPrice();

        try {
            priceDateInfo.setPrice(priceToSet);
            fail();
        }
        catch(Exception ex) {
            assertTrue(Math.abs(previousPrice - priceDateInfo.getPrice()) < 0.00001);
        }
    }

    @Test
    public void testSetPriceGoodInput() {
        double priceToSet = 14.99;
        double initialPrice = 6.99;
        PriceDateInfo priceDateInfo = new PriceDateInfo(initialPrice);

        try {
            priceDateInfo.setPrice(priceToSet);
            boolean priceIsStillInitialPrice = initialPrice == priceDateInfo.getPrice();
            boolean priceIsPriceToSet = priceToSet == priceDateInfo.getPrice();
            assertFalse(priceIsStillInitialPrice);
            assertTrue(priceIsPriceToSet);
        }
        catch(Exception ex) {
            fail();
        }
    }

    @Test
    public void testGetDateOffset1() {
        String currentDate = "2017-02-28";
        String expectedOffsetDate = "2017-03-01";
        String offsetDate = PriceDateInfo.getDateOffset(currentDate, 1);
        assertEquals(expectedOffsetDate, offsetDate);
    }

    @Test
    public void testGetDateOffset2() {
        long currentTimeInMillis = System.currentTimeMillis();
        final long MILLISECONDS_IN_A_DAY = 1000L * 60 * 60 * 24;  // 1000 ms/s * 60 s/m * 60 m/h * 24 h/day
        long nextDayTimeInMillis = currentTimeInMillis + MILLISECONDS_IN_A_DAY;
        String currentDate = PriceDateInfo.getDateString(currentTimeInMillis);
        String nextDate = PriceDateInfo.getDateString(nextDayTimeInMillis);
        String nextDayFromGetDateOffset = PriceDateInfo.getDateOffset(currentDate, 1);
        assertEquals(nextDate, nextDayFromGetDateOffset);
    }

    @Test
    public void testGetDateOffset3() {
        String currentDate = "2017-12-31";
        String expectedOffsetDate = "2017-12-25";
        String offsetDate = PriceDateInfo.getDateOffset(currentDate, -6);
        assertEquals(expectedOffsetDate, offsetDate);
    }

    @Test
    public void testGetDateOffset4() {
        String currentDate = "2017-01-05";
        String expectedOffsetDate = "2017-01-09";
        String offsetDate = PriceDateInfo.getDateOffset(currentDate, 4);
        assertEquals(expectedOffsetDate, offsetDate);
    }

    @Test
    public void testGetDateOffset5() {
        String currentDate = "2017-01-05";
        String expectedOffsetDate = "2016-12-31";
        String offsetDate = PriceDateInfo.getDateOffset(currentDate, -5);
        assertEquals(expectedOffsetDate, offsetDate);
    }

    @Test
    public void testEquals() {
        PriceDateInfo priceDateInfo1 = new PriceDateInfo("2017-08-08", "2017-08-10", 19.99);
        PriceDateInfo priceDateInfo2 = new PriceDateInfo("2017-08-08", "2017-08-10", 19.99);
        PriceDateInfo priceDateInfo3 = new PriceDateInfo("2017-08-08", "2017-08-10", 30.00);

        assertTrue(priceDateInfo1.equals(priceDateInfo2));
        assertFalse(priceDateInfo1.equals(priceDateInfo3));
        assertFalse(priceDateInfo2.equals(new Object()));
    }

    @Test
    public void testGetFormattedPrice() {
        PriceDateInfo priceDateInfo = new PriceDateInfo("2017-08-01", "2017-08-03", 23);

        assertEquals("23.00", priceDateInfo.getFormattedPrice(null));
        assertEquals("$ 23.00", priceDateInfo.getFormattedPrice("$"));
    }
}
