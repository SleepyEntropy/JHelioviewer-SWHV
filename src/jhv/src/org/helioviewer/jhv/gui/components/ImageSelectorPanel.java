package org.helioviewer.jhv.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.helioviewer.base.logging.Log;
import org.helioviewer.jhv.gui.IconBank;
import org.helioviewer.jhv.gui.IconBank.JHVIcon;
import org.helioviewer.jhv.gui.ImageViewerGui;
import org.helioviewer.jhv.gui.components.layerTable.LayerTable;
import org.helioviewer.jhv.gui.components.layerTable.LayerTableContainer;
import org.helioviewer.jhv.gui.dialogs.model.ObservationDialogDateModel;
import org.helioviewer.jhv.gui.dialogs.observation.ImageDataPanel;
import org.helioviewer.jhv.layers.LayersListener;
import org.helioviewer.jhv.layers.LayersModel;
import org.helioviewer.viewmodel.view.MovieView;
import org.helioviewer.viewmodel.view.TimedMovieView;
import org.helioviewer.viewmodel.view.View;
import org.helioviewer.viewmodel.view.jp2view.JHVJP2View;
import org.helioviewer.viewmodel.view.jp2view.datetime.ImmutableDateTime;

/**
 * Panel for displaying all layers including the layer specific controls.
 * <p>
 * This panel plays are very central role: It manages all visible layers,
 * <p>
 *
 * @author Markus Langenberg
 * @author Malte Nuhn
 */
public class ImageSelectorPanel extends JPanel implements LayersListener {

    private static final long serialVersionUID = 1L;

    private final ImageDataPanel observationImagePane = new ImageDataPanel();

    /**
     * Action to add a new layer. If there is a current active layer which much
     * different time, the dates will be updated.
     */
    private final Action addLayerAction = new AbstractAction("Add layer", IconBank.getIcon(JHVIcon.ADD)) {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        {
            putValue(SHORT_DESCRIPTION, "Add a new layer");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            // Check the dates if possible
            final JHVJP2View activeView = LayersModel.getSingletonInstance().getActiveView();

            if (activeView != null) {
                MovieView tmv = activeView.getAdapter(TimedMovieView.class);
                if (tmv != null && tmv.getMaximumAccessibleFrameNumber() == tmv.getMaximumFrameNumber()) {
                    final ImmutableDateTime start = LayersModel.getSingletonInstance().getStartDate(activeView);
                    final ImmutableDateTime end = LayersModel.getSingletonInstance().getEndDate(activeView);
                    if (start != null && end != null) {
                        try {
                            Date startDate = start.getTime();
                            Date endDate = end.getTime();
                            Date obsStartDate = ImageDataPanel.apiDateFormat.parse(observationImagePane.getStartTime());
                            Date obsEndDate = ImageDataPanel.apiDateFormat.parse(observationImagePane.getEndTime());
                            // only updates if its really necessary with a
                            // tolerance of an hour
                            final int tolerance = 60 * 60 * 1000;
                            if (Math.abs(startDate.getTime() - obsStartDate.getTime()) > tolerance || Math.abs(endDate.getTime() - obsEndDate.getTime()) > tolerance) {
                                if (ObservationDialogDateModel.getInstance().getStartDate() == null || !ObservationDialogDateModel.getInstance().isStartDateSetByUser()) {
                                    ObservationDialogDateModel.getInstance().setStartDate(startDate, false);
                                }
                                if (ObservationDialogDateModel.getInstance().getEndDate() == null || !ObservationDialogDateModel.getInstance().isEndDateSetByUser()) {
                                    ObservationDialogDateModel.getInstance().setEndDate(endDate, false);
                                }
                            }
                        } catch (ParseException e) {
                            // Should not happen
                            Log.error("Cannot update observation dialog", e);
                        }
                    }
                }
            }
            // Show dialog
            ImageViewerGui.getSingletonInstance().getObservationDialog().showDialog();
        }
    };
    /**
     * Button to add new layers
     */
    private final JButton addLayerButton = new JButton(addLayerAction);

    /**
     * Action to download the current layer. If there is no active layer, the
     * action will do nothing.
     * <p>
     * Should be activated accordingly in the class
     */
    private final Action downloadLayerAction = new AbstractAction() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        {
            putValue(SHORT_DESCRIPTION, "Download the selected layer");
            putValue(SMALL_ICON, IconBank.getIcon(JHVIcon.DOWNLOAD));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (LayersModel.getSingletonInstance().getActiveView() != null) {
                LayersModel.getSingletonInstance().downloadLayer(LayersModel.getSingletonInstance().getActiveView());
            }
        }
    };
    /**
     * Button to show {@link #downloadLayerAction}
     */
    private final JButton downloadLayerButton = new JButton(downloadLayerAction);

    private final LayerTable layerTable;
    /**
     * Action to move the current layer down. If there is no active layer, the
     * action will do nothing.
     * <p>
     * Should be activated accordingly in the class
     */
    private final Action moveLayerDownAction = new AbstractAction() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        {
            putValue(SHORT_DESCRIPTION, "Move down the selected layer");
            putValue(SMALL_ICON, IconBank.getIcon(JHVIcon.DOWN));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (LayersModel.getSingletonInstance().getActiveView() != null) {
                LayersModel.getSingletonInstance().moveLayerDown(layerTable.getSelectedRow());
            }
        }
    };
    /**
     * Button to show {@link #moveLayerDownAction}
     */
    private final JButton moveLayerDownButton = new JButton(moveLayerDownAction);
    /**
     * Action to move the current layer up. If there is no active layer, the
     * action will do nothing.
     * <p>
     * Should be activated accordingly in the class
     */
    private final Action moveLayerUpAction = new AbstractAction() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        {
            putValue(SHORT_DESCRIPTION, "Move up the selected layer");
            putValue(SMALL_ICON, IconBank.getIcon(JHVIcon.UP));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (LayersModel.getSingletonInstance().getActiveView() != null) {
                LayersModel.getSingletonInstance().moveLayerUp(layerTable.getSelectedRow());
            }
        }
    };
    /**
     * Button to show {@link #moveLayerUpAction}
     */
    private final JButton moveLayerUpButton = new JButton(moveLayerUpAction);
    /**
     * Action to show the meta data. If there is no active layer, the action
     * will do nothing.
     * <p>
     * Should be activated accordingly in the class
     */
    private final Action showMetaAction = new AbstractAction() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        {
            putValue(SHORT_DESCRIPTION, "Show metadata of the selected layer");
            putValue(SMALL_ICON, IconBank.getIcon(JHVIcon.INFO));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (LayersModel.getSingletonInstance().getActiveView() != null) {
                LayersModel.getSingletonInstance().showMetaInfo(LayersModel.getSingletonInstance().getActiveView());
            }
        }
    };
    /**
     * Button to show {@link #showMetaAction}
     */
    private final JButton showMetaButton = new JButton(showMetaAction);

    /**
     * Default constructor.
     */
    public ImageSelectorPanel() {
        // set up observation dialog
        ImageViewerGui.getSingletonInstance().getObservationDialog().addUserInterface("Image data", observationImagePane);

        // add components
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        addLayerButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        southPanel.add(moveLayerUpButton);
        southPanel.add(moveLayerDownButton);
        southPanel.add(showMetaButton);
        southPanel.add(downloadLayerButton);
        southPanel.add(addLayerButton);

        layerTable = new LayerTable();

        // Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(layerTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel emptyLabel = new JLabel("No layers added yet", JLabel.CENTER);
        emptyLabel.setFont(emptyLabel.getFont().deriveFont(Font.ITALIC));
        emptyLabel.setHorizontalTextPosition(JLabel.CENTER);
        emptyLabel.setOpaque(true);
        emptyLabel.setBackground(Color.WHITE);

        // Create the scroll pane and add the table to it.
        JScrollPane emptyScrollPane = new JScrollPane(emptyLabel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        emptyScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        LayerTableContainer layerTableContainer = new LayerTableContainer(scrollPane, emptyScrollPane);

        // Add the scroll pane to this panel.
        layerTableContainer.setPreferredSize(new Dimension(ImageViewerGui.SIDE_PANEL_WIDTH, LayerTable.ROW_HEIGHT * 4 + 2));

        this.setLayout(new BorderLayout());

        this.add(layerTableContainer, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);

        LayersModel.getSingletonInstance().addLayersListener(this);
        activateActions();
    }

    /**
     * Checks if there is a current active layer and activates the buttons
     * accordingly.
     */
    private void activateActions() {
        boolean e = LayersModel.getSingletonInstance().getActiveView() != null;
        downloadLayerAction.setEnabled(e);
        moveLayerDownAction.setEnabled(e);
        moveLayerUpAction.setEnabled(e);
        showMetaAction.setEnabled(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activeLayerChanged(View view) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void layerAdded(int newIndex) {
        activateActions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void layerRemoved(int oldIndex) {
        activateActions();
    }

    public ImageDataPanel getObservationImagePane() {
        return observationImagePane;
    }

}
