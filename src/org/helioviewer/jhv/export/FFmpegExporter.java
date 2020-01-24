package org.helioviewer.jhv.export;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import org.helioviewer.jhv.JHVGlobals;
import org.helioviewer.jhv.base.image.MappedImageFactory;

class FFmpegExporter implements MovieExporter {

    private String path;
    private int w;
    private int h;
    private int fps;

    private File tempFile;

    @Override
    public void open(String _path, int _w, int _h, int _fps) throws Exception {
        path = _path;
        w = _w;
        h = _h;
        fps = _fps;

        tempFile = File.createTempFile("dump", null, JHVGlobals.exportCacheDir);
        //tempFile.deleteOnExit();
        System.out.println(">>> " + w + 'x' + h + '@' + fps);
    }

    @Override
    public void encode(BufferedImage image) throws Exception {
        ByteBuffer data = MappedImageFactory.getByteBuffer(image).flip().limit(w * h * 3);

        try (FileChannel channel = new FileOutputStream(tempFile, true).getChannel()) {
            channel.write(data);
        }
    }

    @Override
    public void close() throws Exception {
        List<String> command = List.of(JHVGlobals.libCacheDir.getPath() + "/ffmpeg",
                "-f", "rawvideo",
                "-pix_fmt", "bgr24",
                "-r", String.valueOf(fps),
                "-s", w + "x" + h,
                "-i", tempFile.getPath(),
                "-pix_fmt", "yuv420p",
                "-y", path);

        try {
            ProcessBuilder builder = new ProcessBuilder()
                    .directory(JHVGlobals.exportCacheDir)
                    .redirectError(File.createTempFile("fferr", null, JHVGlobals.exportCacheDir))
                    .redirectOutput(File.createTempFile("ffout", null, JHVGlobals.exportCacheDir))
                    .command(command);

            int exitCode = builder.start().waitFor();
            System.out.println(">>> exitCode " + exitCode);
        } finally {
            tempFile.delete();
        }
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int getHeight() {
        return h;
    }

}
