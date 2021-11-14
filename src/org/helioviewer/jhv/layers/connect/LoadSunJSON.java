package org.helioviewer.jhv.layers.connect;

import java.net.URI;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.helioviewer.jhv.gui.Message;
import org.helioviewer.jhv.io.JSONUtils;
import org.helioviewer.jhv.layers.Layers;
import org.helioviewer.jhv.log.Log;
import org.helioviewer.jhv.threads.EventQueueCallbackExecutor;

import com.google.common.util.concurrent.FutureCallback;

public class LoadSunJSON implements Callable<SunJSON.GeometryCollection> {

    public static void submit(@Nonnull URI uri) {
        submit(uri, Layers.getConnectionLayer());
    }

    public static Void submit(@Nonnull URI uri, @Nullable ReceiverSunJSON receiver) {
        if (receiver != null)
            EventQueueCallbackExecutor.pool.submit(new LoadSunJSON(uri), new Callback(receiver));
        return null;
    }

    private final URI uri;

    private LoadSunJSON(URI _uri) {
        uri = _uri;
    }

    @Override
    public SunJSON.GeometryCollection call() throws Exception {
        return SunJSON.process(JSONUtils.get(uri));
    }

    private static class Callback implements FutureCallback<SunJSON.GeometryCollection> {

        private final ReceiverSunJSON receiver;

        Callback(ReceiverSunJSON _receiver) {
            receiver = _receiver;
        }

        @Override
        public void onSuccess(SunJSON.GeometryCollection result) {
            receiver.setGeometry(result);
        }

        @Override
        public void onFailure(@Nonnull Throwable t) {
            Log.error("An error occurred while opening the remote file:", t);
            Message.err("An error occurred while opening the remote file:", t.getMessage(), false);
        }

    }

}
