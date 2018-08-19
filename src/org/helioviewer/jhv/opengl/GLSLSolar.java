package org.helioviewer.jhv.opengl;

import org.helioviewer.jhv.base.Buf;

import com.jogamp.opengl.GL2;

public class GLSLSolar extends VAO2 {

    private static final Buf vexBuf = new Buf(4 * 16)
            .put4f(-1, -1, 0, 1)
            .put4f(1, -1, 0, 1)
            .put4f(-1, 1, 0, 1)
            .put4f(1, 1, 0, 1);

    GLSLSolar() {
        super(new VAA2[]{new VAA2(0, 4, false, 0, 0, 0)});
    }

    public void render(GL2 gl) {
        bind(gl);
        gl.glDrawArrays(GL2.GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
    public void init(GL2 gl) {
        super.init(gl);
        vbo.setData(gl, vexBuf.toBuffer());
    }

}
