/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui.level;

import dae.GlobalObjects;
import dae.project.Level;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Koen Samyn
 */
public class LevelParameterPanel extends javax.swing.JPanel {
    
    private JFileChooser chooser;

    /**
     * Creates new form LevelParameterPanel
     */
    public LevelParameterPanel() {
        initComponents();
        GlobalObjects.getInstance().registerListener(this);
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter( new FileNameExtensionFilter( "j3o files","j3o"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        levelRendererPanel2 = new dae.gui.level.LevelRendererPanel();
        lblFiller = new javax.swing.JLabel();
        levelMainSettingsPanel1 = new dae.gui.level.LevelMainSettingsPanel();

        setMaximumSize(new java.awt.Dimension(640, 480));
        setMinimumSize(new java.awt.Dimension(0, 0));
        setPreferredSize(new java.awt.Dimension(0, 0));
        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(levelRendererPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.weighty = 1.0;
        add(lblFiller, gridBagConstraints);

        levelMainSettingsPanel1.setMinimumSize(new java.awt.Dimension(100, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(levelMainSettingsPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblFiller;
    private dae.gui.level.LevelMainSettingsPanel levelMainSettingsPanel1;
    private dae.gui.level.LevelRendererPanel levelRendererPanel2;
    // End of variables declaration//GEN-END:variables
}
