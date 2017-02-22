package org.helioviewer.jhv.plugins.swek.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.helioviewer.jhv.gui.ComponentUtils;
import org.helioviewer.jhv.plugins.swek.model.SWEKTreeModelElement;
import org.helioviewer.jhv.plugins.swek.model.SWEKTreeModelEventType;
import org.helioviewer.jhv.plugins.swek.model.SWEKTreeModelSupplier;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("serial")
class SWEKEventTreeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object whatToDisplay, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (whatToDisplay instanceof SWEKTreeModelEventType) {
            return createLeaf(((SWEKTreeModelEventType) whatToDisplay).getSwekEventType().getEventName(), whatToDisplay, tree.getBackground());
        } else if (whatToDisplay instanceof SWEKTreeModelSupplier) {
            return createLeaf(((SWEKTreeModelSupplier) whatToDisplay).getSwekSupplier().getSupplierDisplayName(), whatToDisplay, tree.getBackground());
        } else {
            return super.getTreeCellRendererComponent(tree, whatToDisplay, selected, expanded, leaf, row, hasFocus);
        }
    }

    private static class TreeLabel extends JPanel {

        private final ImageIcon imageIcon;

        public TreeLabel(ImageIcon icon) {
            imageIcon = icon;
            int size = new JLabel("J").getPreferredSize().height;
            setPreferredSize(new Dimension(size, size));
        }

        @Override
        public void paintComponent(@NotNull Graphics g) {
            //super.paintComponent(g);
            Image image = imageIcon.getImage();
            int minDim = getWidth() < getHeight() ? getWidth() : getHeight();
            int diffx = (getWidth() - minDim) / 2;
            int diffy = (getHeight() - minDim) / 2;
            g.drawImage(image, diffx, diffy, diffx + minDim, diffy + minDim, 0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), this);
        }
    }

    /**
     * Creates a leaf of the tree. This leaf will be a panel with the name of
     * the leaf and a checkbox indicating whether the leaf was selected or not.
     *
     * @param name
     *            The name of the leaf
     * @param whatToDisplay
     *            What to be displayed
     * @return The panel to be placed in the tree
     */
    @NotNull
    private static JPanel createLeaf(String name, @NotNull Object whatToDisplay, Color back) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        ImageIcon icon = ((SWEKTreeModelElement) whatToDisplay).getIcon();
        if (icon != null) {
            panel.add(new TreeLabel(icon), BorderLayout.LINE_START);
        }

        JCheckBox checkBox = new JCheckBox(name);
        checkBox.setSelected(((SWEKTreeModelElement) whatToDisplay).isCheckboxSelected());
        checkBox.setBackground(back);
        panel.add(checkBox, BorderLayout.CENTER);

        ComponentUtils.smallVariant(panel);
        return panel;
    }

}
