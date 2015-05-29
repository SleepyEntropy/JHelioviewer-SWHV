package org.helioviewer.jhv.io;

import java.util.HashSet;

import javax.swing.SwingWorker;

import org.helioviewer.jhv.Settings;

public class DataSourceServers {

    private static DataSourceServers instance;

    private static final HashSet<DataSourceServerListener> listeners = new HashSet<DataSourceServerListener>();

    private String selectedServer = "";
    private final String[] serverList = new String[] { "ROB", "Helioviewer.org", "IAS" };

    private DataSourceServers() {
    }

    public static DataSourceServers getSingletonInstance() {
        if (instance == null) {
            instance = new DataSourceServers();
        }
        return instance;
    }

    public void addListener(DataSourceServerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DataSourceServerListener listener) {
        listeners.remove(listener);
    }

    public void changeServer(String server, final boolean donotloadStartup) {
        selectedServer = server;
        if (server.contains("ROB")) {
            Settings.getSingletonInstance().setProperty("API.dataSources.path", "http://swhv.oma.be/hv/api/?action=getDataSources&verbose=true&enable=[STEREO_A,STEREO_B,PROBA2]");
            Settings.getSingletonInstance().setProperty("API.jp2images.path", "http://swhv.oma.be/hv/api/index.php");
            Settings.getSingletonInstance().setProperty("API.jp2series.path", "http://swhv.oma.be/hv/api/index.php");
            Settings.getSingletonInstance().setProperty("default.remote.path", "jpip://swhv.oma.be:8090");
            Settings.getSingletonInstance().setProperty("API.event.path", "http://swhv.oma.be/hv/api/");
            Settings.getSingletonInstance().setProperty("default.httpRemote.path", "http://swhv.oma.be/hv/jp2/");
        } else if (server.contains("Helioviewer")) {
            Settings.getSingletonInstance().setProperty("API.dataSources.path", "http://helioviewer.org/api/?action=getDataSources&verbose=true&enable=[STEREO_A,STEREO_B,PROBA2]");
            Settings.getSingletonInstance().setProperty("API.jp2images.path", "http://helioviewer.org/api/index.php");
            Settings.getSingletonInstance().setProperty("API.jp2series.path", "http://helioviewer.org/api/index.php");
            Settings.getSingletonInstance().setProperty("default.remote.path", "jpip://helioviewer.org:8090");
            Settings.getSingletonInstance().setProperty("API.event.path", "http://helioviewer.org/api/");
            Settings.getSingletonInstance().setProperty("default.httpRemote.path", "http://helioviewer.org/jp2/");
        } else if (server.contains("IAS")) {
            Settings.getSingletonInstance().setProperty("API.dataSources.path", "http://helioviewer.ias.u-psud.fr/helioviewer/api/?action=getDataSources&verbose=true&enable=[STEREO_A,STEREO_B,PROBA2]");
            Settings.getSingletonInstance().setProperty("API.jp2images.path", "http://helioviewer.ias.u-psud.fr/helioviewer/api/index.php");
            Settings.getSingletonInstance().setProperty("API.jp2series.path", "http://helioviewer.ias.u-psud.fr/helioviewer/api/index.php");
            Settings.getSingletonInstance().setProperty("default.remote.path", "jpip://helioviewer.ias.u-psud.fr:8080");
            Settings.getSingletonInstance().setProperty("API.event.path", "http://helioviewer.ias.u-psud.fr/helioviewer/api/");
            Settings.getSingletonInstance().setProperty("default.httpRemote.path", "http://helioviewer.ias.u-psud.fr/helioviewer/jp2/");
        }

        SwingWorker<Void, Void> reloadSources = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                Thread.currentThread().setName("ReloadServer");
                DataSources.getSingletonInstance().reload();
                return null;
            }

            @Override
            public void done() {
                for (DataSourceServerListener l : listeners) {
                    l.serverChanged(donotloadStartup);
                }
            }

        };
        reloadSources.execute();
    }

    public String[] getServerList() {
        return serverList;
    }

    public String getSelectedServer() {
        return selectedServer;
    }

}
