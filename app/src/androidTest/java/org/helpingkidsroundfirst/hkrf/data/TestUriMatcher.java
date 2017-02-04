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
    private static final Uri TEST_CATEGORY_DIR = InventoryContract.CategoryEntry.CONTENT_URI;


    public void testUirMatcher() {
        UriMatcher testMatcher = InventoryProvider.buildUriMatcher();

        // test item uri
        assertEquals("Error: the ITEM uri was matched incorrectly",
                testMatcher.match(TEST_ITEM_DIR), InventoryProvider.INVENTORY_ITEM);
        /*
        assertEquals("Error: the ITEM uri was matched incorrectly",
                testMatcher.match(TEST_ITEM_DIR), InventoryProvider.INVENTORY_ITEM_WITH_ID);
        assertEquals("Error: the ITEM uri was matched incorrectly",
                testMatcher.match(TEST_ITEM_DIR), InventoryProvider.INVENTORY_ITEM_WITH_CATEGORY);*/

        // test current inv uri
        assertEquals("Error: the CURRENT INV uri was matched incorrectly",
                testMatcher.match(TEST_CURRENT_INVENTORY_DIR), InventoryProvider.CURRENT_INVENTORY);
        /*assertEquals("Error: the CURRENT INV uri was matched incorrectly",
                testMatcher.match(TEST_CURRENT_INVENTORY_DIR), InventoryProvider.CATEGORY_WITH_ID);
        assertEquals("Error: the CURRENT INV uri was matched incorrectly",
                testMatcher.match(TEST_CURRENT_INVENTORY_DIR),
                InventoryProvider.CURRENT_INVENTORY_WITH_CATEGORY);*/

        // test past inv uri
        assertEquals("Error: the PAST INV uri was matched incorrectly",
                testMatcher.match(TEST_PAST_INVENTORY_DIR), InventoryProvider.PAST_INVENTORY);
        /*assertEquals("Error: the PAST INV uri was matched incorrectly",
                testMatcher.match(TEST_PAST_INVENTORY_DIR), InventoryProvider.PAST_INVENTORY_WITH_ID);
        assertEquals("Error: the PAST INV uri was matched incorrectly",
                testMatcher.match(TEST_PAST_INVENTORY_DIR),
                InventoryProvider.PAST_INVENTORY_WITH_CATEGORY);*/

        // test category uri
        assertEquals("Error: the CATEGORY uri was matched incorreclty",
                testMatcher.match(TEST_CATEGORY_DIR), InventoryProvider.CATEGORY);
        /*assertEquals("Error: the CATEGORY uri was matched incorreclty",
                testMatcher.match(TEST_CATEGORY_DIR), InventoryProvider.CATEGORY_WITH_ID);*/
    }
}
