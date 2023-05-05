package org.helioviewer.jhv.view.j2k.jpip.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.zip.InflaterInputStream;
import java.util.zip.GZIPInputStream;

import org.helioviewer.jhv.base.Regex;

public class HTTPChannel {

    private static final int PORT = 80;

    private final SocketChannel channel;

    protected final InputStream inputStream;
    protected final String host;

    protected HTTPChannel(URI uri) throws IOException {
        try {
            int port = uri.getPort();
            int usedPort = port <= 0 ? PORT : port;
            String usedHost = uri.getHost();
            host = usedHost + ':' + usedPort;

            channel = JHVChannel.connect(usedHost, usedPort);
            inputStream = new ByteChannelInputStream(channel);
        } catch (Exception e) { // redirect all to IOException
            throw new IOException(e);
        }
    }

    protected InputStream getStream(HTTPMessage msg) throws IOException {
        String head = msg.getHeader("Transfer-Encoding");
        String transferEncoding = head == null ? "" : head.toLowerCase();
        head = msg.getHeader("Content-Encoding");
        String contentEncoding = head == null ? "" : head.toLowerCase();

        TransferInputStream transferInput;
        switch (transferEncoding) {
            case "", "identity" -> {
                String contentLength = msg.getHeader("Content-Length");
                try {
                    transferInput = new FixedSizedInputStream(inputStream, Integer.parseInt(contentLength));
                } catch (Exception e) {
                    throw new IOException("Invalid Content-Length header: " + contentLength);
                }
            }
            case "chunked" -> transferInput = new ChunkedInputStream(inputStream);
            default -> throw new IOException("Unsupported transfer encoding: " + transferEncoding);
        }

        return switch (contentEncoding) {
            case "", "identity" -> transferInput;
            case "gzip" -> new GZIPInputStream(transferInput);
            case "deflate" -> new InflaterInputStream(transferInput);
            default -> throw new IOException("Unknown content encoding: " + contentEncoding);
        };
    }

    protected HTTPMessage readHeader() throws IOException {
        String line = LineRead.readAsciiLine(inputStream);
        if (!"HTTP/1.1 200 OK".equals(line))
            throw new ProtocolException("Invalid HTTP response: " + line);

        // Parses HTTP headers
        HTTPMessage res = new HTTPMessage();
        while (true) {
            line = LineRead.readAsciiLine(inputStream);
            if (line.isEmpty())
                return res;

            String[] parts = Regex.HttpField.split(line);
            if (parts.length != 2)
                throw new ProtocolException("Invalid HTTP header field: " + line);
            res.setHeader(parts[0], parts[1]);
        }
    }

    protected void write(String str) throws IOException {
        channel.write(ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8)));
    }

    protected void close() throws IOException {
        channel.close();
    }

    public boolean isClosed() {
        return !channel.isOpen();
    }

}
