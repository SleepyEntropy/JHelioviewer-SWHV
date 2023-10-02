package org.helioviewer.jhv.layers;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.helioviewer.jhv.base.lut.LUT;
import org.helioviewer.jhv.gui.components.Buttons;
import org.helioviewer.jhv.layers.filters.*;

import com.jidesoft.swing.JideToggleButton;

@SuppressWarnings("serial")
class ImageLayerOptions extends JPanel {

    private final LUTPanel lutPanel;
    private final RunningDifferencePanel runningDifferencePanel;

    ImageLayerOptions(ImageLayer layer) {
        runningDifferencePanel = new RunningDifferencePanel(layer);
        FilterDetails opacityPanel = new OpacityPanel(layer);
        FilterDetails blendPanel = new BlendPanel(layer);
        FilterDetails channelMixerPanel = new ChannelMixerPanel(layer);
        lutPanel = new LUTPanel(layer);
        FilterDetails levelsPanel = new LevelsPanel(layer);
        FilterDetails sharpenPanel = new SharpenPanel(layer);

        SlitPanel slitPanel = new SlitPanel(layer);
        InnerMaskPanel innerMaskPanel = new InnerMaskPanel(layer);

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;

        c.gridy = 0;
        addToGridBag(c, runningDifferencePanel);
        c.gridy++;
        addToGridBag(c, opacityPanel);
        c.gridy++;
        addToGridBag(c, blendPanel);
        c.gridy++;
        addToGridBag(c, sharpenPanel);
        c.gridy++;
        addToGridBag(c, levelsPanel);
        c.gridy++;
        addToGridBag(c, lutPanel);
        c.gridy++;
        addToGridBag(c, channelMixerPanel);
        c.gridy++;

        slitPanel.setVisible(false);
        innerMaskPanel.setVisible(false);

        JideToggleButton adjButton = new JideToggleButton(Buttons.adjustmentsRight);
        adjButton.setToolTipText("Options to control playback and recording");
        adjButton.addActionListener(e -> {
            boolean selected = adjButton.isSelected();
            slitPanel.setVisible(selected);
            innerMaskPanel.setVisible(selected);
            adjButton.setText(selected ? Buttons.adjustmentsDown : Buttons.adjustmentsRight);
        });
        c.gridx = 1;
        add(adjButton, c);

        c.gridy++;
        addToGridBag(c, slitPanel);
        c.gridy++;
        addToGridBag(c, innerMaskPanel);
    }

    private void addToGridBag(GridBagConstraints c, FilterDetails details) {
        c.gridwidth = 1;

        c.gridx = 0;
        c.weightx = 0;
        c.weighty = 1;
        c.anchor = GridBagConstraints.LINE_END;
        c.fill = GridBagConstraints.NONE;
        add(details.getTitle(), c);

        c.gridx = 1;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(details.getComponent(), c);

        c.gridx = 2;
        c.weightx = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.NONE;
        add(details.getLabel(), c);
    }

    void setLUT(LUT lut) {
        lutPanel.setLUT(lut);
    }

    RunningDifferencePanel getRunningDifferencePanel() {
        return runningDifferencePanel;
    }

}
