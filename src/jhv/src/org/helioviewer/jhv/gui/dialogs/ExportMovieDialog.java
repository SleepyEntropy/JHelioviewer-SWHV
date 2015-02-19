package org.helioviewer.jhv.gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.helioviewer.base.FileUtils;
import org.helioviewer.base.logging.Log;
import org.helioviewer.base.message.Message;
import org.helioviewer.gl3d.view.GL3DComponentView;
import org.helioviewer.jhv.display.Displayer;
import org.helioviewer.jhv.gui.ImageViewerGui;
import org.helioviewer.jhv.gui.interfaces.ShowableDialog;
import org.helioviewer.viewmodel.view.AbstractComponentView;

/**
 * Dialog o export movies to standard video formats.
 *
 * <p>
 * This class includes everything needed to export movies to an external format.
 * Therefore, it copies the existing view chain and performs all its operations
 * on this copy. The movie is produced by invoking the ffmpeg exectuable and
 * piping bmp images to the ffmpeg process.
 *
 * @author Markus Langenberg
 * @author Andre Dau
 */
public class ExportMovieDialog extends JDialog implements ActionListener, ShowableDialog {

    private static final long serialVersionUID = 1L;

    private final JLabel movieLabel = new JLabel("", SwingConstants.CENTER);
    final JButton exportButton = new JButton("Click to start export");

    public void setLabelText(String exportingText) {
        this.movieLabel.setText(exportingText);
    }

    public void reset3D() {
        setVisible(false);
        remove(movieLabel);
        this.exportButton.setEnabled(true);
        this.exportButton.setVisible(true);
    }

    private class CloseDialogTask extends TimerTask {
        @Override
        public void run() {
            reset3D();
        }
    }

    public void fail() {
        this.movieLabel.setText("No image series. Aborting...");
        Timer timer = new Timer();
        timer.schedule(new CloseDialogTask(), 2000);
    }

    /**
     * Default constructor
     */
    public ExportMovieDialog() {
        super(ImageViewerGui.getMainFrame(), "Export Movie", true);
        ImageViewerGui.getSingletonInstance().getLeftContentPane().setEnabled(false);

        final AbstractComponentView component = ImageViewerGui.getSingletonInstance().getMainView().getAdapter(GL3DComponentView.class);
        final ExportMovieDialog exportMovieDialog = this;

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                add(movieLabel);
                movieLabel.setText("Export started...");
                component.startExport(exportMovieDialog);
                exportButton.setEnabled(false);
                exportButton.setVisible(false);
            }
        });
        this.add(exportButton);
    }

    @Override
    public void init() {
        ImageViewerGui.getSingletonInstance().getLeftContentPane().setEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showDialog() {

        if (!FileUtils.isExecutableRegistered("mp4box")) {
            Message.err("Could not find MP4Box tool", "The MP4Box tool could not be found. Exported movie will not contain subtitles.", false);
            Log.error(">> ExportMovieDialog > The MP4Box tool could not be found. Exported movie will not contain subtitles.");
        }

        if (!FileUtils.isExecutableRegistered("ffmpeg")) {
            Message.err("Could not find FFmpeg executable", "Movie export will not work. However, you can try to export image series.", false);
            Log.error(">> ExportMovieDialog > Could not find FFmpeg executable");
        }

        pack();
        setSize(new Dimension(180, 60));

        setLocationRelativeTo(ImageViewerGui.getMainFrame());
        setVisible(true);
        repaint();
    }

    synchronized public void release3d() {
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
    }

}