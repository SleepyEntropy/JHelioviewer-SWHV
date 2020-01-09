package org.helioviewer.jhv.metadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.helioviewer.jhv.astronomy.Sun;
import org.helioviewer.jhv.base.Region;
import org.helioviewer.jhv.imagedata.SubImage;
import org.helioviewer.jhv.math.Quat;
import org.helioviewer.jhv.math.Vec2;
import org.helioviewer.jhv.position.Position;

class BaseMetaData implements MetaData {

    protected int frameNumber = 0;
    protected Region region;
    protected String displayName = "unknown";
    protected String unit = "";
    protected float[] physLUT;

    protected boolean calculateDepth;

    protected int pixelW;
    protected int pixelH;
    protected double unitPerPixelX = 1;
    protected double unitPerPixelY = 1;
    protected double unitPerArcsec = Double.NaN;
    protected double responseFactor = 1;

    protected Position viewpoint = Sun.StartEarth;
    protected double innerRadius = 0;
    protected double outerRadius = Double.MAX_VALUE;

    protected double crota = 0;
    protected double scrota = 0;
    protected double ccrota = 1;

    protected double sector0 = 0;
    protected double sector1 = 0;

    // Serves only for LASCO cutOff edges
    protected double cutOffValue = -1;
    protected Vec2 cutOffDirection = Vec2.ZERO;

    @Override
    public int getFrameNumber() {
        return frameNumber;
    }

    @Nonnull
    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Nonnull
    @Override
    public Region getPhysicalRegion() {
        return region;
    }

    @Override
    public int getPixelWidth() {
        return pixelW;
    }

    @Override
    public int getPixelHeight() {
        return pixelH;
    }

    @Override
    public double getUnitPerArcsec() {
        return unitPerArcsec;
    }

    @Override
    public double getResponseFactor() {
        return responseFactor;
    }

    @Nonnull
    @Override
    public Position getViewpoint() {
        return viewpoint;
    }

    @Override
    public double getInnerRadius() {
        return innerRadius;
    }

    @Override
    public double getOuterRadius() {
        return outerRadius;
    }

    @Override
    public double getCutOffValue() {
        return cutOffValue;
    }

    @Nonnull
    @Override
    public Vec2 getCutOffDirection() {
        return cutOffDirection;
    }

    @Nonnull
    @Override
    public Quat getCenterRotation() {
        return viewpoint.toQuat();
    }

    @Override
    public double getCROTA() {
        return crota;
    }

    @Override
    public double getSCROTA() {
        return scrota;
    }

    @Override
    public double getCCROTA() {
        return ccrota;
    }

    @Override
    public double getSector0() {
        return sector0;
    }

    @Override
    public double getSector1() {
        return sector1;
    }

    @Nonnull
    @Override
    public Region roiToRegion(@Nonnull SubImage roi, double factorX, double factorY) {
        return new Region(roi.x * factorX * unitPerPixelX + region.llx, roi.y * factorY * unitPerPixelY + region.lly,
                roi.width * factorX * unitPerPixelX, roi.height * factorY * unitPerPixelY);
    }

    @Override
    public double xPixelFactor(double xPoint) {
        return (xPoint - region.llx) / unitPerPixelX / pixelW;
    }

    @Override
    public double yPixelFactor(double yPoint) {
        return 1 - (yPoint - region.lly) / unitPerPixelY / pixelH;
    }

    @Nonnull
    @Override
    public String getUnit() {
        return unit;
    }

    @Nullable
    @Override
    public float[] getPhysicalLUT() {
        return physLUT;
    }

    @Override
    public boolean getCalculateDepth() {
        return calculateDepth;
    }

}
