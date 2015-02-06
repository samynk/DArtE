package dae.gui.components;

import dae.prefabs.ui.classpath.FileNode;
import dae.project.Project;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Koen Samyn
 */
public class SoundInfo extends javax.swing.JPanel implements LineListener, ActionListener, ChangeListener {

    private FileNode currentFileNode;
    private Project project;
    private Clip currentClip;
    private Timer playerTimer;
    private int sampleSizeInBits;

    /**
     * Creates new form SoundInfo
     */
    public SoundInfo() {
        initComponents();
        playerTimer = new Timer(50, this);
    }

    public void activatePlayer() {
        playerTimer.start();
    }

    public void deactivatePlayer() {
        if (currentClip != null) {
            currentClip.close();
        }
        playerTimer.stop();
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

        pnlCommonProperties = new javax.swing.JPanel();
        lblChannels = new javax.swing.JLabel();
        txtChannels = new javax.swing.JTextField();
        lblSampleRate = new javax.swing.JLabel();
        txtSampleRate = new javax.swing.JTextField();
        lblBitsPerSample = new javax.swing.JLabel();
        txtBitsPerSample = new javax.swing.JTextField();
        lblBitRate = new javax.swing.JLabel();
        txtBitRate = new javax.swing.JTextField();
        nonCollapsibleHeader1 = new dae.gui.components.NonCollapsibleHeader();
        pnlMiniPlayer = new javax.swing.JPanel();
        sliderPosition = new javax.swing.JSlider();
        lblFiller = new javax.swing.JLabel();
        btnStop = new javax.swing.JButton();
        btnRewind = new javax.swing.JButton();
        btnFastForward = new javax.swing.JButton();
        btnPlayPause = new javax.swing.JToggleButton();
        lblEndTime = new javax.swing.JLabel();
        nonCollapsibleHeader2 = new dae.gui.components.NonCollapsibleHeader();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        pnlCommonProperties.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        pnlCommonProperties.setLayout(new java.awt.GridBagLayout());

        lblChannels.setText("Channels:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlCommonProperties.add(lblChannels, gridBagConstraints);

        txtChannels.setEditable(false);
        txtChannels.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 1, 2);
        pnlCommonProperties.add(txtChannels, gridBagConstraints);

        lblSampleRate.setText("Sample rate:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlCommonProperties.add(lblSampleRate, gridBagConstraints);

        txtSampleRate.setEditable(false);
        txtSampleRate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 1, 2);
        pnlCommonProperties.add(txtSampleRate, gridBagConstraints);

        lblBitsPerSample.setText("Bits per sample:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlCommonProperties.add(lblBitsPerSample, gridBagConstraints);

        txtBitsPerSample.setEditable(false);
        txtBitsPerSample.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 1, 2);
        pnlCommonProperties.add(txtBitsPerSample, gridBagConstraints);

        lblBitRate.setText("Bits per sample:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlCommonProperties.add(lblBitRate, gridBagConstraints);

        txtBitRate.setEditable(false);
        txtBitRate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 1, 2);
        pnlCommonProperties.add(txtBitRate, gridBagConstraints);

        nonCollapsibleHeader1.setTitle("Sound properties");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlCommonProperties.add(nonCollapsibleHeader1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.weightx = 1.0;
        add(pnlCommonProperties, gridBagConstraints);

        pnlMiniPlayer.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        pnlMiniPlayer.setLayout(new java.awt.GridBagLayout());

        sliderPosition.setMajorTickSpacing(10);
        sliderPosition.setMinorTickSpacing(1);
        sliderPosition.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 2);
        pnlMiniPlayer.add(sliderPosition, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.weighty = 1.0;
        pnlMiniPlayer.add(lblFiller, gridBagConstraints);

        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/miniplayer/control_stop_blue.png"))); // NOI18N
        btnStop.setEnabled(false);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlMiniPlayer.add(btnStop, gridBagConstraints);

        btnRewind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/miniplayer/control_rewind_blue.png"))); // NOI18N
        btnRewind.setEnabled(false);
        btnRewind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRewindActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlMiniPlayer.add(btnRewind, gridBagConstraints);

        btnFastForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/miniplayer/control_fastforward_blue.png"))); // NOI18N
        btnFastForward.setToolTipText("");
        btnFastForward.setEnabled(false);
        btnFastForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFastForwardActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlMiniPlayer.add(btnFastForward, gridBagConstraints);

        btnPlayPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/miniplayer/control_play_blue.png"))); // NOI18N
        btnPlayPause.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/miniplayer/control_play_blue.png"))); // NOI18N
        btnPlayPause.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/miniplayer/control_pause_blue.png"))); // NOI18N
        btnPlayPause.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnPlayPauseItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlMiniPlayer.add(btnPlayPause, gridBagConstraints);

        lblEndTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEndTime.setMaximumSize(null);
        lblEndTime.setMinimumSize(new java.awt.Dimension(120, 20));
        lblEndTime.setPreferredSize(new java.awt.Dimension(120, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlMiniPlayer.add(lblEndTime, gridBagConstraints);

        nonCollapsibleHeader2.setTitle("Mini player");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        pnlMiniPlayer.add(nonCollapsibleHeader2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(pnlMiniPlayer, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        add(jLabel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnRewindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRewindActionPerformed
    }//GEN-LAST:event_btnRewindActionPerformed

    private void btnFastForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFastForwardActionPerformed
    }//GEN-LAST:event_btnFastForwardActionPerformed

    private void btnPlayPauseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnPlayPauseItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            File audioFile = project.getResourceAsFile(currentFileNode.getFullName());
            testPlay(audioFile);
        } else {
            if (currentClip != null && currentClip.isRunning()) {
                currentClip.stop();
            }
        }
    }//GEN-LAST:event_btnPlayPauseItemStateChanged

    public void setFileNode(FileNode fileNode) {
        this.currentFileNode = fileNode;

        if (currentClip != null) {
            currentClip.removeLineListener(this);
            currentClip.close();
        }
        doOgg();
    }

    public void testPlay(File file) {
        AudioInputStream in = null;
        try {
            in = AudioSystem.getAudioInputStream(file);
            AudioInputStream din;
            if (in != null) {
                AudioFormat baseFormat = in.getFormat();
                System.out.println("Bits from input stream : " + baseFormat.getSampleSizeInBits());
                System.out.println("Bits from file : " + sampleSizeInBits);
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false);
                // Get AudioInputStream that will be decoded by underlying VorbisSPI
                din = AudioSystem.getAudioInputStream(decodedFormat, in);
                // Play now !
                currentClip = AudioSystem.getClip();
                currentClip.open(din);
                currentClip.start();

                sliderPosition.setValue(0);
                sliderPosition.setMaximum(currentClip.getFrameLength());
                long millis = currentClip.getMicrosecondLength() / 1000;
                long second = (millis / 1000) % 60;
                long minute = (millis / (1000 * 60)) % 60;
                long hour = (millis / (1000 * 60 * 60)) % 24;

                String time ;
                if (hour != 0) {
                    time = String.format("%02d:%02d:%02d:%d", hour, minute, second, millis);
                } else if (minute != 0) {
                    time = String.format("%02d:%02d:%d", minute, second, millis);
                } else if (second != 0) {
                    time = String.format("%02d:%d", second, millis);
                } else {
                    time = String.format("%d", millis);
                }
                lblEndTime.setText(time + " ms");

                currentClip.addLineListener(this);
            }
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger("DArtE").log(Level.INFO, "Could not play :  " + file.getPath(), ex);
        } catch (IOException ex) {
            Logger.getLogger("DArtE").log(Level.INFO, "Could not play :  " + file.getPath(), ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger("DArtE").log(Level.INFO, "No audio line available for : " + file.getPath(), ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger("DArtE").log(Level.INFO,  "Could not close " + file.getPath(),ex);
            }
        }

    }

    private void rawplay(AudioFormat targetFormat,
            AudioInputStream din) throws IOException, LineUnavailableException {
        byte[] data = new byte[4096];
        SourceDataLine line = getLine(targetFormat);
        if (line != null) {
            // Start
            line.start();
            int nBytesRead = 0, nBytesWritten = 0;
            while (nBytesRead != -1) {
                nBytesRead = din.read(data, 0, data.length);
                if (nBytesRead != -1) {
                    nBytesWritten = line.write(data, 0, nBytesRead);
                }
            }
            // Stop
            line.drain();
            line.stop();
            line.close();
            din.close();
        }
    }

    private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);
        return res;
    }

    private void doOgg() {
        SwingWorker worker = new SwingWorker<AudioFileFormat, Void>() {
            @Override
            protected AudioFileFormat doInBackground() throws Exception {
                String fullName = currentFileNode.getFullName();
                URL resourceURL = project.getResource(fullName);
                File resourceFile = new File(resourceURL.toURI());
                if (resourceFile.exists()) {
                    AudioFileFormat format = AudioSystem.getAudioFileFormat(resourceFile);
                    return format;
                } else {
                    return null;
                }
            }

            @Override
            protected void done() {
                AudioFileFormat format;
                try {
                    format = get();
                    if (format != null) {
                        int channels = format.getFormat().getChannels();
                        float sampleRate = format.getFormat().getSampleRate();
                        sampleSizeInBits = format.getFormat().getSampleSizeInBits();
                        updateTextFields(channels, sampleRate, sampleSizeInBits);
                    } else {
                        clearTextFiels();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(SoundInfo.class
                            .getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(SoundInfo.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }
        };
        worker.execute();
    }

    private void clearTextFiels() {
        txtBitRate.setText("");
        txtBitsPerSample.setText("");
        txtChannels.setText("");
        txtSampleRate.setText("");
    }

    /**
     * Sets the current project.
     *
     * @param project the current project.
     */
    public void setProject(Project project) {
        this.project = project;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFastForward;
    private javax.swing.JToggleButton btnPlayPause;
    private javax.swing.JButton btnRewind;
    private javax.swing.JButton btnStop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblBitRate;
    private javax.swing.JLabel lblBitsPerSample;
    private javax.swing.JLabel lblChannels;
    private javax.swing.JLabel lblEndTime;
    private javax.swing.JLabel lblFiller;
    private javax.swing.JLabel lblSampleRate;
    private dae.gui.components.NonCollapsibleHeader nonCollapsibleHeader1;
    private dae.gui.components.NonCollapsibleHeader nonCollapsibleHeader2;
    private javax.swing.JPanel pnlCommonProperties;
    private javax.swing.JPanel pnlMiniPlayer;
    private javax.swing.JSlider sliderPosition;
    private javax.swing.JTextField txtBitRate;
    private javax.swing.JTextField txtBitsPerSample;
    private javax.swing.JTextField txtChannels;
    private javax.swing.JTextField txtSampleRate;
    // End of variables declaration//GEN-END:variables

    private void updateTextFields(int channels, float sampleRate, int bits) {
        txtChannels.setText(channels == 1 ? "mono" : "stereo");

        int unit = 1000;
        if (sampleRate < unit) {
            txtSampleRate.setText(sampleRate + " Hz");
        }
        int exp = (int) (Math.log(sampleRate) / Math.log(unit));
        char pre = ("kMGTPE").charAt(exp - 1);
        txtSampleRate.setText(String.format("%.1f %cHz", sampleRate / Math.pow(unit, exp), pre));

        txtBitsPerSample.setText("" + bits);
    }

    public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.START) {
            System.out.println("sound clip started");
        } else if (event.getType() == LineEvent.Type.STOP) {
            System.out.println("sound clip stopped!");
            btnPlayPause.setSelected(false);
            sliderPosition.setValue(0);
        }
    }

    public void actionPerformed(ActionEvent e) {
        //System.out.println("update position");
        if (currentClip != null && currentClip.isRunning()) {
            int framePos = currentClip.getFramePosition();
            //System.out.println("current clip is running : " + framePos);
            sliderPosition.removeChangeListener(this);
            sliderPosition.setValue(framePos);
            sliderPosition.addChangeListener(this);
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
        }
    }
}
