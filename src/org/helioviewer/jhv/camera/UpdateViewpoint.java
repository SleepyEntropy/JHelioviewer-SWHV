package org.helioviewer.jhv.camera;

import org.helioviewer.jhv.astronomy.Position;
import org.helioviewer.jhv.astronomy.Sun;
import org.helioviewer.jhv.layers.Layers;
import org.helioviewer.jhv.math.Quat;
import org.helioviewer.jhv.time.JHVDate;
import org.helioviewer.jhv.view.View;

public interface UpdateViewpoint {

    Position.Q update(JHVDate time);

    Observer observer = new Observer();
    Earth earth = new Earth();
    EarthFixedDistance earthFixedDistance = new EarthFixedDistance();
    Ecliptic ecliptic = new Ecliptic();
    Expert expert = new Expert();

    class Observer implements UpdateViewpoint {
        @Override
        public Position.Q update(JHVDate time) {
            View view = Layers.getActiveView();
            return view == null ? Sun.getEarthQuat(time) : view.getMetaData(time).getViewpoint();
        }
    }

    class Earth implements UpdateViewpoint {
        @Override
        public Position.Q update(JHVDate time) {
            return Sun.getEarthQuat(time);
        }
    }

    class EarthFixedDistance implements UpdateViewpoint {
        @Override
        public Position.Q update(JHVDate time) {
            Position.L p = Sun.getEarth(time);
            return new Position.Q(time, Sun.MeanEarthDistance, new Quat(0, p.lon));
        }
    }

    class Ecliptic implements UpdateViewpoint {

        private double distance;

        void setDistance(double d) {
            distance = d;
        }

        private PositionLoad positionLoad;

        void setPositionLoad(PositionLoad _positionLoad) {
            positionLoad = _positionLoad;
        }

        @Override
        public Position.Q update(JHVDate time) {
            return new Position.Q(time, distance, Quat.rotate(Quat.Q90, Sun.getEarthQuat(time).orientation));
        }
    }

    class Expert implements UpdateViewpoint {

        private PositionLoad positionLoad;

        void setPositionLoad(PositionLoad _positionLoad) {
            positionLoad = _positionLoad;
        }

        @Override
        public Position.Q update(JHVDate time) {
            if (!positionLoad.isLoaded())
                return Sun.getEarthQuat(time);

            long layerStart = 0, layerEnd = 0;
            // Active layer times
            View view = Layers.getActiveView();
            if (view != null) {
                layerStart = view.getFirstTime().milli;
                layerEnd = view.getLastTime().milli;
            }

            // camera times
            long positionStart = positionLoad.getStartTime();
            long positionEnd = positionLoad.getEndTime();

            long cameraTime;
            if (layerEnd == layerStart)
                cameraTime = positionEnd;
            else {
                double alpha = (time.milli - layerStart) / (double) (layerEnd - layerStart); //!
                cameraTime = (long) (positionStart + alpha * (positionEnd - positionStart) + .5);
            }

            return positionLoad.getInterpolatedQ(cameraTime);
        }

    }

}
