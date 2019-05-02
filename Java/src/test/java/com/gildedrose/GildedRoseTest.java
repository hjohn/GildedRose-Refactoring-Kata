package com.gildedrose;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GildedRoseTest {

    @Test
    public void shouldPassTestCases() {
      // Functional versions of the desired behaviour:
      List<TestCase> testCases = Arrays.asList(
        new TestCase(
          "Sulfuras, Hand of Ragnaros",
          (q, sellIn, days) -> q,
          (sellIn, days) -> sellIn
        ),
        new TestCase(
          "Aged Brie",
          (q, sellIn, days) -> Math.min(50, q + days + (days > sellIn ? days - sellIn : 0)),
          (sellIn, days) -> sellIn - days
        ),
        new TestCase(
          "Backstage passes to a TAFKAL80ETC concert",
          (q, sellIn, days) -> {
            int daysToGo = sellIn - days;
            int daysPassedBelow10 = daysToGo <= 10 ? Math.min(10, sellIn) - daysToGo : 0;
            int daysPassedBelow5 = daysToGo <= 5 ? Math.min(5, sellIn) - daysToGo : 0;

            return daysToGo < 0 ? 0 : Math.min(50, q + days + daysPassedBelow10 + daysPassedBelow5);
          },
          (sellIn, days) -> sellIn - days
        ),
        new TestCase(
          "Regular Item",
          (q, sellIn, days) -> Math.max(0, q - days - Math.max(0, days - sellIn)),
          (sellIn, days) -> sellIn - days
        ),
        new TestCase(
          "Conjured",
          (q, sellIn, days) -> Math.max(0, q - (days + Math.max(0, days - sellIn)) * 2),
          (sellIn, days) -> sellIn - days
        )
      );

      // Tests everything with upto 99 sell days and 0 to 50 quality:
      for(int startSellIn = 0; startSellIn < 100; startSellIn++) {
        for(int startQuality = 0; startQuality <= 50; startQuality++) {
          for(TestCase testCase : testCases) {
            String testCaseName = testCase.name + ", startQ=" + startQuality + ", startSellIn=" + startSellIn;
            Item item = new Item(testCase.name, startSellIn, startQuality);

            GildedRose rose = new GildedRose(new Item[] { item });

            for(int daysPassed = 0; daysPassed < 100; daysPassed++) {
              assertEquals(
                "Quality incorrect for [" + testCaseName + "] after " + daysPassed + " days passed",
                testCase.expectedQuality.afterDays(startQuality, startSellIn, daysPassed),
                item.quality
              );

              assertEquals(
                "SellIn incorrect for [" + testCaseName + "] after " + daysPassed + " days passed",
                testCase.expectedSellIn.afterDays(startSellIn, daysPassed),
                item.sellIn
              );


              rose.updateQuality();
            }
          }
        }
      }
    }

    @FunctionalInterface
    private interface ExpectedQuality {
      /**
       * Returns expected quality after the given number of days.
       */
      int afterDays(int quality, int sellIn, int days);
    }

    @FunctionalInterface
    private interface ExpectedSellIn {
      /**
       * Returns expected sell-in time after the given number of days.
       */
      int afterDays(int sellIn, int days);
    }

    private static class TestCase {
      private final String name;
      private final ExpectedQuality expectedQuality;
      private final ExpectedSellIn expectedSellIn;

      public TestCase(String itemName, ExpectedQuality expectedQuality, ExpectedSellIn expectedSellIn) {
        this.name = itemName;
        this.expectedQuality = expectedQuality;
        this.expectedSellIn = expectedSellIn;
      }
    }
}
