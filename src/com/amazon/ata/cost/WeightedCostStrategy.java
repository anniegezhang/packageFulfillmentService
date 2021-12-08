package com.amazon.ata.cost;

import com.amazon.ata.types.ShipmentCost;
import com.amazon.ata.types.ShipmentOption;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class WeightedCostStrategy implements CostStrategy {
    private Map<CostStrategy, BigDecimal> strategyWeightMap;

    /**
     {@code WeightedCostStrategy} builder static inner class.
     */
    private WeightedCostStrategy(Builder builder) {
        strategyWeightMap = builder.strategyWeightMap;
    }

    /**
     * Returns a new WeightedCostStrategy.Builder object for constructing a WeightedCostStrategy.
     *
     * @return new builder ready for constructing a ShipmentOption
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ShipmentCost getCost(ShipmentOption shipmentOption) {
        BigDecimal weightedCost = BigDecimal.valueOf(0);
        for (Map.Entry<CostStrategy, BigDecimal> entry : strategyWeightMap.entrySet()) {
            weightedCost = weightedCost.add(entry.getKey().getCost(shipmentOption).getCost()
                            .multiply(entry.getValue()));
        }
        return new ShipmentCost(shipmentOption, weightedCost);
    }

    /**
     * {@code strategyWeightMap} builder static inner class.
     */
    public static class Builder {
        private Map<CostStrategy, BigDecimal> strategyWeightMap = new HashMap<>();

        private Builder() {
        }

        /**
         * Sets the {@code StrategyWithWeight} and returns a reference to this Builder so that the methods
         * can be chained together.
         *
         * @param costStrategy the {@code StrategyWithWeight} to set
         * @param costWeight the {@code StrategyWithWeight} to set
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

