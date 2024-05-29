package org.helioviewer.jhv.layers.connect;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.helioviewer.jhv.Log;
import org.helioviewer.jhv.base.Colors;
import org.helioviewer.jhv.math.MathUtils;
import org.helioviewer.jhv.math.Vec3;
import org.helioviewer.jhv.time.JHVTime;

import com.alibaba.fastjson2.JSON;

public class SunFastJSON {

    static SunJSONTypes.GeometryCollection process(String string) {
        return parseInput(JSON.parseObject(string, JObject.class));
    }

    static SunJSONTypes.GeometryCollection process(InputStream input) {
        return parseInput(JSON.parseObject(input, JObject.class));
    }

    private static SunJSONTypes.GeometryCollection parseInput(JObject jo) {
        if (!"SunJSON".equals(jo.type))
            throw new IllegalArgumentException("Unknown type: " + jo.type);

        JHVTime time = new JHVTime(jo.time);
        List<SunJSONTypes.GeometryBuffer> gl = jo.geometry.parallelStream().map(jg -> {
            try {
                return createGeometry(jg);
            } catch (Exception e) {
                Log.error(e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
        return new SunJSONTypes.GeometryCollection(time, gl);
    }

    private static SunJSONTypes.GeometryBuffer createGeometry(JGeometry jg) {
        int size = jg.coordinates.size();
        List<Vec3> coords = new ArrayList<>(size);
        for (double[] c : jg.coordinates) {
            if (c.length != 3)
                throw new IllegalArgumentException("Coordinate length not 3");
            if (c[0] < 1)
                Log.warn("Radius < 1: " + c[0] + ' ' + c[1] + ' ' + c[2]);
            coords.add(SunJSONTypes.toCartesian(c[0], Math.toRadians(c[1]), Math.toRadians(c[2])));
        }

        List<byte[]> colors = new ArrayList<>(size);
        for (int[] c : jg.colors) {
            if (c.length != 4)
                throw new IllegalArgumentException("Color length not 4");
            colors.add(Colors.bytes(
                    MathUtils.clip(c[0], 0, 255),
                    MathUtils.clip(c[1], 0, 255),
                    MathUtils.clip(c[2], 0, 255),
                    MathUtils.clip(c[3], 0, 255)));
        }

        double thickness = MathUtils.clip(jg.thickness, 1e-5, 1e-1);
        return SunJSONTypes.getGeometryBuffer(jg.type, coords, colors, thickness);
    }

    private record JObject(String type, String time, List<JGeometry> geometry) {
    }

    private record JGeometry(String type, List<double[]> coordinates, List<int[]> colors, double thickness) {
    }

}
