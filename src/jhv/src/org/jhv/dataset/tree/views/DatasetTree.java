package org.jhv.dataset.tree.views;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jhv.dataset.tree.models.DatasetIntervals;
import org.jhv.dataset.tree.models.DatasetNodeRenderer;
import org.jhv.dataset.tree.models.DatasetTreeCellEditor;


public class DatasetTree extends JTree{
	
	private static final long serialVersionUID = 3552416133364895287L;
	DefaultTreeModel model;
	
	public DatasetTree(DefaultTreeModel model){
		super(model);
		this.model = model;
		this.setCellRenderer( new DatasetNodeRenderer());
		this.setCellEditor(new DatasetTreeCellEditor(this, (DefaultTreeCellRenderer) this.getCellRenderer()));
		this.setRowHeight(30);
		this.setUI(new DatasetTreeUI());
		this.setEditable(true);
	}

}
