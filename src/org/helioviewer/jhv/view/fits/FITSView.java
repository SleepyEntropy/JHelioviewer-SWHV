package org.helioviewer.jhv.view.fits;

import java.awt.EventQueue;
import java.net.URI;

import javax.annotation.Nonnull;

import org.helioviewer.jhv.imagedata.ImageBuffer;
import org.helioviewer.jhv.imagedata.ImageData;
import org.helioviewer.jhv.io.APIRequest;
import org.helioviewer.jhv.metadata.XMLMetaDataContainer;
import org.helioviewer.jhv.position.Position;
import org.helioviewer.jhv.view.BaseView;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class FITSView extends BaseView {

    private static final Cache<FITSView, ImageBuffer> decodeCache = CacheBuilder.newBuilder().weakKeys().softValues().build();

    private final FITSDecodeExecutor executor;
    private final String xml;

    public FITSView(APIRequest _request, URI _uri, FITSDecodeExecutor _executor) throws Exception {
        super(_request, _uri);
        executor = _executor == null ? new FITSDecodeExecutor() : _executor;

        xml = FITSImage.getHeader(uri);
        if (xml == null)
            throw new Exception("Could not read FITS: " + uri);
        metaData[0] = new XMLMetaDataContainer(xml).getHVMetaData(0, false);
    }

    @Override
    public void decode(Position viewpoint, double pixFactor, double factor) {
        ImageBuffer imageBuffer = decodeCache.getIfPresent(this);
        if (imageBuffer == null) {
            executor.decode(this, viewpoint);
        } else {
            ImageData data = new ImageData(imageBuffer, metaData[0], metaData[0].getPhysicalRegion(), viewpoint);
            if (dataHandler != null)
                dataHandler.handleData(data);
        }
    }

    void setDataFromDecoder(ImageBuffer imageBuffer, Position viewpoint) {
        decodeCache.put(this, imageBuffer);
        ImageData data = new ImageData(imageBuffer, metaData[0], metaData[0].getPhysicalRegion(), viewpoint);
        EventQueue.invokeLater(() -> {
            if (dataHandler != null)
                dataHandler.handleData(data);
        });
    }

    @Override
    public void abolish() {
        executor.abolish();
        decodeCache.invalidate(this);
    }

    @Nonnull
    @Override
    public String getXMLMetaData() {
        return xml;
    }

}
