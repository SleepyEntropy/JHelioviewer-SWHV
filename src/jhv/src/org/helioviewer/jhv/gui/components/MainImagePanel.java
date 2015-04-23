package org.helioviewer.jhv.gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.helioviewer.jhv.gui.controller.CameraMouseController;
import org.helioviewer.jhv.gui.interfaces.ImagePanelPlugin;
import org.helioviewer.viewmodel.view.ComponentView;

/**
 * This class represents an image component that is used to display the image of
 * all images.
 *
 * @author caplins
 * @author Alen Agheksanterian
 * @author Benjamin Wamsler
 * @author Stephan Pagel
 * @author Markus Langenberg
 */
public class MainImagePanel extends JPanel {

    private final ArrayList<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
    private final LinkedList<ImagePanelPlugin> plugins = new LinkedList<ImagePanelPlugin>();

    private final static CameraMouseController mouseController = new CameraMouseController();
    private final static ComponentView componentView = new ComponentView();

    public MainImagePanel() {
        super(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        Component component = componentView.getComponent();
        component.addMouseListener(mouseController);
        component.addMouseMotionListener(mouseController);
        component.addMouseWheelListener(mouseController);

        if (KeyListener.class.isAssignableFrom(mouseController.getClass())) {
            component.addKeyListener((KeyListener) mouseController);
        }
        add(component);

        mouseController.setImagePanel(this);
    }

    public ComponentView getComponentView() {
        return componentView;
    }

    /**
     * Adds a new plug-in to the component. Plug-ins in this case are controller
     * which e.g. has to react on inputs made to this component.
     *
     * @param newPlugin
     *            new plug-in which has to to be added to this component
     */
    public void addPlugin(ImagePanelPlugin newPlugin) {
        if (newPlugin == null) {
            return;
        }

        newPlugin.setImagePanel(this);
        newPlugin.setView(componentView);
        plugins.add(newPlugin);

        Component component = componentView.getComponent();

        if (newPlugin instanceof MouseListener && !Arrays.asList(component.getMouseListeners()).contains(newPlugin)) {
            component.addMouseListener((MouseListener) newPlugin);
        }
        if (newPlugin instanceof MouseMotionListener && !Arrays.asList(component.getMouseMotionListeners()).contains(newPlugin)) {
            component.addMouseMotionListener((MouseMotionListener) newPlugin);
        }
        if (newPlugin instanceof MouseWheelListener && !Arrays.asList(component.getMouseWheelListeners()).contains(newPlugin)) {
            component.addMouseWheelListener((MouseWheelListener) newPlugin);
        }
    }

    /**
     * Removes a plug-in from the component.
     *
     * @param oldPlugin
     *            plug-in which has to to be removed from this component
     *
     * @see MainImagePanel#addPlugin(ImagePanelPlugin)
     */
    public void removePlugin(ImagePanelPlugin oldPlugin) {
        if (oldPlugin == null) {
            return;
        }

        oldPlugin.setView(null);
        oldPlugin.setImagePanel(null);
        plugins.remove(oldPlugin);

        if (oldPlugin instanceof MouseListener)
            componentView.getComponent().removeMouseListener((MouseListener) oldPlugin);
        if (oldPlugin instanceof MouseMotionListener)
            componentView.getComponent().removeMouseMotionListener((MouseMotionListener) oldPlugin);
        if (oldPlugin instanceof MouseWheelListener)
            componentView.getComponent().removeMouseWheelListener((MouseWheelListener) oldPlugin);
    }

}
