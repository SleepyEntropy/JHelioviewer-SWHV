package org.helioviewer.jhv.camera.annotate;

import org.helioviewer.jhv.base.BufferUtils;
import org.helioviewer.jhv.base.FloatArray;
import org.helioviewer.jhv.camera.Camera;
import org.helioviewer.jhv.camera.InteractionAnnotate.AnnotationMode;
import org.helioviewer.jhv.display.Display;
import org.helioviewer.jhv.display.Viewport;
import org.helioviewer.jhv.math.Vec2;
import org.helioviewer.jhv.math.Vec3;
import org.helioviewer.jhv.opengl.GLHelper;
import org.helioviewer.jhv.opengl.GLSLPolyline;
import org.json.JSONObject;

import com.jogamp.opengl.GL2;

public class AnnotateRectangle extends AbstractAnnotateable {

    private static final int SUBDIVISIONS = 12;

    private final GLSLPolyline line = new GLSLPolyline();

    public AnnotateRectangle(JSONObject jo) {
        super(jo);
    }

    @Override
    public void init(GL2 gl) {
        line.init(gl);
    }

    @Override
    public void dispose(GL2 gl) {
        line.dispose(gl);
    }

    private static void drawRectangle(Camera camera, Viewport vp, Vec3 bp, Vec3 ep, FloatArray pos, FloatArray col, float[] color) {
        if (bp.z * ep.z < 0) {
            if (ep.z < bp.z && bp.z > Math.PI / 2)
                ep.z += 2 * Math.PI;
            else if (ep.z > bp.z && bp.z < -Math.PI / 2)
                bp.z += 2 * Math.PI;
        }

        Vec3 p2 = new Vec3(radius, ep.y, bp.z);
        Vec3 p4 = new Vec3(radius, bp.y, ep.z);

        Vec2 previous = null;
        Vec3 point1, point2;

        point1 = bp;
        point2 = p2;
        for (int i = 0; i <= SUBDIVISIONS; i++) {
            Vec3 pc = interpolate(i / (double) SUBDIVISIONS, point1, point2);

            if (Display.mode == Display.DisplayMode.Orthographic) {
                if (i == 0) { // first
                    pos.put4f(pc);
                    col.put4f(BufferUtils.colorNull);
                }
                pos.put4f(pc);
                col.put4f(color);
            } else {
                pc.y = -pc.y;
                if (i == 0) {
                    GLHelper.drawVertex(camera, vp, pc, previous, pos, col, BufferUtils.colorNull);
                }
                previous = GLHelper.drawVertex(camera, vp, pc, previous, pos, col, color);
            }
        }

        point1 = p2;
        point2 = ep;
        for (int i = 0; i <= SUBDIVISIONS; i++) {
            Vec3 pc = interpolate(i / (double) SUBDIVISIONS, point1, point2);

            if (Display.mode == Display.DisplayMode.Orthographic) {
                pos.put4f(pc);
                col.put4f(color);
            } else {
                pc.y = -pc.y;
                previous = GLHelper.drawVertex(camera, vp, pc, previous, pos, col, color);
            }
        }

        point1 = ep;
        point2 = p4;
        for (int i = 0; i <= SUBDIVISIONS; i++) {
            Vec3 pc = interpolate(i / (double) SUBDIVISIONS, point1, point2);

            if (Display.mode == Display.DisplayMode.Orthographic) {
                pos.put4f(pc);
                col.put4f(color);
            } else {
                pc.y = -pc.y;
                previous = GLHelper.drawVertex(camera, vp, pc, previous, pos, col, color);
            }
        }

        point1 = p4;
        point2 = bp;
        for (int i = 0; i <= SUBDIVISIONS; i++) {
            Vec3 pc = interpolate(i / (double) SUBDIVISIONS, point1, point2);

            if (Display.mode == Display.DisplayMode.Orthographic) {
                pos.put4f(pc);
                col.put4f(color);
                if (i == SUBDIVISIONS) { // last
                    pos.put4f(pc);
                    col.put4f(BufferUtils.colorNull);
                }
            } else {
                pc.y = -pc.y;
                previous = GLHelper.drawVertex(camera, vp, pc, previous, pos, col, color);
                if (i == SUBDIVISIONS) {
                    GLHelper.drawVertex(camera, vp, pc, previous, pos, col, BufferUtils.colorNull);
                }
            }
        }
    }

    @Override
    public void render(Camera camera, Viewport vp, GL2 gl, boolean active) {
        boolean dragged = beingDragged();
        if ((startPoint == null || endPoint == null) && !dragged)
            return;

        float[] color = dragged ? dragColor : (active ? activeColor : baseColor);
        Vec3 p0 = dragged ? dragStartPoint : startPoint;
        Vec3 p1 = dragged ? dragEndPoint : endPoint;

        FloatArray pos = new FloatArray();
        FloatArray col = new FloatArray();

        drawRectangle(camera, vp, toSpherical(p0), toSpherical(p1), pos, col, color);
        line.setData(gl, pos.toBuffer(), col.toBuffer());
        line.render(gl, vp, LINEWIDTH);
    }

    @Override
    public void mousePressed(Camera camera, int x, int y) {
        Vec3 pt = computePoint(camera, x, y);
        if (pt != null)
            dragStartPoint = pt;
    }

    @Override
    public void mouseDragged(Camera camera, int x, int y) {
        Vec3 pt = computePoint(camera, x, y);
        if (pt != null)
            dragEndPoint = pt;
    }

    @Override
    public void mouseReleased() {
        if (beingDragged()) {
            startPoint = dragStartPoint;
            endPoint = dragEndPoint;
        }
        dragStartPoint = null;
        dragEndPoint = null;
    }

    @Override
    public boolean beingDragged() {
        return dragEndPoint != null && dragStartPoint != null;
    }

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    public String getType() {
        return AnnotationMode.Rectangle.toString();
    }

}
