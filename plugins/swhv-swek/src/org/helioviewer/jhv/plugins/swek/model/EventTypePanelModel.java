package org.helioviewer.jhv.plugins.swek.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.helioviewer.jhv.plugins.swek.download.SWEKDownloadManager;

/**
 * The model of the event type panel. This model is a TreeModel and is used by
 * the tree on the event type panel.
 * 
 * @author Bram Bourgoignie (Bram.Bourgoignie@oma.be)
 * 
 */
public class EventTypePanelModel implements TreeModel, TreeExpansionListener {

    /** The event type for this model */
    private final SWEKTreeModelEventType eventType;

    /** Holds the TreeModelListeners */
    private final List<TreeModelListener> listeners;

    /** Holds the EventPanelModelListeners */
    private final List<EventPanelModelListener> panelModelListeners;

    /** Local instance of the tree model */
    private final SWEKTreeModel treeModelInstance;

    /** Instance of the download manager */
    private final SWEKDownloadManager downloadManager;

    /**
     * Creates a SWEKTreeModel for the given SWEK event type.
     * 
     * @param eventType
     *            The event type for which to create the tree model
     */
    public EventTypePanelModel(SWEKTreeModelEventType eventType) {
        this.eventType = eventType;
        this.listeners = new ArrayList<TreeModelListener>();
        this.treeModelInstance = SWEKTreeModel.getSingletonInstance();
        this.panelModelListeners = new ArrayList<EventPanelModelListener>();
        this.downloadManager = SWEKDownloadManager.getSingletonInstance();
    }

    /**
     * Adds a new event panel model listener.
     * 
     * @param listener
     *            the listener to add
     */
    public void addEventPanelModelListener(EventPanelModelListener listener) {
        this.panelModelListeners.add(listener);
    }

    /**
     * Removes an event panel model listener.
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeEventPanelModelListener(EventPanelModelListener listener) {
        this.panelModelListeners.remove(listener);
    }

    /**
     * Informs the model about the row that was clicked. The clicked row will be
     * selected or unselected if it previously respectively was unselected or
     * selected.
     * 
     * @param row
     *            The row that was selected
     */
    public void rowClicked(int row) {
        if (row == 0) {
            this.eventType.setCheckboxSelected(!this.eventType.isCheckboxSelected());
            for (SWEKTreeModelSupplier supplier : this.eventType.getSwekTreeSuppliers()) {
                supplier.setCheckboxSelected(this.eventType.isCheckboxSelected());
            }
            if (this.eventType.isCheckboxSelected()) {
                this.downloadManager.startDownloadEventType(this.eventType.getSwekEventType());
            } else {
                this.downloadManager.stopDownloadingEventType(this.eventType.getSwekEventType());
            }
        } else if (row > 0 && row <= this.eventType.getSwekTreeSuppliers().size()) {
            SWEKTreeModelSupplier supplier = this.eventType.getSwekTreeSuppliers().get(row - 1);
            supplier.setCheckboxSelected(!supplier.isCheckboxSelected());
            if (supplier.isCheckboxSelected()) {
                this.eventType.setCheckboxSelected(true);
            }
            if (supplier.isCheckboxSelected()) {
                this.downloadManager.startDownloadEventType(this.eventType.getSwekEventType(), supplier.getSwekSupplier().getSource());
            } else {
                this.downloadManager.stopDownloadingEventType(this.eventType.getSwekEventType(), supplier.getSwekSupplier().getSource());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.
     * TreeModelListener)
     */
    @Override
    public void addTreeModelListener(TreeModelListener l) {
        this.listeners.add(l);

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof SWEKTreeModelEventType) {
            return ((SWEKTreeModelEventType) parent).getSwekTreeSuppliers().get(index);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof SWEKTreeModelEventType) {
            return ((SWEKTreeModelEventType) parent).getSwekEventType().getSuppliers().size();
        } else {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if ((parent instanceof SWEKTreeModelEventType) && (child instanceof SWEKTreeModelSupplier)) {
            int count = 0;
            for (SWEKTreeModelSupplier supplier : ((SWEKTreeModelEventType) parent).getSwekTreeSuppliers()) {
                if (supplier.equals(child)) {
                    return count;
                } else {
                    count++;
                }
            }
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    @Override
    public Object getRoot() {
        return this.eventType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    @Override
    public boolean isLeaf(Object node) {
        if (node instanceof SWEKTreeModelEventType) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.
     * TreeModelListener)
     */
    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        this.listeners.remove(l);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
     * java.lang.Object)
     */
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.event.TreeExpansionListener#treeCollapsed(javax.swing.event
     * .TreeExpansionEvent)
     */
    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        this.treeModelInstance.subTreeCollapsed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.event.TreeExpansionListener#treeExpanded(javax.swing.event
     * .TreeExpansionEvent)
     */
    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        this.treeModelInstance.subTreeExpanded();
    }
}
