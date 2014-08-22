package org.helioviewer.viewmodel.view.opengl.shader;

import javax.media.opengl.GL2;

import org.helioviewer.viewmodel.view.opengl.shader.GLShaderBuilder.GLBuildShaderException;

/**
 * Specialized implementation of GLFragmentShaderProgram for filters based on
 * lookup tables.
 *
 * <p>
 * To use this program, it is not necessary to derive another class from this
 * one. A one-dimensional texture is used as the lookup table. To set the lookup
 * table, call {@link #activateLutTexture(GL)}. This binds the texture id used
 * for the lookup table. After that, the texture can be filled with the lookup
 * data. The rest is done by the class itself.
 *
 * @author Markus Langenberg
 */
public class GLSingleChannelLookupFragmentShaderProgram extends GLFragmentShaderProgram {

    private static int lutID = 0;
    int lutMode = -1;
    private GLShaderBuilder builder;

    /**
     * Binds the texture used for the lookup table.
     *
     * As a result, copying the lookup data to the texture can take place.
     *
     * @param gl
     *            Valid reference to the current gl object
     */
    public void activateLutTexture(GL2 gl) {
        gl.glActiveTexture(lutMode);
    }
    @Override
    public void bind(GL2 gl){
        super.bind(gl);
        gl.glBindProgramARB(target, shaderID);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildImpl(GLShaderBuilder shaderBuilder) {
        this.builder = shaderBuilder;
        try {
            String program = "\toutput.rgb = tex1D(lut, output.r).rgb;";

            program = program.replaceAll("output", shaderBuilder.useOutputValue("float4", "COLOR"));

            lutMode = shaderBuilder.addTextureParameter("sampler1D lut" + lutID);
            program = program.replaceAll("lut", "lut" + lutID);
            lutID = (lutID + 1) & 15;

            shaderBuilder.addMainFragment(program);

        } catch (GLBuildShaderException e) {
            e.printStackTrace();
        }
    }
}
