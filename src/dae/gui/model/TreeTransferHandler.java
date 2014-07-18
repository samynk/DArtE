/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui.model;

import dae.prefabs.ui.classpath.FileNode;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.COPY;
import javax.swing.tree.TreePath;

/**
 *
 * @author Koen Samyn
 */
public class TreeTransferHandler extends TransferHandler {

    private JTree tree;

    /**
     * Create a Transferable to use as the source for a data transfer.
     *
     * @param c The component holding the data to be transfered. This argument
     * is provided to enable sharing of TransferHandlers by multiple components.
     * @return The representation of the data to be transfered.
     *
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JTree) {
            tree = (JTree) c;
            TreePath path = tree.getSelectionPath();

            return (FileNode) path.getLastPathComponent();
        }

        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }
}