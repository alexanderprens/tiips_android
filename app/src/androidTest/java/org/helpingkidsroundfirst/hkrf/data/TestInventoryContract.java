package org.helpingkidsroundfirst.hkrf.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Alex on 1/28/2017.
 */

public class TestInventoryContract extends AndroidTestCase {

    public void testBuildInventoryItem() {

        // test item uri
        Uri itemUri = InventoryContract.ItemEntry.buildInventoryItemUri();
        assertNotNull("Error: null uri returned", itemUri);
        assertEquals("Error: uri does not match expected result",
                itemUri.toString(),
                "content://org.helpingkidsroundfirst.hkrf/items"
        );
    }

    public void testBuildCurrentInventory() {
        // test item uri
        Uri currentInvUri = InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri();
        assertNotNull("Error: null uri returned", currentInvUri);
        assertEquals("Error: uri does not match expected result",
                currentInvUri.toString(),
                "content://org.helpingkidsroundfirst.hkrf/current_inventory"
        );
    }

    public void testBuildPastInventory() {
        // test item uri
        Uri currentInvUri = InventoryContract.PastInventoryEntry.buildPastInventoryUri();
        assertNotNull("Error: null uri returned", currentInvUri);
        assertEquals("Error: uri does not match expected result",
                currentInvUri.toString(),
                "content://org.helpingkidsroundfirst.hkrf/past_inventory"
        );
    }
}
