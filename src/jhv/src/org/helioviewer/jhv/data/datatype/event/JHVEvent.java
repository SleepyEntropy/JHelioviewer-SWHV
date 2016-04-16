package org.helioviewer.jhv.data.datatype.event;

import java.util.HashMap;
import java.util.Map;

import org.helioviewer.jhv.base.astronomy.Position;

public class JHVEvent {

    public final long start;
    public final long end;

    private final String eventName;
    private final String eventDisplayName;

    private final Map<String, JHVEventParameter> allParameters = new HashMap<String, JHVEventParameter>();
    private final Map<String, JHVEventParameter> allVisibleParameters = new HashMap<String, JHVEventParameter>();

    private final JHVEventType eventType;
    private JHVPositionInformation positionInformation = JHVPositionInformation.NULLINFO;
    private Position.Q earthPosition = null;
    private Integer id;

    public JHVEvent(String _eventName, String _eventDisplayName, JHVEventType _eventType, int _id, long _start, long _end) {
        eventName = _eventName.intern();
        eventDisplayName = _eventDisplayName.intern();
        eventType = _eventType;
        start = _start;
        end = _end;
        id = _id;
    }

    public Map<String, JHVEventParameter> getAllEventParameters() {
        return allParameters;
    }

    public Map<String, JHVEventParameter> getVisibleEventParameters() {
        return allVisibleParameters;
    }

    public String getName() {
        return eventName;
    }

    public String getDisplayName() {
        return eventDisplayName;
    }

    public JHVEventType getJHVEventType() {
        return eventType;
    }

    public JHVPositionInformation getPositioningInformation() {
        return positionInformation;
    }

    public Integer getUniqueID() {
        return id;
    }

    public void setUniqueID(Integer id) {
        this.id = id;
    }

    /**
     * Adds a parameter to the event.
     *
     * @param parameter
     *            the parameter to add
     * @param visible
     *            is the parameter visible
     * @param configured
     *            was the event in the configuration file
     */
    public void addParameter(JHVEventParameter parameter, boolean visible, boolean configured) {
        String name = parameter.getParameterName();

        allParameters.put(name, parameter);
        if (configured && visible) {
            allVisibleParameters.put(name, parameter);
        }
    }

    public void addJHVPositionInformation(JHVPositionInformation positionInformation) {
        this.positionInformation = positionInformation;
    }

    public void addEarthPosition(Position.Q p) {
        earthPosition = p;
    }

    public Position.Q getEarthPosition() {
        return earthPosition;
    }

    public void addParameter(String keyString, String value) {
        boolean visible = false;
        boolean configured = false;
        String displayName;

        SWEKParameter p = eventType.getEventType().getParameter(keyString);
        if (p == null) {
            p = eventType.getSupplier().getSource().getParameter(keyString);
        }

        if (p != null) {
            configured = true;
            visible = p.isDefaultVisible();
            displayName = p.getParameterDisplayName();
        } else {
            displayName = keyString.replaceAll("_", " ").trim();
        }

        JHVEventParameter parameter = new JHVEventParameter(keyString, displayName, value);
        addParameter(parameter, visible, configured);
    }

}
