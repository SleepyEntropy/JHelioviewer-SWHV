package org.helioviewer.jhv.plugins.swek.view;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.helioviewer.jhv.plugins.swek.config.SWEKConfigurationManager;
import org.helioviewer.jhv.plugins.swek.config.SWEKEventType;
import org.helioviewer.jhv.plugins.swek.model.SWEKTreeModel;
import org.helioviewer.jhv.plugins.swek.model.SWEKTreeModelListener;

/**
 * The main visual component of the SWEK-plugin.
 * 
 * @author Bram Bourgoignie (Bram.Bourgoignie@oma.be)
 * 
 */
@SuppressWarnings({"serial"})
public class SWEKPluginPanel extends JPanel implements SWEKTreeModelListener {

    /** The singleton panel used */
    private static SWEKPluginPanel swekPluginPanel;

    /** The SWEK configuration manager */
    private final SWEKConfigurationManager configManager;

    /** Instance of the treeModel */
    private final SWEKTreeModel treeModelInstance;

    private SWEKPluginPanel() {
        configManager = SWEKConfigurationManager.getSingletonInstance();
        treeModelInstance = SWEKTreeModel.getSingletonInstance();
        treeModelInstance.addSWEKTreeModelListener(this);
        initVisualComponents();
        this.revalidate();
        this.repaint();

    }

    /**
     * Initializes the visual components.
     */
    private void initVisualComponents() {
        SWEKPluginPanel.this.setLayout(new BorderLayout());
        // this.setPreferredSize(new Dimension(150, 200));
        JPanel eventTypePanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(eventTypePanel, BoxLayout.Y_AXIS);
        eventTypePanel.setLayout(boxLayout);
        for (SWEKEventType eventType : configManager.getOrderedEventTypes()) {
            EventPanel eventPanel = new EventPanel(eventType);
            eventPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            eventTypePanel.add(eventPanel);
        }
        // JScrollPane sp = new JScrollPane(eventTypePanel);
        SWEKPluginPanel.this.add(eventTypePanel, BorderLayout.CENTER);
    }

    /**
     * Gives the main SWEKPluginPanel.
     * 
     * @return The swekPluginPanel.
     */
    public static SWEKPluginPanel getSWEKPluginPanelInstance() {
        if (swekPluginPanel == null) {
            swekPluginPanel = new SWEKPluginPanel();
        }
        return swekPluginPanel;
    }

    @Override
    public void expansionChanged() {
        super.revalidate();
        super.repaint();
    }

    @Override
    public void startedDownloadingEventType(SWEKEventType eventType) {
    }

    @Override
    public void stoppedDownloadingEventType(SWEKEventType eventType) {
    }

}
