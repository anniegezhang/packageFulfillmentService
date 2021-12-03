package com.amazon.ata.cost;

import com.amazon.ata.types.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonetaryCostStrategyTest {

    private static final Packaging BOX_10x10x20 =
        new Box(Material.CORRUGATE, BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.valueOf(20));

    private static final Packaging PolyBag_100 =
            new PolyBag(Material.LAMINATED_PLASTIC, BigDecimal.valueOf(100));

    private MonetaryCostStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new MonetaryCostStrategy();
    }

    @Test
    void getCost_corrugateMaterial_returnsCorrectCost() {
        // GIVEN
        ShipmentOption option = ShipmentOption.builder()
            .withPackaging(BOX_10x10x20)
            .build();

        // WHEN
        ShipmentCost shipmentCost = strategy.getCost(option);

        // THEN
        assertTrue(BigDecimal.valueOf(5.43).compareTo(shipmentCost.getCost()) == 0,
            "Incorrect monetary cost calculation for a box with dimensions 10x10x20.");
    }

    @Test
    void getCost_LAMINATED_PLASTIC_Material_returnsCorrectCost() {
        // GIVEN
        ShipmentOption option = ShipmentOption.builder()
                .withPackaging(PolyBag_100)
                .build();

        // WHEN
        ShipmentCost shipmentCost = strategy.getCost(option);

        // THEN
        assertTrue(BigDecimal.valueOf(1.93).compareTo(shipmentCost.getCost()) == 0,
                "Incorrect monetary cost calculation for a Polybag with volume 100.");
    }
}
