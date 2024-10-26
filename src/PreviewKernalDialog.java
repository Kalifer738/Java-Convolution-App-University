import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.JDialog;

public class PreviewKernalDialog extends JDialog {
    public PreviewKernalDialog(KernelInputDialog owner, BufferedImage image) {
        super(owner, "Preview Convolution Kernel", true);
        setLayout(new BorderLayout());

        image = convolveImage(image, owner.getKernelData(), owner.getKernelWidth(), owner.getKernelHeight());

        ImagePanel imagePanel = new ImagePanel();
        imagePanel.setImage(image);
        add(imagePanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(owner);
    }

    private static BufferedImage convolveImage(BufferedImage src, float[] kernelData, int kernelWidth, int kernelHeight) {
        ConvolveOp op = new ConvolveOp(new Kernel(kernelWidth, kernelHeight, kernelData), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(src, null);
    }
}
