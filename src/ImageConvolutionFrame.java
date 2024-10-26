import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class ImageConvolutionFrame extends JFrame {
    private ImagePanel imagePanel;
    private BufferedImage originalImage;
    private BufferedImage displayedImage;

    public ImageConvolutionFrame() {
        super("Image Convolution Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize menubar
        createMenuBar();

        JPanel buttonPanel = new JPanel();

        imagePanel = new ImagePanel();

        // Layout setup
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(imagePanel), BorderLayout.CENTER);

        // Maximize window
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu imageMenu = new JMenu("Image");

        
        JMenuItem openImage = new JMenuItem("Open Image");
        JMenuItem exportImage = new JMenuItem("Export Image");
        JMenuItem exitOption = new JMenuItem("Exit");
        
        
        JMenuItem applyKernel = new JMenuItem("Apply Kernel");
        JMenuItem removePreviousKernel = new JMenuItem("Remove Previous Kernel");


        fileMenu.add(openImage);
        fileMenu.add(exportImage);
        fileMenu.addSeparator();
        fileMenu.add(exitOption);

        imageMenu.add(applyKernel);
        imageMenu.add(removePreviousKernel);


        menuBar.add(fileMenu);
        menuBar.add(imageMenu);

        setJMenuBar(menuBar);

        openImage.addActionListener(e -> openImage());
        exportImage.addActionListener(e -> exportImage());
        exitOption.addActionListener(e -> dispose());

        applyKernel.addActionListener(e -> applyKernel());
        removePreviousKernel.addActionListener(e -> clearKernel());
    }

    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().matches(".*\\.(jpg|jpeg|png|pgm)$");
            }

            @Override
            public String getDescription() {
                return "Image Files (jpg, jpeg, png, pgm)";
            }
        });

        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                if (file.getName().endsWith(".pgm")) {
                    originalImage = PGMReader.readPGM(file.getPath());
                } else {
                    originalImage = ImageIO.read(file);
                }
                displayedImage = originalImage;
                imagePanel.setImage(displayedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportImage() {
        if(displayedImage == null) {
            JOptionPane.showMessageDialog(this, "Error saving image: No image is loaded!", "Error",
                    JOptionPane.ERROR_MESSAGE);
                    return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image");

        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().matches(".*\\.(jpg|jpeg|png|pgm)$");
            }

            @Override
            public String getDescription() {
                return "Image Files (jpg, jpeg, png, pgm)";
            }
        });

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filename = fileToSave.getAbsolutePath();

            int lastDotIndex = filename.lastIndexOf(".");

            if (lastDotIndex != -1 && lastDotIndex != filename.length() - 1) {
                String extension = filename.substring(lastDotIndex + 1);
                if (extension.equalsIgnoreCase("pgm")) {
                    try {
                        PGMWriter.writePGM(displayedImage, fileToSave);
                        JOptionPane.showMessageDialog(this, "Image saved successfully as a " + extension, "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                                "Error saving image as " + extension + ": " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    try {
                        ImageIO.write(displayedImage, extension, fileToSave);
                        JOptionPane.showMessageDialog(this, "Image saved successfully as a " + extension, "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error saving image as " + extension + ": " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                // Save as PNG.
                filename += ".png";
                try {
                    ImageIO.write(displayedImage, "png", fileToSave);
                    JOptionPane.showMessageDialog(this, "Image saved successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    private void applyKernel() {
        if (displayedImage == null) {
            JOptionPane.showMessageDialog(this,
                    "Please load an image!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Open dialog to input kernel
        KernelInputDialog dialog = new KernelInputDialog(this, displayedImage);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            float[] kernelData = dialog.getKernelData();
            int kernelWidth = dialog.getKernelWidth();
            int kernelHeight = dialog.getKernelHeight();

            if (kernelData != null && kernelData.length == kernelWidth * kernelHeight) {
                // Apply convolution
                originalImage = displayedImage;
                displayedImage = convolveImage(displayedImage, kernelData, kernelWidth, kernelHeight);
                imagePanel.setImage(displayedImage);
            }
        }
    }

    private void clearKernel() {
        displayedImage = originalImage;
        imagePanel.setImage(displayedImage);
    }

    private BufferedImage convolveImage(BufferedImage src, float[] kernelData, int kernelWidth, int kernelHeight) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage result = new BufferedImage(width, height, src.getType());

        int kernelHalfWidth = kernelWidth / 2;
        int kernelHalfHeight = kernelHeight / 2;

        for (int y = kernelHalfHeight; y < height - kernelHalfHeight; y++) {
            for (int x = kernelHalfWidth; x < width - kernelHalfWidth; x++) {
                float[] rgb = { 0f, 0f, 0f };

                for (int ky = -kernelHalfHeight; ky <= kernelHalfHeight; ky++) {
                    for (int kx = -kernelHalfWidth; kx <= kernelHalfWidth; kx++) {
                        int pixel = src.getRGB(x + kx, y + ky);
                        float kernelValue = kernelData[(ky + kernelHalfHeight) * kernelWidth + (kx + kernelHalfWidth)];

                        rgb[0] += ((pixel >> 16) & 0xff) * kernelValue; // Red
                        rgb[1] += ((pixel >> 8) & 0xff) * kernelValue; // Green
                        rgb[2] += (pixel & 0xff) * kernelValue; // Blue
                    }
                }

                int newPixel = (0xff << 24) | // Alpha
                        (clamp(rgb[0]) << 16) | // Red
                        (clamp(rgb[1]) << 8) | // Green
                        clamp(rgb[2]); // Blue

                result.setRGB(x, y, newPixel);
            }
        }

        return result;
    }

    private int clamp(float value) {
        return Math.min(255, Math.max(0, Math.round(value)));
    }
}
