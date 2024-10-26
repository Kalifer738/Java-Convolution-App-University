import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

class ImagePanel extends JPanel {
    private Image image;

    public ImagePanel() {
        setBackground(Color.WHITE);
    }

    public void setImage(Image img) {
        this.image = img;
        revalidate();
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int imageWidth = image.getWidth(this);
            int imageHeight = image.getHeight(this);

            // Calculate scaling factors
            double widthScale = (double) panelWidth / imageWidth;
            double heightScale = (double) panelHeight / imageHeight;
            double scale = Math.min(widthScale, heightScale);

            // Do not scale up beyond original size
            scale = Math.min(scale, 1.0);

            int newWidth = (int) (imageWidth * scale);
            int newHeight = (int) (imageHeight * scale);

            // Center the image
            int x = (panelWidth - newWidth) / 2;
            int y = (panelHeight - newHeight) / 2;

            g.drawImage(image, x, y, newWidth, newHeight, this);
        }
    }

    public Dimension getPreferredSize() {
        if (image != null) {
            int panelWidth = getParent() != null ? getParent().getWidth() : image.getWidth(this);
            int panelHeight = getParent() != null ? getParent().getHeight() : image.getHeight(this);
            int imageWidth = image.getWidth(this);
            int imageHeight = image.getHeight(this);

            // Calculate scaling factors
            double widthScale = (double) panelWidth / imageWidth;
            double heightScale = (double) panelHeight / imageHeight;
            double scale = Math.min(widthScale, heightScale);

            // Do not scale up beyond original size
            scale = Math.min(scale, 1.0);

            int newWidth = (int) (imageWidth * scale);
            int newHeight = (int) (imageHeight * scale);

            if (newWidth < 250) {
                newWidth = 250;
            }
            if (newHeight < 250) {
                newHeight = 250;
            }


            return new Dimension(newWidth, newHeight);
        } else {
            Dimension prefferedSize = super.getPreferredSize();
            if(prefferedSize.getWidth() < 250) {
                prefferedSize.width = 250;
            }
            if(prefferedSize.getHeight() < 250) {
                prefferedSize.height = 250;
            }
            return prefferedSize;
        }
    }
}
