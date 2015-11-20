package org.helioviewer.jhv.camera;

import org.helioviewer.jhv.display.Displayer;
import org.helioviewer.jhv.gui.ImageViewerGui;

public class Viewport {

    private int w;
    private int h;
    private int x;
    private int y;
    public final int index;

    private boolean active;

    public Viewport(int _idx, int _x, int _y, int _w, int _h, boolean _active) {
        index = _idx;
        w = _w;
        h = _h;
        x = _x;
        y = _y;
        active = _active;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public int getOffsetX() {
        return x;
    }

    public int getOffsetY() {
        return Displayer.getGLHeight() - h - y;
    }

    public void setSize(int _x, int _y, int _w, int _h) {
        w = _w;
        h = _h;
        x = _x;
        y = _y;
    }

    public boolean isInside(int px, int py) {
        if (px >= x && px < x + w && py >= getOffsetY() && py < getOffsetY() + h) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Offset: " + getOffsetX() + "," + getOffsetY() + " Size: " + getWidth() + "," + getHeight();
    }

    public boolean isActive() {
        return active;
    }

    public void computeActive() {
        active = ImageViewerGui.getRenderableContainer().isViewportActive(index);
    }

}
