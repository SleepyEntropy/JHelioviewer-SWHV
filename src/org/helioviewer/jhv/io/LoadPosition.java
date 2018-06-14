package org.helioviewer.jhv.io;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.annotation.Nullable;

import org.helioviewer.jhv.astronomy.Position;
import org.helioviewer.jhv.astronomy.SpaceObject;
import org.helioviewer.jhv.astronomy.Sun;
import org.helioviewer.jhv.log.Log;
import org.helioviewer.jhv.math.MathUtils;
import org.helioviewer.jhv.threads.JHVWorker;
import org.helioviewer.jhv.time.JHVDate;
import org.json.JSONObject;

public class LoadPosition extends JHVWorker<Position[], Void> {

    private final LoadPositionFire receiver;
    private final SpaceObject target;
    private final String frame;
    public final long start;
    public final long end;

    private Position[] position = new Position[0];
    private String report = null;

    public LoadPosition(LoadPositionFire _receiver, SpaceObject _target, String _frame, long _start, long _end) {
        receiver = _receiver;
        target = _target;
        frame = _frame;
        start = _start;
        end = _end;
        receiver.fireLoaded("Loading...");
        setThreadName("MAIN--PositionLoad");
    }

    @Nullable
    @Override
    protected Position[] backgroundWork() {
        long deltat = 60, span = (end - start) / 1000;
        long max = 10000;

        if (span / deltat > max)
            deltat = span / max;

        try (NetClient nc = NetClient.of(new PositionRequest(target, frame, start, end, deltat).url, true)) {
            JSONObject result = JSONUtils.get(nc.getReader());
            if (nc.isSuccessful())
                return PositionRequest.parseResponse(result);
            else
                report = result.optString("faultstring", "Invalid network response");
        } catch (UnknownHostException e) {
            Log.debug("Unknown host, network down?", e);
        } catch (IOException e) {
            report = "Failed: server error";
        } catch (Exception e) {
            report = "Failed: JSON parse error: " + e;
        }
        return null;
    }

    @Override
    protected void done() {
        if (isCancelled()) {
            receiver.fireLoaded("Cancelled");
            return;
        }

        Position[] newPosition = null;
        try {
            newPosition = get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (report == null) {
            if (newPosition == null || newPosition.length == 0) {
                report = "empty response";
            } else {
                position = newPosition;
                receiver.fireLoaded("Loaded");
            }
        }
        if (report != null)
            receiver.fireLoaded(report);
    }

    public SpaceObject getTarget() {
        return target;
    }

    public boolean isLoaded() {
        return position.length > 0;
    }

    public Position getInterpolated(long time) {
        long tstart = position[0].time.milli;
        long tend = position[position.length - 1].time.milli;
        if (time <= tstart || tstart == tend)
            return position[0];
        else if (time >= tend)
            return position[position.length - 1];
        else {
            double interpolatedIndex = (time - tstart) / (double) (tend - tstart) * position.length;
            int i = (int) interpolatedIndex;
            i = MathUtils.clip(i, 0, position.length - 1);
            int inext = Math.min(i + 1, position.length - 1);

            tstart = position[i].time.milli;
            tend = position[inext].time.milli;

            double alpha = tend == tstart ? 1. : ((time - tstart) / (double) (tend - tstart)) % 1.;
            double dist = (1. - alpha) * position[i].distance + alpha * position[inext].distance;
            double hgln = (1. - alpha) * position[i].lon + alpha * position[inext].lon;
            double hglt = (1. - alpha) * position[i].lat + alpha * position[inext].lat;
            return new Position(new JHVDate(time), dist, hgln, hglt);
        }
    }

    public Position getRelativeInterpolated(long time) {
        Position p = getInterpolated(time);
        double elon = Sun.getEarth(p.time /*!*/).lon;
        return new Position(p.time, p.distance, elon - p.lon, p.lat);
    }

}
