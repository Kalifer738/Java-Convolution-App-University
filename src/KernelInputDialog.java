import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Dialog to input convolution kernel
class KernelInputDialog extends JDialog {
    private JTextField kernelField;
    private JTextField widthField;
    private JTextField heightField;
    private JButton applyButton;
    private JButton previewButton;
    private boolean confirmed = false;
    private float[] kernelData;
    private int kernelWidth;
    private int kernelHeight;

    public KernelInputDialog(Frame owner, BufferedImage image) {
        super(owner, "Input Convolution Kernel", true);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Kernel Width:"));
        widthField = new JTextField("3");
        inputPanel.add(widthField);
        inputPanel.add(new JLabel("Kernel Height:"));
        heightField = new JTextField("3");
        inputPanel.add(heightField);
        inputPanel.add(new JLabel("Kernel Data (space-separated):"));
        kernelField = new JTextField("0 -1 0 -1 5 -1 0 -1 0");
        inputPanel.add(kernelField);

        applyButton = new JButton("Apply");
        previewButton = new JButton("Preview");

        add(inputPanel, BorderLayout.CENTER);
        add(applyButton, BorderLayout.SOUTH);
        add(previewButton, BorderLayout.LINE_END);

        // Action listener for apply button
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyButtonClick();
            }
        });

        previewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previewButtonClick(image);
            }
        });

        pack();
        setLocationRelativeTo(owner);
    }

    private void applyButtonClick() {
        if (parseFormData()) {
            confirmed = true;
            dispose();
        }
    }

    private boolean parseFormData() {
        try {
            kernelWidth = Integer.parseInt(widthField.getText());
            kernelHeight = Integer.parseInt(heightField.getText());
            String[] tokens = kernelField.getText().split("\\s+");
            kernelData = new float[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                kernelData[i] = Float.parseFloat(tokens[i]);
            }
            if (kernelData.length != kernelWidth * kernelHeight) {
                JOptionPane.showMessageDialog(KernelInputDialog.this,
                        "Kernel data size does not match width and height.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                return true;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(KernelInputDialog.this, "Invalid number format.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    private void previewButtonClick(BufferedImage image) {
        if (image == null) {
            JOptionPane.showMessageDialog(this,
                    "Please load an image!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (parseFormData()) {
            PreviewKernalDialog previewDialog = new PreviewKernalDialog(this, image);
            previewDialog.setVisible(true);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public float[] getKernelData() {
        return kernelData;
    }

    public int getKernelWidth() {
        return kernelWidth;
    }

    public int getKernelHeight() {
        return kernelHeight;
    }
}
