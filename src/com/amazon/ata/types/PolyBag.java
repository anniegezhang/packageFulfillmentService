package com.amazon.ata.types;

import java.math.BigDecimal;


// created the entire class for m3m1 on 2021/12/02
public class PolyBag extends Packaging{
    private BigDecimal Volume;

    /**
     * Instantiates a new Packaging object.
     *
     *
     * @param volume - fixed volume of the package
     */
    public PolyBag(BigDecimal volume) {
        super(Material.LAMINATED_PLASTIC);
        this.Volume = volume;
    }

    public BigDecimal getVolume() {
        return Volume;
    }

    @Override
    public boolean canFitItem(Item item) {
        BigDecimal itemVolume = item.getHeight().multiply(item.getLength()).multiply(item.getWidth());
        return this.Volume.compareTo(itemVolume) > 0;
    }

    @Override
    public BigDecimal getMass() {
        return BigDecimal.valueOf(Math.ceil(Math.sqrt(this.Volume.doubleValue()) * 0.6));
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
