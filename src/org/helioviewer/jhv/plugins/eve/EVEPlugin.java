package org.helioviewer.jhv.plugins.eve;

import javax.swing.JMenuItem;

import org.helioviewer.jhv.gui.JHVFrame;
import org.helioviewer.jhv.plugins.Plugin;
import org.helioviewer.jhv.timelines.Timelines;
import org.helioviewer.jhv.timelines.band.BandDataProvider;
import org.helioviewer.jhv.timelines.gui.NewLayerAction;
import org.helioviewer.jhv.timelines.gui.OpenLocalFileAction;
import org.json.JSONObject;

public class EVEPlugin extends Plugin {

    private final Timelines tl = new Timelines();
    private final JMenuItem newItem = new JMenuItem(new NewLayerAction());
    private final JMenuItem openItem = new JMenuItem(new OpenLocalFileAction());

    public EVEPlugin() {
        super("Timelines", "Visualize 1D and 2D time series");
    }

    @Override
    public void install() {
        tl.installTimelines();
        BandDataProvider.loadBandTypes();
        JHVFrame.getMenuBar().getMenu(0).add(newItem, 1);
        JHVFrame.getMenuBar().getMenu(0).add(openItem, 3);
    }

    @Override
    public void uninstall() {
        tl.uninstallTimelines();
        JHVFrame.getMenuBar().getMenu(0).remove(newItem);
        JHVFrame.getMenuBar().getMenu(0).remove(openItem);
    }

    @Override
    public void saveState(JSONObject jo) {
        Timelines.saveState(jo);
    }

    @Override
    public void loadState(JSONObject jo) {
        Timelines.loadState(jo);
    }

}
