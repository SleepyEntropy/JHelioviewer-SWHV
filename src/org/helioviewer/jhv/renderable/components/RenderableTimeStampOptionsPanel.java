package org.helioviewer.jhv.renderable.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.helioviewer.jhv.display.Displayer;
import org.helioviewer.jhv.gui.ComponentUtils;
import org.helioviewer.jhv.gui.components.base.WheelSupport;

@SuppressWarnings("serial")
class RenderableTimeStampOptionsPanel extends JPanel {

    public RenderableTimeStampOptionsPanel(RenderableTimeStamp timeStamp) {
        setLayout(new GridBagLayout());

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 100, 200, 100);
        slider.addChangeListener(e -> {
            timeStamp.setSize(slider.getValue() / 100.);
            Displayer.display();
        });
        WheelSupport.installMouseWheelSupport(slider);

        GridBagConstraints c0 = new GridBagConstraints();
        c0.anchor = GridBagConstraints.EAST;
        c0.weightx = 1.;
        c0.weighty = 1.;
        c0.gridy = 0;
        c0.gridx = 0;
        add(new JLabel("Size", JLabel.RIGHT), c0);

        c0.anchor = GridBagConstraints.WEST;
        c0.gridx = 1;
        add(slider, c0);

        ComponentUtils.smallVariant(this);
    }

}
