package org.helioviewer.viewmodel.view.opengl.shader;

import javax.media.opengl.GL2;

import org.helioviewer.gl3d.model.image.GL3DImageMesh;

/**
 * The {@link GL3DShaderFactory} is used to create {@link GL3DImageMesh} nodes.
 * A Factory pattern is used, because the underlying image layer determines what
 * image meshes need to be created. Depending on the image, several image meshes
 * are grouped created.
 *
 * @author Simon Spoerri (simon.spoerri@fhnw.ch)
 *
 */
public class GL3DShaderFactory {

    public static GLVertexShaderProgram createVertexShaderProgram(GL2 gl, GLVertexShaderProgram vertexShaderProgram) {
        GLShaderBuilder newShaderBuilder = new GLShaderBuilder(gl, GL2.GL_VERTEX_PROGRAM_ARB, true);

        GLMinimalVertexShaderProgram minimalProgram = new GLMinimalVertexShaderProgram();
        minimalProgram.build(newShaderBuilder);
        vertexShaderProgram.build(newShaderBuilder);

        return vertexShaderProgram;
    }

}
