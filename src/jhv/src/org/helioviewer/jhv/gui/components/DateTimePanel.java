package org.helioviewer.jhv.gui.components;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.helioviewer.jhv.base.time.TimeUtils;
import org.helioviewer.jhv.gui.components.base.TimeTextField;
import org.helioviewer.jhv.gui.components.calendar.JHVCalendarDatePicker;
import org.helioviewer.jhv.gui.components.calendar.JHVCalendarEvent;
import org.helioviewer.jhv.gui.components.calendar.JHVCalendarListener;

@SuppressWarnings("serial")
public class DateTimePanel extends JPanel implements ActionListener, JHVCalendarListener {

    private final HashSet<ActionListener> listeners = new HashSet<>();
    private final JHVCalendarDatePicker datePicker;
    private final TimeTextField timePicker;

    public DateTimePanel(String text) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        datePicker = new JHVCalendarDatePicker();
        timePicker = new TimeTextField();
        datePicker.addJHVCalendarListener(this);
        timePicker.addActionListener(this);

        JLabel label = new JLabel(text, JLabel.RIGHT);
        label.setPreferredSize(new Dimension(40, 0));

        add(label);
        add(datePicker);
        add(timePicker);
        add(Box.createRigidArea(new Dimension(40, 0)));
    }

    public long getTime() {
        return datePicker.getTime() + timePicker.getTime();
    }

    public void setTime(long time) {
        long timeDate = time - time % TimeUtils.DAY_IN_MILLIS;
        String timeText = TimeUtils.timeDateFormat.format(time);

        datePicker.setTime(timeDate);
        timePicker.setText(timeText);
    }

    public void addListener(ActionListener l) {
        listeners.add(l);
    }

    public void removeListener(ActionListener l) {
        listeners.remove(l);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (ActionListener l : listeners) {
            l.actionPerformed(e);
        }
    }

    @Override
    public void actionPerformed(JHVCalendarEvent e) {
        actionPerformed(new ActionEvent(this, 0, null));
    }

}
