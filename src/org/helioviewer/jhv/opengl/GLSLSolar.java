package org.helioviewer.jhv.opengl;

import java.nio.Buffer;

import com.jogamp.opengl.GL2;

public class GLSLSolar extends VAO2 {

    GLSLSolar() {
        super(1, false, new VAA[]{new VAA(0, 4, false, 0, 0, 0)});
    }

    public void render(GL2 gl) {
        bind(gl);
        gl.glDrawArrays(GL2.GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
    public void init(GL2 gl) {
        super.init(gl);
        BufVertex buf = new BufVertex(4 * 16).put4f(-1, -1, 0, 1).put4f(1, -1, 0, 1).put4f(-1, 1, 0, 1).put4f(1, 1, 0, 1);
        Buffer buffer = buf.toVertexBuffer();
        vbo[0].setBufferData(gl, buffer.limit(), buffer.capacity(), buffer);
    }

}
