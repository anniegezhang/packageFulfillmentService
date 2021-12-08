package com.amazon.ata.dao;

import com.amazon.ata.datastore.PackagingDatastore;
import com.amazon.ata.exceptions.NoPackagingFitsItemException;
import com.amazon.ata.exceptions.UnknownFulfillmentCenterException;
import com.amazon.ata.types.FcPackagingOption;
import com.amazon.ata.types.FulfillmentCenter;
import com.amazon.ata.types.Item;
import com.amazon.ata.types.Packaging;
import com.amazon.ata.types.ShipmentOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Access data for which packaging is available at which fulfillment center.
 */
public class PackagingDAO {
    /*
     * A list of fulfillment centers with a packaging options they provide.
     */
    //private List<FcPackagingOption> fcPackagingOptions;
    //mt4: change the arraylist to a map
    /*
    Update the PackagingDAO (and any other necessary classes) to detect and ignore duplicate FcPackagingOptions.
    "Duplicate" in this case means that the FC code is identical, and the Packaging has the same size and the same
     material.
     We can achieve this by instead of using an ArrayList to hold our fcPackagingOption we can use a HashMap who keys
     are the FulfillmentCenters and whose values are a HashSet of FcPackagingOptions.
     */

    private Map<FulfillmentCenter, Set> fcPackagingOptions = new HashMap<FulfillmentCenter, Set>();


    /**
     * Instantiates a PackagingDAO object.
     *
     * @param datastore Where to pull the data from for fulfillment center/packaging available mappings.
     */

    public PackagingDAO(PackagingDatastore datastore) {
        for (FcPackagingOption fcPackagingOption : datastore.getFcPackagingOptions()) {
            if (!fcPackagingOptions.containsKey(fcPackagingOption.getFulfillmentCenter())) {
                Set<Packaging> packagingHashSet = new HashSet<Packaging>();
                packagingHashSet.add(fcPackagingOption.getPackaging());
                fcPackagingOptions.put(fcPackagingOption.getFulfillmentCenter(), (HashSet) packagingHashSet);
            } else {
                Set<Packaging> packagingHashSet = fcPackagingOptions.get(fcPackagingOption.getFulfillmentCenter());
                packagingHashSet.add(fcPackagingOption.getPackaging());
                fcPackagingOptions.put(fcPackagingOption.getFulfillmentCenter(), (HashSet) packagingHashSet);
            }
        }
        //this.fcPackagingOptions =  new ArrayList<>(datastore.getFcPackagingOptions());
    }

    /**
     * Returns the packaging options available for a given item at the specified fulfillment center. The API
     * used to call this method handles null inputs, so we don't have to.
     *
     * @param item              the item to pack
     * @param fulfillmentCenter fulfillment center to fulfill the order from
     * @return the shipping options available for that item; this can never be empty, because if there is no
     * acceptable option an exception will be thrown
     * @throws UnknownFulfillmentCenterException if the fulfillmentCenter is not in the fcPackagingOptions list
     * @throws NoPackagingFitsItemException      if the item doesn't fit in any packaging at the FC
     */
    public List<ShipmentOption> findShipmentOptions(Item item, FulfillmentCenter fulfillmentCenter)
            throws UnknownFulfillmentCenterException, NoPackagingFitsItemException {

        // Check all FcPackagingOptions for a suitable Packaging in the given FulfillmentCenter
        List<ShipmentOption> result = new ArrayList<>();
        for (Map.Entry<FulfillmentCenter, Set> entry : fcPackagingOptions.entrySet()) {
            if (!fcPackagingOptions.containsKey(fulfillmentCenter)) {
                throw new UnknownFulfillmentCenterException(
                        String.format("Unknown FC: %s!", fulfillmentCenter.getFcCode()));
            }
            String fcCode = entry.getKey().getFcCode();
            if (fcCode.equals(fulfillmentCenter.getFcCode())) {
                Set<Packaging> packagingHashSet = entry.getValue();
                Packaging[] packagingList = packagingHashSet.toArray(new Packaging[packagingHashSet.size()]);
                for (Packaging packaging : packagingList) {
                    if (packaging.canFitItem(item)) {
                        result.add(ShipmentOption.builder()
                                .withItem(item)
                                .withPackaging(packaging)
                                .withFulfillmentCenter(fulfillmentCenter)
                                .build());
                    }
                }
                if (result.isEmpty()) {
                    throw new NoPackagingFitsItemException(
                            String.format("No packaging at %s fits %s!", fulfillmentCenter.getFcCode(), item));
                }
            }
        }
        return result;
    }
}
