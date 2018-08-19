package org.helioviewer.jhv.opengl;

import java.nio.ByteBuffer;

import org.helioviewer.jhv.base.Buf;

import com.jogamp.opengl.GL2;

class VBO2 {

    private final int bufferID;

    VBO2(GL2 gl) {
        int[] tmpId = new int[1];
        gl.glGenBuffers(1, tmpId, 0);
        bufferID = tmpId[0];
    }

    void delete(GL2 gl) {
        gl.glDeleteBuffers(1, new int[]{bufferID}, 0);
    }

    void bind(GL2 gl) {
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferID);
    }

    void setData(GL2 gl, Buf buf) {
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferID);
        ByteBuffer buffer = buf.toBuffer();
        int length = buffer.limit();
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, length, null, GL2.GL_STATIC_DRAW); // https://www.khronos.org/opengl/wiki/Buffer_Object_Streaming#Buffer_re-specification
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, length, buffer);
        buf.rewind();
    }

}
