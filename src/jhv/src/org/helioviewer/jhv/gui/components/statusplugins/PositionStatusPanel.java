package org.helioviewer.jhv.gui.components.statusplugins;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import org.helioviewer.jhv.base.math.Quat;
import org.helioviewer.jhv.base.math.Vec2;
import org.helioviewer.jhv.camera.Camera;
import org.helioviewer.jhv.camera.CameraHelper;
import org.helioviewer.jhv.display.Displayer;
import org.helioviewer.jhv.display.Viewport;
import org.helioviewer.jhv.gui.ImageViewerGui;
import org.helioviewer.jhv.gui.components.StatusPanel;
import org.helioviewer.jhv.gui.controller.InputControllerPlugin;

@SuppressWarnings("serial")
public class PositionStatusPanel extends StatusPanel.StatusPlugin implements MouseMotionListener, InputControllerPlugin {

    private static final String nullCoordStr = "---\u00B0,---\u00B0";
    private static final String nullXYStr = "---\u2033,---\u2033";

    private static Camera camera;

    public PositionStatusPanel() {
        setText(formatOrtho(null, 0, 0, 0));
    }

    private void update(Point position) {
        Viewport vp = Displayer.getActiveViewport();
        Vec2 coord = ImageViewerGui.getRenderableGrid().gridPoint(camera, vp, position);

        if (Displayer.mode == Displayer.DisplayMode.LATITUDINAL) {
            setText(String.format("(\u03C6,\u03B8) : (%.2f\u00B0,%.2f\u00B0)", coord.x, coord.y));
        } else if (Displayer.mode == Displayer.DisplayMode.POLAR || Displayer.mode == Displayer.DisplayMode.LOGPOLAR) {
            setText(String.format("(\u03B8,\u03c1) : (%.2f\u00B0,%.2fR\u2299)", coord.x, coord.y));
        } else {
            double x = CameraHelper.computeUpX(camera, vp, position.x);
            double y = CameraHelper.computeUpY(camera, vp, position.y);
            double r = Math.sqrt(x * x + y * y);

            double d = camera.getViewpoint().distance;
            int px = (int) Math.round((3600 * 180 / Math.PI) * Math.atan2(x, d));
            int py = (int) Math.round((3600 * 180 / Math.PI) * Math.atan2(y, d));

            setText(formatOrtho(coord, r, px, py));
        }
    }

    private String formatOrtho(Vec2 coord, double r, int px, int py) {
        String coordStr;
        if (coord == null)
            coordStr = nullCoordStr;
        else
            coordStr = String.format("%+7.2f\u00B0,%+7.2f\u00B0", coord.x, coord.y);

        String xyStr;
        if (camera != null /* camera may be null on first call */ && camera.getCurrentDragRotation().equals(Quat.ZERO))
            xyStr = String.format("%+5d\u2033,%+5d\u2033", px, py);
        else
            xyStr = nullXYStr;

        return String.format("(\u03C6,\u03B8) : (%s) | \u03c1 : %.2fR\u2299 | (x,y) : (%s)", coordStr, r, xyStr);
    }

    @Override
    public void setCamera(Camera _camera) {
        camera = _camera;
    }

    @Override
    public void setComponent(Component _component) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        update(e.getPoint());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        update(e.getPoint());
    }

}
