package com.amazon.ata.types;

import java.math.BigDecimal;
import java.util.Objects;


// created the entire class for m3m1 on 2021/12/02
public class PolyBag extends Packaging {
    private BigDecimal volume;

    /**
     * Instantiates a new Packaging object.
     * @param material - get the appropriate material from super class.
     * @param volume - fixed volume of the package
     */
    public PolyBag(Material material, BigDecimal volume) {
        super(Material.LAMINATED_PLASTIC);
        this.volume = volume;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    @Override
    public boolean canFitItem(Item item) {
        BigDecimal itemVolume = item.getHeight().multiply(item.getLength()).multiply(item.getWidth());
        return this.volume.compareTo(itemVolume) > 0;
    }

    @Override
    public BigDecimal getMass() {
        return BigDecimal.valueOf(Math.ceil(Math.sqrt(this.volume.doubleValue()) * 0.6));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PolyBag polyBag = (PolyBag) o;
        return getVolume().equals(polyBag.getVolume());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getVolume());
    }
}
