package com.amazon.ata.service;

import com.amazon.ata.cost.MonetaryCostStrategy;
import com.amazon.ata.dao.PackagingDAO;
import com.amazon.ata.datastore.PackagingDatastore;
import com.amazon.ata.exceptions.NoPackagingFitsItemException;
import com.amazon.ata.exceptions.UnknownFulfillmentCenterException;
import com.amazon.ata.types.Box;
import com.amazon.ata.types.FulfillmentCenter;
import com.amazon.ata.types.Item;
import com.amazon.ata.types.Material;
import com.amazon.ata.types.Packaging;
import com.amazon.ata.types.ShipmentOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ShipmentServiceTest {

    private Item smallItem = Item.builder()
            .withHeight(BigDecimal.valueOf(1))
            .withWidth(BigDecimal.valueOf(1))
            .withLength(BigDecimal.valueOf(1))
            .withAsin("abcde")
            .build();

    private Item largeItem = Item.builder()
            .withHeight(BigDecimal.valueOf(1000))
            .withWidth(BigDecimal.valueOf(1000))
            .withLength(BigDecimal.valueOf(1000))
            .withAsin("12345")
            .build();

    private Packaging smallBox = new Box(Material.CORRUGATE,
            BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.valueOf(10));

    private FulfillmentCenter existentFC = new FulfillmentCenter("ABE2");
    private FulfillmentCenter nonExistentFC = new FulfillmentCenter("NonExistentFC");


    @Mock
    private PackagingDAO packagingDAO;

    @InjectMocks
    private ShipmentService shipmentService = new ShipmentService(new PackagingDAO(new PackagingDatastore()),
            new MonetaryCostStrategy());

    @BeforeEach
    void setUp() {
        initMocks(this);
    }


    @Test
    void findBestShipmentOption_existentFCAndItemCanFit_returnsShipmentOption() throws Exception {
        List<ShipmentOption> shipmentOptions = new ArrayList<ShipmentOption>();
        shipmentOptions.add(ShipmentOption.builder()
                .withItem(smallItem)
                .withFulfillmentCenter(existentFC)
                .withPackaging(smallBox)
                .build());
        when(packagingDAO.findShipmentOptions(any(Item.class), any(FulfillmentCenter.class))).thenReturn(shipmentOptions);

        // GIVEN & WHEN
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(smallItem, existentFC);

        // THEN
        assertNotNull(shipmentOption);
    }

    @Test
    void findBestShipmentOption_existentFCAndItemCannotFit_returnsShipmentOption() throws Exception{
        when(packagingDAO.findShipmentOptions(any(Item.class), any(FulfillmentCenter.class))).thenThrow(
                new NoPackagingFitsItemException()
        );

        // GIVEN & WHEN
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(largeItem, existentFC);

        // THEN
        assertNotNull(shipmentOption);
        assertNull(shipmentOption.getPackaging());
    }

    @Test
    void findBestShipmentOption_nonExistentFCAndItemCanFit_returnsShipmentOption() throws Exception {
        when(packagingDAO.findShipmentOptions(any(Item.class), any(FulfillmentCenter.class))).thenThrow(
                new UnknownFulfillmentCenterException()
        );

        // WHEN + THEN
        Exception exception = assertThrows(RuntimeException.class, () -> {
            shipmentService.findShipmentOption(smallItem, nonExistentFC);
        }, "When FC is not exist, throw RuntimeException.");

        assertTrue(exception.getCause().getClass().equals(UnknownFulfillmentCenterException.class));
    }

    @Test
    void findBestShipmentOption_nonExistentFCAndItemCannotFit_returnsShipmentOption() throws Exception {
        when(packagingDAO.findShipmentOptions(any(Item.class), any(FulfillmentCenter.class))).thenThrow(
                new UnknownFulfillmentCenterException()
        );

        // THEN
        assertThrows(RuntimeException.class, () -> {
            shipmentService.findShipmentOption(largeItem, nonExistentFC);
        }, "When FC is not exist, throw RuntimeException.");
    }
}