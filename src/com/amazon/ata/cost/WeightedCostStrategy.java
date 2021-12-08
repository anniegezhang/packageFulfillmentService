package com.amazon.ata.cost;

import com.amazon.ata.types.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeightedCostStrategy implements CostStrategy {
    private Map<CostStrategy, BigDecimal> strategyWeightMap;

    public static Builder builder() {
        return new Builder();
    }

    public WeightedCostStrategy(Builder builder) {
        strategyWeightMap = builder.strategyWeightMap;
    }

    @Override
    public ShipmentCost getCost(ShipmentOption shipmentOption) {
        BigDecimal weightedCost = BigDecimal.valueOf(0);
        for (Map.Entry<CostStrategy, BigDecimal> entry : strategyWeightMap.entrySet()) {
            weightedCost = weightedCost.add(entry.getKey().getCost(shipmentOption).getCost().multiply(entry.getValue()));
        }
        return new ShipmentCost(shipmentOption,weightedCost);
    }

    /**
     * {@code ShipmentOption} builder static inner class.
     */
    public static class Builder {
        private Map<CostStrategy, BigDecimal> strategyWeightMap = new HashMap<>();

        private Builder() {
        }

        /**
         * Sets the {@code item} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param costStrategy the {@code item} to set
         * @return a reference to this Builder
         */
        public WeightedCostStrategy.Builder addStrategyWithWeight(CostStrategy costStrategy, BigDecimal costWeight) {
            this.strategyWeightMap.put(costStrategy, costWeight);
            return this;
        }

        /**
         * Returns a {@code WeightedCostStrategy} built from the parameters previously set.
         *
         * @return a {@code WeightedCostStrategy} built with parameters of this {@code WeightedCostStrategy.Builder}
         */
        public WeightedCostStrategy build() {
            return new WeightedCostStrategy(this);
        }
    }
}

