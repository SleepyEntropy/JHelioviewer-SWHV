package org.helioviewer.viewmodel.view.opengl;

import java.util.Date;

import javax.media.opengl.GL2;

import org.helioviewer.base.math.MathUtils;
import org.helioviewer.base.physics.Astronomy;
import org.helioviewer.gl3d.scenegraph.GL3DState;
import org.helioviewer.viewmodel.changeevent.CacheStatusChangedReason;
import org.helioviewer.viewmodel.changeevent.ChangeEvent;
import org.helioviewer.viewmodel.changeevent.ImageTextureRecapturedReason;
import org.helioviewer.viewmodel.changeevent.RegionChangedReason;
import org.helioviewer.viewmodel.changeevent.RegionUpdatedReason;
import org.helioviewer.viewmodel.changeevent.SubImageDataChangedReason;
import org.helioviewer.viewmodel.imagedata.ImageData;
import org.helioviewer.viewmodel.metadata.HelioviewerOcculterMetaData;
import org.helioviewer.viewmodel.metadata.HelioviewerPositionedMetaData;
import org.helioviewer.viewmodel.metadata.MetaData;
import org.helioviewer.viewmodel.region.Region;
import org.helioviewer.viewmodel.region.StaticRegion;
import org.helioviewer.viewmodel.view.ImageInfoView;
import org.helioviewer.viewmodel.view.SubimageDataView;
import org.helioviewer.viewmodel.view.View;
import org.helioviewer.viewmodel.view.ViewListener;
import org.helioviewer.viewmodel.view.jp2view.JHVJPXView;
import org.helioviewer.viewmodel.view.opengl.shader.ShaderFactory;

public class GL3DImageTextureView extends AbstractGL3DView implements GL3DView {

    private boolean recaptureRequested = true;
    private boolean regionChanged = true;
    private boolean forceUpdate = false;
    private Region capturedRegion = null;

    @Override
    public void renderGL(GL2 gl, boolean nextView) {
        render3D(GL3DState.get());
    }

    @Override
    public void render3D(GL3DState state) {
        if (this.getView() != null) {
            // Only copy Framebuffer if necessary
            if (forceUpdate || recaptureRequested || regionChanged) {
                this.capturedRegion = copyScreenToTexture(state);
                if (forceUpdate) {
                    this.notifyViewListeners(new ChangeEvent(new ImageTextureRecapturedReason(this, StaticRegion.createAdaptedRegion(this.capturedRegion.getRectangle()))));
                }
                regionChanged = false;
                forceUpdate = false;
                recaptureRequested = false;
            }
        }
    }

    private Region copyScreenToTexture(GL3DState state) {
        ImageInfoView imageView = this.getAdapter(ImageInfoView.class);
        ImageData image = imageView.getAdapter(SubimageDataView.class).getSubimageData();
        Region region = image.getRegion();

        if (region == null) {
            regionChanged = false;
            return null;
        }

        double xOffset = region.getLowerLeftCorner().getX();
        double yOffset = region.getLowerLeftCorner().getY();
        double xScale = 1. / region.getWidth();
        double yScale = 1. / region.getHeight();
        Date dt = new Date(image.getDateMillis());

        double theta = -Astronomy.getB0InRadians(dt);
        double phi = Astronomy.getL0Radians(dt);
        MetaData metadata = imageView.getMetadata();
        if (metadata instanceof HelioviewerPositionedMetaData) {
            HelioviewerPositionedMetaData md = (HelioviewerPositionedMetaData) metadata;
            phi -= md.getStonyhurstLongitude() / MathUtils.radeg;
            theta = -md.getStonyhurstLatitude() / MathUtils.radeg;
        }

        ShaderFactory.changeRect(xOffset, yOffset, xScale, yScale);
        ShaderFactory.changeAngles(theta, phi);

        JHVJPXView jhvjpx = this.getAdapter(JHVJPXView.class);
        if (jhvjpx != null) {
            boolean diffMode = false;
            Region diffRegion = null;
            Date diffDate = null;

            if (!jhvjpx.getBaseDifferenceMode() && jhvjpx.getPreviousImageData() != null) {
                diffMode = true;
                diffRegion = jhvjpx.getPreviousImageData().getRegion();
                diffDate = new Date(jhvjpx.getPreviousImageData().getDateMillis());
            } else if (jhvjpx.getBaseDifferenceMode() && jhvjpx.getBaseDifferenceImageData() != null) {
                diffMode = true;
                diffRegion = jhvjpx.getBaseDifferenceImageData().getRegion();
                diffDate = new Date(jhvjpx.getBaseDifferenceImageData().getDateMillis());
            }

            if (diffMode) {
                double diffXOffset = diffRegion.getLowerLeftCorner().getX();
                double diffYOffset = diffRegion.getLowerLeftCorner().getY();
                double diffXScale = 1. / diffRegion.getWidth();
                double diffYScale = 1. / diffRegion.getHeight();

                double diffTheta = -Astronomy.getB0InRadians(diffDate);
                double diffPhi = Astronomy.getL0Radians(diffDate);

                ShaderFactory.setDifferenceRect(diffXOffset, diffYOffset, diffXScale, diffYScale);
                ShaderFactory.changeDifferenceAngles(diffTheta, diffPhi);
            }
        }

        double innerCutOff = 0;
        double outerCutOff = 40;
        if (metadata instanceof HelioviewerOcculterMetaData) {
            HelioviewerOcculterMetaData md = (HelioviewerOcculterMetaData) metadata;
            innerCutOff = md.getInnerPhysicalOcculterRadius();
            outerCutOff = md.getOuterPhysicalOcculterRadius();
        }

        ShaderFactory.setCutOffRadius(innerCutOff, outerCutOff);

        this.recaptureRequested = false;
        return region;
    }

    @Override
    protected void setViewSpecificImplementation(View newView, ChangeEvent changeEvent) {
        newView.addViewListener(new ViewListener() {
            @Override
            public void viewChanged(View sender, ChangeEvent aEvent) {
                if (aEvent.reasonOccurred(RegionChangedReason.class)) {
                    recaptureRequested = true;
                    regionChanged = true;
                } else if (aEvent.reasonOccurred(RegionUpdatedReason.class)) {
                    regionChanged = true;
                } else if (aEvent.reasonOccurred(SubImageDataChangedReason.class)) {
                    recaptureRequested = true;
                } else if (aEvent.reasonOccurred(CacheStatusChangedReason.class)) {
                    recaptureRequested = true;
                }
            }
        });
    }

    public Region getCapturedRegion() {
        return capturedRegion;
    }

    public void forceUpdate() {
        this.forceUpdate = true;
    }

}
