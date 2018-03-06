package org.helioviewer.jhv.view.jp2view.cache;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import kdu_jni.KduException;

import org.helioviewer.jhv.log.Log;
import org.helioviewer.jhv.view.jp2view.image.ResolutionSet;
import org.helioviewer.jhv.view.jp2view.kakadu.KakaduSource;

public class CacheStatusRemote implements CacheStatus {

    private final int maxFrame;
    private final ResolutionSet[] resolutionSet;
    private KakaduSource source;

    private int partialUntil = 0;

    public CacheStatusRemote(KakaduSource _source, int _maxFrame) throws KduException {
        maxFrame = _maxFrame;

        source = _source;
        resolutionSet = new ResolutionSet[maxFrame + 1];
        resolutionSet[0] = source.getResolutionSet(0);
        destroyIfFull();
    }

    private void destroyIfFull() {
        for (int i = 0; i <= maxFrame; i++) {
            if (resolutionSet[i] == null) {
                return;
            }
        }
        source = null;
    }

    @Override
    public int getPartialUntil() {
        int i;
        for (i = partialUntil; i <= maxFrame; i++) {
            if (resolutionSet[i] == null)
                break;
        }
        partialUntil = Math.max(0, i - 1);
        return partialUntil;
    }

    @Override
    public ResolutionSet getResolutionSet(int frame) {
        if (resolutionSet[frame] == null) {
            Log.error("resolutionSet[" + frame + "] null"); // never happened?
            return resolutionSet[0];
        }
        return resolutionSet[frame];
    }

    private boolean fullyComplete;
    private static final AtomicBoolean full = new AtomicBoolean(true);

    @Override
    public boolean isComplete(int level) {
        if (fullyComplete)
            return true;

        for (int i = 0; i <= maxFrame; i++) {
            if (resolutionSet[i] == null)
                return false;
            AtomicBoolean status = resolutionSet[i].getComplete(level);
            if (status == null || !status.get())
                return false;
        }
        if (level == 0)
            fullyComplete = true;
        return true;
    }

    @Nullable
    @Override
    public AtomicBoolean getFrameStatus(int frame, int level) {
        if (fullyComplete)
            return full;
        if (resolutionSet[frame] == null)
            return null;
        return resolutionSet[frame].getComplete(level);
    }

    @Override
    public void setFrameComplete(int frame, int level) {
        if (fullyComplete)
            return;

        setFramePartial(frame);
        if (resolutionSet[frame] != null)
            resolutionSet[frame].setComplete(level);
    }

    @Override
    public void setFramePartial(int frame) {
        if (resolutionSet[frame] == null) {
            try {
                resolutionSet[frame] = source.getResolutionSet(frame);
                destroyIfFull();
            } catch (KduException e) {
                e.printStackTrace();
            }
        }
    }

}
