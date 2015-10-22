package org.helioviewer.jhv.viewmodel.view.jp2view.image;

import java.util.Date;

import org.helioviewer.jhv.viewmodel.view.jp2view.JP2Image;
import org.helioviewer.jhv.viewmodel.view.jp2view.image.ResolutionSet.ResolutionLevel;

/**
 * A simple class that encapsulates the important parameters for decompressing
 * and downloading a JPEG2000 image. An immutable class so it is thread safe.
 * 
 * @author caplins
 * @author Benjamin Wamsler
 * 
 */
public class JP2ImageParameter {

    public final JP2Image jp2Image;

    public final Date masterTime;

    /** Essentially an immutable Rectangle */
    public final SubImage subImage;

    /** An object that contains the zoom/resolution information. */
    public final ResolutionLevel resolution;

    /** Zero based frame number */
    public final int compositionLayer;

    /** This constructor assigns all variables... throw NPE if any args are null */
    public JP2ImageParameter(JP2Image _jp2Image, Date _masterTime, SubImage _roi, ResolutionLevel _resolution, int _compositionLayer) {
        if (_roi == null || _resolution == null)
            throw new NullPointerException();
        jp2Image = _jp2Image;
        masterTime = _masterTime;
        subImage = _roi;
        resolution = _resolution;
        compositionLayer = _compositionLayer;
    }

    @Override
    public String toString() {
        return "ImageViewParams[ " + jp2Image + " " + masterTime + " " + subImage + " " + resolution + " [LayerNum=" + compositionLayer + "]]";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JP2ImageParameter)) {
            return false;
        }
        JP2ImageParameter params = (JP2ImageParameter) o;
        return jp2Image.equals(params.jp2Image) && masterTime.getTime() == params.masterTime.getTime() && subImage.equals(params.subImage) && resolution.equals(params.resolution) && compositionLayer == params.compositionLayer;
    }

}
