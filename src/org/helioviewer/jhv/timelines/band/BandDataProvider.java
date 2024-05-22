package org.helioviewer.jhv.timelines.band;

import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import org.helioviewer.jhv.base.interval.Interval;

import com.google.common.collect.ArrayListMultimap;

class BandDataProvider {

    private static final ArrayListMultimap<Band, Future<Band.Data>> workers = ArrayListMultimap.create();

    static void addDownloads(Band band, List<Interval> intervals) {
        String baseUrl = band.getBandType().getBaseUrl();
        if ("".equals(baseUrl))
            return;
        intervals.forEach(interval -> workers.put(band, HapiReader.requestData(baseUrl, interval.start, interval.end)));
    }

    static void stopDownloads(Band band) {
        workers.get(band).forEach(worker -> worker.cancel(true));
        workers.removeAll(band);
    }

    static boolean isDownloadActive(Band band) {
        for (Future<?> worker : workers.get(band)) {
            if (!worker.isDone())
                return true;
        }
        return false;
    }

}
