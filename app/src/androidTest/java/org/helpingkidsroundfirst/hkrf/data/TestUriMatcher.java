package org.helpingkidsroundfirst.hkrf.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Alex on 1/28/2017.
 */

public class TestUriMatcher extends AndroidTestCase {

    private static final Uri TEST_ITEM_DIR = InventoryContract.ItemEntry.CONTENT_URI;
    private static final Uri TEST_CURRENT_INVENTORY_DIR =
            InventoryContract.CurrentInventoryEntry.CONTENT_URI;
    private static final Uri TEST_PAST_INVENTORY_DIR =
            InventoryContract.PastInventoryEntry.CONTENT_URI;


    public void testUirMatcher() {
        UriMatcher testMatcher = InventoryProvider.buildUriMatcher();

        // test item uri
        assertEquals("Error: the ITEM uri was matched incorrectly",
                testMatcher.match(TEST_ITEM_DIR), InventoryProvider.INVENTORY_ITEM);

        // test current inv uri
        assertEquals("Error: the CURRENT INV uri was matched incorrectly",
                testMatcher.match(TEST_CURRENT_INVENTORY_DIR), InventoryProvider.CURRENT_INVENTORY);

        // test past inv uri
        assertEquals("Error: the PAST INV uri was matched incorrectly",
                testMatcher.match(TEST_PAST_INVENTORY_DIR), InventoryProvider.PAST_INVENTORY);
    }
}
