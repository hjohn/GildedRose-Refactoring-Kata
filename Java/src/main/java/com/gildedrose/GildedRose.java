package com.gildedrose;

import java.util.HashMap;
import java.util.Map;

/*
 * General comment...
 *
 * The original code is a nested mess.  Here is a version that is much easier to extend without
 * introducing side effects.
 */

class GildedRose {
    Item[] items;

    private static final Map<String, ItemUpdater> ITEM_UPDATERS = new HashMap<>();
    private static final ItemUpdater DEFAULT_ITEM_UPDATER;

    static {
      ITEM_UPDATERS.put("Aged Brie", item -> {
        changeQuality(1, item);

        if(item.sellIn <= 0) {
          changeQuality(1, item);  // Possible bug in original, brie increases twice as fast in quality after sell date passed
        }

        item.sellIn--;
      });

      ITEM_UPDATERS.put("Sulfuras, Hand of Ragnaros", item -> {});

      ITEM_UPDATERS.put("Backstage passes to a TAFKAL80ETC concert", item -> {
        if(item.sellIn <= 0) {
          item.quality = 0;
        }
        else {
          changeQuality(item.sellIn <= 5 ? 3 : item.sellIn <= 10 ? 2 : 1, item);
        }

        item.sellIn--;
      });

      ITEM_UPDATERS.put("Conjured", item -> {
        changeQuality(-2, item);

        if(item.sellIn <= 0) {
          changeQuality(-2, item);
        }

        item.sellIn--;
      });

      DEFAULT_ITEM_UPDATER = item -> {
        changeQuality(-1, item);

        if(item.sellIn <= 0) {
          changeQuality(-1, item);
        }

        item.sellIn--;
      };
    }

    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (int i = 0; i < items.length; i++) {
          ItemUpdater itemUpdater = ITEM_UPDATERS.get(items[i].name);

          if(itemUpdater == null) {
            itemUpdater = DEFAULT_ITEM_UPDATER;
          }

          itemUpdater.updateItem(items[i]);
        }
    }

    private interface ItemUpdater {
      void updateItem(Item item);
    }

    private static void changeQuality(int delta, Item item) {
      item.quality = Math.max(0, Math.min(50, item.quality + delta));
    }
}