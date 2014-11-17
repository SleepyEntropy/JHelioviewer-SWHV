package org.helioviewer.gl3d.model.image;

import java.util.List;

import javax.media.opengl.GL2;

import org.helioviewer.base.physics.Constants;
import org.helioviewer.gl3d.scenegraph.GL3DState;
import org.helioviewer.gl3d.scenegraph.math.GL3DVec2d;
import org.helioviewer.gl3d.scenegraph.math.GL3DVec3d;
import org.helioviewer.gl3d.scenegraph.math.GL3DVec4d;
import org.helioviewer.gl3d.view.GL3DImageTextureView;
import org.helioviewer.viewmodel.metadata.HelioviewerOcculterMetaData;
import org.helioviewer.viewmodel.metadata.HelioviewerPositionedMetaData;
import org.helioviewer.viewmodel.metadata.MetaData;
import org.helioviewer.viewmodel.view.opengl.shader.GLFragmentShaderProgram;
import org.helioviewer.viewmodel.view.opengl.shader.GLVertexShaderProgram;

/**
 * Maps the solar disc part of an image layer onto an adaptive mesh that either
 * covers the entire solar disc or the just the part that is visible in the view
 * frustum.
 *
 * @author Simon Spoerri (simon.spoerri@fhnw.ch)
 *
 */
public class GL3DImageSphere extends GL3DImageMesh {

    private final GL3DImageLayer layer;
    private boolean showSphere;
    private boolean showCorona;
    private final boolean restoreColorMask;

    public GL3DImageSphere(GL3DImageTextureView imageTextureView, GLVertexShaderProgram vertexShaderProgram, GLFragmentShaderProgram fragmentShaderProgram, GL3DImageLayer imageLayer, boolean showSphere, boolean showCorona, boolean restoreColorMask) {
        super("Sphere", imageTextureView, vertexShaderProgram, fragmentShaderProgram);
        this.restoreColorMask = restoreColorMask;
        layer = imageLayer;
        this.showSphere = showSphere;
        this.showCorona = showCorona;
    }

    @Override
    public void shapeDraw(GL3DState state) {
        state.gl.glEnable(GL2.GL_CULL_FACE);
        state.gl.glEnable(GL2.GL_DEPTH_TEST);
        state.gl.glEnable(GL2.GL_BLEND);

        super.shapeDraw(state);
        if (restoreColorMask) {
            state.gl.glColorMask(true, true, true, true);
        }
    }

    @Override
    public GL3DMeshPrimitive createMesh(GL3DState state, List<GL3DVec3d> positions, List<GL3DVec3d> normals, List<GL3DVec2d> textCoords, List<Integer> indices, List<GL3DVec4d> colors) {
        if (this.capturedRegion != null) {
            int resolutionX = 50;
            int resolutionY = 50;
            int numberOfPositions = 0;
            MetaData metaData = this.layer.metaDataView.getMetaData();
            if (metaData instanceof HelioviewerOcculterMetaData) {
                HelioviewerOcculterMetaData md = (HelioviewerOcculterMetaData) metaData;
                showSphere = false;
            }
            if (showSphere) {
                for (int latNumber = 0; latNumber <= resolutionX; latNumber++) {
                    double theta = latNumber * Math.PI / resolutionX;
                    double sinTheta = Math.sin(theta);
                    double cosTheta = Math.cos(theta);
                    for (int longNumber = 0; longNumber <= resolutionY; longNumber++) {
                        double phi = longNumber * 2 * Math.PI / resolutionY;
                        double sinPhi = Math.sin(phi);
                        double cosPhi = Math.cos(phi);

                        double x = cosPhi * sinTheta;
                        double y = cosTheta;
                        double z = sinPhi * sinTheta;
                        positions.add(new GL3DVec3d(Constants.SunRadius * x, Constants.SunRadius * y, Constants.SunRadius * z));
                        numberOfPositions++;
                    }
                }

                for (int latNumber = 0; latNumber < resolutionX; latNumber++) {
                    for (int longNumber = 0; longNumber < resolutionY; longNumber++) {
                        int first = (latNumber * (resolutionY + 1)) + longNumber;
                        int second = first + resolutionY + 1;
                        indices.add(first);
                        indices.add(first + 1);
                        indices.add(second + 1);
                        indices.add(first);
                        indices.add(second + 1);
                        indices.add(second);
                    }
                }
            }
            if (metaData instanceof HelioviewerPositionedMetaData) {
                HelioviewerPositionedMetaData md = (HelioviewerPositionedMetaData) metaData;
                if (md.getInstrument().contains("HMI")) {
                    showCorona = false;
                }
            }
            if (showCorona) {
                int beginPositionNumberCorona = numberOfPositions;
                positions.add(new GL3DVec3d(-40., 40., 0.));
                numberOfPositions++;
                positions.add(new GL3DVec3d(40., 40., 0.));
                numberOfPositions++;
                positions.add(new GL3DVec3d(40., -40., 0.));
                numberOfPositions++;
                positions.add(new GL3DVec3d(-40., -40., 0.));
                numberOfPositions++;

                indices.add(beginPositionNumberCorona + 0);
                indices.add(beginPositionNumberCorona + 2);
                indices.add(beginPositionNumberCorona + 1);

                indices.add(beginPositionNumberCorona + 2);
                indices.add(beginPositionNumberCorona + 0);
                indices.add(beginPositionNumberCorona + 3);
            }
        }
        return GL3DMeshPrimitive.TRIANGLES;
    }
}
