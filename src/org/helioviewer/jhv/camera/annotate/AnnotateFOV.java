package org.helioviewer.jhv.camera.annotate;

import javax.annotation.Nullable;

import org.helioviewer.jhv.astronomy.Position;
import org.helioviewer.jhv.camera.Camera;
import org.helioviewer.jhv.camera.CameraHelper;
import org.helioviewer.jhv.camera.InteractionAnnotate.AnnotationMode;
import org.helioviewer.jhv.display.Display;
import org.helioviewer.jhv.display.Viewport;
import org.helioviewer.jhv.math.Vec3;
import org.helioviewer.jhv.opengl.FOVShape;
import org.helioviewer.jhv.opengl.GLInfo;
import org.json.JSONObject;

import com.jogamp.opengl.GL2;

public class AnnotateFOV extends AbstractAnnotateable {

    private final FOVShape fov = new FOVShape();
    private boolean inited;

    public AnnotateFOV(JSONObject jo) {
        super(jo);
    }

    @Override
    public void init(GL2 gl) {
        if (!inited) {
            fov.init(gl);
            inited = true;
        }
    }

    @Override
    public void dispose(GL2 gl) {
        fov.dispose(gl);
    }

    @Nullable
    private Vec3 computePointFOV(Camera camera, int x, int y) {
        return CameraHelper.getVectorFromSphereOrPlane(camera, Display.getActiveViewport(), x, y, camera.getCurrentDragRotation());
    }

    @Override
    public void render(Camera camera, Viewport vp, GL2 gl, boolean active) {
        if ((startPoint == null || endPoint == null) && !beingDragged())
            return;

        double pointFactor = GLInfo.pixelScale[0] / (2 * camera.getFOV());
        Position viewpoint = camera.getViewpoint();

        gl.glPushMatrix();
        gl.glMultMatrixd(viewpoint.toQuat().toMatrix().transpose().m, 0);
        {
            Vec3 p0, p1;
            if (beingDragged()) {
                p0 = dragStartPoint;
                p1 = dragEndPoint;
            } else {
                p0 = startPoint;
                p1 = endPoint;
            }
            double dx = (p1.x - p0.x) / 2;
            double dy = (p1.y - p0.y) / 2;
            fov.setCenter(p0.x + dx, p0.y + dy);
            fov.setTAngles(dx / viewpoint.distance, dy / viewpoint.distance);
            fov.render(gl, viewpoint.distance, vp.aspect, pointFactor, active);
        }

        gl.glPopMatrix();
    }

    @Override
    public void mousePressed(Camera camera, int x, int y) {
        Vec3 pt = computePointFOV(camera, x, y);
        if (pt != null)
            dragStartPoint = pt;
    }

    @Override
    public void mouseDragged(Camera camera, int x, int y) {
        Vec3 pt = computePointFOV(camera, x, y);
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
        return AnnotationMode.FOV.toString();
    }

}
