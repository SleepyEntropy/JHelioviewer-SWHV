package org.helioviewer.jhv.plugins.swek.model;

import java.util.ArrayList;
import java.util.List;

import org.helioviewer.jhv.data.event.SWEKEventType;
import org.helioviewer.jhv.data.event.SWEKSupplier;
import org.jetbrains.annotations.NotNull;

// The SWEK tree model representation of the SWEK event type
public class SWEKTreeModelEventType extends SWEKTreeModelElement {

    /** The swekEventType for this treemodel event type */
    @NotNull
    private final SWEKEventType swekEventType;

    /** List with SWEKSuppliers for this event type */
    private final List<SWEKTreeModelSupplier> swekTreeSuppliers = new ArrayList<>();

    /**
     * Creates a SWEK tree model event type for the given SWEK event type.
     * 
     * @param swekEventType
     *            The event type for which the SWEK tree model event type is
     *            created
     */
    public SWEKTreeModelEventType(@NotNull SWEKEventType _swekEventType) {
        super(false, _swekEventType.getEventIcon());
        swekEventType = _swekEventType;
        fillSWEKTreeSuppliers();
    }

    /**
     * Gets the event type of this SWEK tree model event type.
     * 
     * @return the SWEK event type
     */
    @NotNull
    public SWEKEventType getSwekEventType() {
        return swekEventType;
    }

    /**
     * Gets the list of SWEK tree model suppliers for this SWEK tree model event
     * type.
     * 
     * @return a list of SWEK tree model suppliers
     */
    @NotNull
    public List<SWEKTreeModelSupplier> getSwekTreeSuppliers() {
        return swekTreeSuppliers;
    }

    /**
     * Fills the list of SWEK tree model suppliers for this swek event type.
     */
    private void fillSWEKTreeSuppliers() {
        for (SWEKSupplier swekSupplier : swekEventType.getSuppliers()) {
            swekTreeSuppliers.add(new SWEKTreeModelSupplier(swekSupplier));
        }
    }

}
