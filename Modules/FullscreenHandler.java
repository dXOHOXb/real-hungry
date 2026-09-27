package Modules;

import javax.swing.*;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

public class FullscreenHandler extends AbstractAction {
  private JFrame frame;
  private GraphicsDevice fullscreenDevice;

  public FullscreenHandler(JFrame frame) {
    this.frame = frame;
    this.fullscreenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    frame.dispose();
    if (frame.isUndecorated()) {
      fullscreenDevice.setFullScreenWindow(null);
      frame.setUndecorated(false);
    } else {
      frame.setUndecorated(true);
      fullscreenDevice.setFullScreenWindow(frame);
    }

    frame.setVisible(true);
    frame.repaint();
  }
}