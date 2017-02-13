package org.helioviewer.jhv.camera;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.helioviewer.jhv.base.time.JHVDate;
import org.helioviewer.jhv.base.time.TimeUtils;
import org.helioviewer.jhv.gui.ComponentUtils;
import org.helioviewer.jhv.gui.components.base.JSeparatorComboBox;
import org.helioviewer.jhv.gui.components.base.TimeTextField;
import org.helioviewer.jhv.gui.components.calendar.JHVCalendarDatePicker;
import org.helioviewer.jhv.layers.Layers;
import org.helioviewer.jhv.layers.LayersListener;
import org.helioviewer.jhv.viewmodel.view.View;

@SuppressWarnings("serial")
public class CameraOptionPanelExpert extends CameraOptionPanel implements LayersListener {

    private final JLabel loadedLabel;

    private JPanel addBeginDatePanel;
    private JHVCalendarDatePicker beginDatePicker;
    private TimeTextField beginTimePicker;

    private JPanel addEndDatePanel;
    private JHVCalendarDatePicker endDatePicker;
    private TimeTextField endTimePicker;

    private JPanel buttonPanel;

    private final JCheckBox exactDateCheckBox;

    private final PositionLoad positionLoad;

    CameraOptionPanelExpert(PositionLoad _positionLoad) {
        positionLoad = _positionLoad;

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(new JSeparator(SwingConstants.HORIZONTAL), c);

        JPanel loadedLabelPanel = new JPanel();
        loadedLabelPanel.setLayout(new BoxLayout(loadedLabelPanel, BoxLayout.LINE_AXIS));

        loadedLabel = new JLabel("Status: Not loaded");
        loadedLabelPanel.add(loadedLabel);
        c.gridy = 1;
        add(loadedLabelPanel, c);
        c.gridy = 2;
        add(new JSeparator(SwingConstants.HORIZONTAL), c);
        c.gridy = 3;

        addObjectCombobox(c);
        exactDateCheckBox = new JCheckBox("Use master layer timestamps", true);
        c.gridy = 4;
        add(exactDateCheckBox, c);
        c.gridy = 5;
        addBeginDatePanel(c);
        c.gridy = 6;
        addEndDatePanel(c);
        addBeginDatePanel.setVisible(false);
        addEndDatePanel.setVisible(false);
        c.gridy = 7;

        addSyncButtons(c);
        buttonPanel.setVisible(false);

        exactDateCheckBox.addActionListener(e -> {
            boolean selected = !exactDateCheckBox.isSelected();
            addBeginDatePanel.setVisible(selected);
            addEndDatePanel.setVisible(selected);
            buttonPanel.setVisible(selected);
            if (selected) {
                setBeginTime(false);
                setEndTime(true);
            }
        });

        ComponentUtils.smallVariant(this);
    }

    private void addSyncButtons(GridBagConstraints c) {
        JButton synchronizeWithLayersButton = new JButton("Sync");
        synchronizeWithLayersButton.setToolTipText("Fill selected layer dates");
        synchronizeWithLayersButton.addActionListener(e -> syncWithLayer());

        JButton synchronizeWithNowButton = new JButton("Now");
        synchronizeWithNowButton.setToolTipText("Fill twice current time");
        synchronizeWithNowButton.addActionListener(e -> syncBothLayerNow());

        JButton synchronizeWithCurrentButton = new JButton("Current");
        synchronizeWithCurrentButton.setToolTipText("Fill twice selected layer time");
        synchronizeWithCurrentButton.addActionListener(e -> syncWithLayerCurrentTime());

        buttonPanel = new JPanel(new GridLayout(0, 3));

        synchronizeWithLayersButton.getMaximumSize().width = 15;
        buttonPanel.add(synchronizeWithLayersButton);
        synchronizeWithCurrentButton.getMaximumSize().width = 15;
        buttonPanel.add(synchronizeWithCurrentButton);
        synchronizeWithNowButton.getMaximumSize().width = 15;
        buttonPanel.add(synchronizeWithNowButton);

        add(buttonPanel, c);
    }

    @Override
    void activate() {
        Layers.addLayersListener(this);
    }

    @Override
    void deactivate() {
        Layers.removeLayersListener(this);
    }

    @Override
    public void activeLayerChanged(View view) {
        if (exactDateCheckBox.isSelected()) {
            syncWithLayer();
        }
        // Displayer.render();
    }

    private void addObjectCombobox(GridBagConstraints c) {
        JSeparatorComboBox objectCombobox = new JSeparatorComboBox(SpaceObject.getObjectList().toArray());
        objectCombobox.setSelectedItem(SpaceObject.earth);
        objectCombobox.addActionListener(e -> {
            String object = ((SpaceObject) objectCombobox.getSelectedItem()).getUrlName();
            positionLoad.setObserver(object, true);
            // Displayer.render();
        });
        add(objectCombobox, c);
    }

    private void addBeginDatePanel(GridBagConstraints c) {
        beginDatePicker = new JHVCalendarDatePicker();
        beginDatePicker.getTextField().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setBeginTime(true);
            }
        });
        beginTimePicker = new TimeTextField();
        addBeginDatePanel = new JPanel();
        addBeginDatePanel.setLayout(new BoxLayout(addBeginDatePanel, BoxLayout.LINE_AXIS));

        JLabel beginDateLabel = new JLabel("Begin", JLabel.RIGHT);
        beginDateLabel.setPreferredSize(new Dimension(40, 0));

        addBeginDatePanel.add(beginDateLabel);

        beginDatePicker.addJHVCalendarListener(e -> setBeginTime(true));
        beginTimePicker.addActionListener(e -> setBeginTime(true));

        addBeginDatePanel.add(beginDatePicker);
        addBeginDatePanel.add(beginTimePicker);
        addBeginDatePanel.add(Box.createRigidArea(new Dimension(40, 0)));
        add(addBeginDatePanel, c);
    }

    private void setEndTime(boolean applyChanges) {
        positionLoad.setEndTime(endDatePicker.getTime() + endTimePicker.getTime(), applyChanges);
    }

    private void setBeginTime(boolean applyChanges) {
        positionLoad.setBeginTime(beginDatePicker.getTime() + beginTimePicker.getTime(), applyChanges);
    }

    @Override
    void syncWithLayer() {
        syncWithLayerBeginTime(false);
        syncWithLayerEndTime(true);
    }

    private void syncWithLayerBeginTime(boolean applyChanges) {
        View view = Layers.getActiveView();
        if (view == null)
            return;

        JHVDate startTime = view.getFirstTime();
        beginDatePicker.setTime(startTime.milli - startTime.milli % TimeUtils.DAY_IN_MILLIS);
        beginTimePicker.setText(TimeUtils.timeDateFormat.format(startTime.milli));
        setBeginTime(applyChanges);
    }

    private void syncBothLayerNow() {
        long now = System.currentTimeMillis();
        long syncTime = now - now % TimeUtils.DAY_IN_MILLIS;
        String timeText = TimeUtils.timeDateFormat.format(now);

        beginDatePicker.setTime(syncTime);
        beginTimePicker.setText(timeText);

        endDatePicker.setTime(syncTime);
        endTimePicker.setText(timeText);

        setBeginTime(false);
        setEndTime(true);
    }

    private void syncWithLayerCurrentTime() {
        JHVDate currentTime = Layers.getLastUpdatedTimestamp();
        long syncTime = currentTime.milli - currentTime.milli % TimeUtils.DAY_IN_MILLIS;
        String timeText = TimeUtils.timeDateFormat.format(currentTime.milli);

        endDatePicker.setTime(syncTime);
        endTimePicker.setText(timeText);

        beginDatePicker.setTime(syncTime);
        beginTimePicker.setText(timeText);

        setBeginTime(false);
        setEndTime(true);
    }

    private void syncWithLayerEndTime(boolean applyChanges) {
        View view = Layers.getActiveView();
        if (view == null)
            return;

        JHVDate endTime = view.getLastTime();
        endDatePicker.setTime(endTime.milli - endTime.milli % TimeUtils.DAY_IN_MILLIS);
        endTimePicker.setText(TimeUtils.timeDateFormat.format(endTime.milli));
        setEndTime(applyChanges);
    }

    private void addEndDatePanel(GridBagConstraints c) {
        endDatePicker = new JHVCalendarDatePicker();
        endDatePicker.getTextField().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setEndTime(true);
            }
        });
        endTimePicker = new TimeTextField();
        addEndDatePanel = new JPanel();
        addEndDatePanel.setLayout(new BoxLayout(addEndDatePanel, BoxLayout.LINE_AXIS));

        JLabel endDateLabel = new JLabel("End", JLabel.RIGHT);
        endDateLabel.setPreferredSize(new Dimension(40, 0));
        addEndDatePanel.add(endDateLabel);

        endDatePicker.addJHVCalendarListener(e -> setEndTime(true));
        endTimePicker.addActionListener(e -> setEndTime(true));

        addEndDatePanel.add(endDatePicker);
        addEndDatePanel.add(endTimePicker);
        addEndDatePanel.add(Box.createRigidArea(new Dimension(40, 0)));

        add(addEndDatePanel, c);
    }

    void fireLoaded(String state) {
        loadedLabel.setText("<html><body style='width: 200px'>Status: " + state);
    }

}
