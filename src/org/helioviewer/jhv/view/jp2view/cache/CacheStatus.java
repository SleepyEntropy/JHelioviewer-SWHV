package org.helioviewer.jhv.view.jp2view.cache;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import kdu_jni.KduException;

import org.helioviewer.jhv.view.jp2view.kakadu.KakaduSource;
import org.helioviewer.jhv.view.jp2view.image.ResolutionSet;

public interface CacheStatus {

    int getPartialUntil();

    ResolutionSet getResolutionSet(int frame);

    boolean isComplete(int level);

    @Nullable
    AtomicBoolean getFrameStatus(int frame, int level);

    void setFrameComplete(KakaduSource source, int frame, int level) throws KduException;

    void setFramePartial(KakaduSource source, int frame) throws KduException;

}
