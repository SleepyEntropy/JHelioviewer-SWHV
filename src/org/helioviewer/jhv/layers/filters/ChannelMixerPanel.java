package org.helioviewer.jhv.layers.filters;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.helioviewer.jhv.gui.components.Buttons;
import org.helioviewer.jhv.gui.dialogs.MetaDataDialog;
import org.helioviewer.jhv.layers.ImageLayer;
import org.helioviewer.jhv.layers.MovieDisplay;

import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideToggleButton;

public class ChannelMixerPanel implements FilterDetails {

    private final JPanel boxPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    private final JPanel buttonPanel = new JPanel(new BorderLayout());
    private final JLabel title = new JLabel("Channels ", JLabel.RIGHT);

    public ChannelMixerPanel(ImageLayer layer) {
        JCheckBox redCheckBox = new JCheckBox("Red", layer.getGLImage().getRed());
        redCheckBox.setToolTipText("Toggle red channel");
        boxPanel.add(redCheckBox, BorderLayout.LINE_START);

        JCheckBox greenCheckBox = new JCheckBox("Green", layer.getGLImage().getGreen());
        greenCheckBox.setToolTipText("Toggle green channel");
        boxPanel.add(greenCheckBox, BorderLayout.CENTER);

        JCheckBox blueCheckBox = new JCheckBox("Blue", layer.getGLImage().getBlue());
        blueCheckBox.setToolTipText("Toggle blue channel");
        boxPanel.add(blueCheckBox, BorderLayout.LINE_END);

        ActionListener listener = e -> {
            layer.getGLImage().setColor(redCheckBox.isSelected() ? 1 : 0,
                    greenCheckBox.isSelected() ? 1 : 0,
                    blueCheckBox.isSelected() ? 1 : 0);
            MovieDisplay.display();
        };
        redCheckBox.addActionListener(listener);
        greenCheckBox.addActionListener(listener);
        blueCheckBox.addActionListener(listener);

        JideToggleButton mgnButton = new JideToggleButton(Buttons.mgn);
        mgnButton.setToolTipText("Multi-scale Gaussian normalization");
        mgnButton.addActionListener(e -> {
            layer.getView().clearCache();
            layer.getView().setMGN(mgnButton.isSelected());
            MovieDisplay.render(1);
        });

        MetaDataDialog metaDialog = new MetaDataDialog();
        JideButton metaButton = new JideButton(Buttons.info);
        metaButton.setToolTipText("Show metadata of selected layer");
        metaButton.addActionListener(e -> {
            metaDialog.setMetaData(layer);
            metaDialog.showDialog();
        });

        buttonPanel.add(mgnButton, BorderLayout.LINE_START);
        buttonPanel.add(metaButton, BorderLayout.LINE_END);
    }

    @Override
    public Component getFirst() {
        return title;
    }

    @Override
    public Component getSecond() {
        return boxPanel;
    }

    @Override
    public Component getThird() {
        return buttonPanel;
    }

}
