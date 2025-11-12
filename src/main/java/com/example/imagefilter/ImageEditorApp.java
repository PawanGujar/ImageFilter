package com.example.imagefilter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * ImageEditorApp — Advanced Image Filter Studio GUI
 * Features:
 * - Aspect ratio preserved
 * - Multiple filters with chaining
 * - Adjustable blur
 * - Brightness/Contrast sliders
 * - Undo/Redo
 * - Drag & Drop support
 */
public class ImageEditorApp extends JFrame {

    private BufferedImage originalImage;
    private BufferedImage currentImage;
    private JLabel imageLabel;
    private final UndoManager undoManager = new UndoManager();

    // Sliders
    private JSlider brightnessSlider;
    private JSlider contrastSlider;
    private JSlider blurSlider;

    public ImageEditorApp() {
        setTitle("🖼 Image Filter Studio Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        setupUI();
    }

    private void setupUI() {
        JPanel topPanel = new JPanel();
        JButton openBtn = new JButton("📂 Open Image");
        JButton saveBtn = new JButton("💾 Save Image");
        JButton undoBtn = new JButton("↩ Undo");
        JButton redoBtn = new JButton("↪ Redo");

        topPanel.add(openBtn);
        topPanel.add(saveBtn);
        topPanel.add(undoBtn);
        topPanel.add(redoBtn);

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridLayout(3, 3, 8, 8));

        JButton grayBtn = new JButton("Grayscale");
        JButton sepiaBtn = new JButton("Sepia");
        JButton blurBtn = new JButton("Blur");
        JButton sharpenBtn = new JButton("Sharpen");
        JButton edgeBtn = new JButton("Edge Detect");
        JButton resetBtn = new JButton("Reset");

        filterPanel.add(grayBtn);
        filterPanel.add(sepiaBtn);
        filterPanel.add(blurBtn);
        filterPanel.add(sharpenBtn);
        filterPanel.add(edgeBtn);
        filterPanel.add(resetBtn);

        // Sliders panel
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new GridLayout(3, 2, 8, 8));
        brightnessSlider = new JSlider(-100, 100, 0);
        contrastSlider = new JSlider(0, 200, 100);
        blurSlider = new JSlider(1, 10, 1);

        sliderPanel.add(new JLabel("Brightness"));
        sliderPanel.add(brightnessSlider);
        sliderPanel.add(new JLabel("Contrast"));
        sliderPanel.add(contrastSlider);
        sliderPanel.add(new JLabel("Blur Radius"));
        sliderPanel.add(blurSlider);

        // Image display (scrollable, keeps aspect ratio)
        imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setBackground(Color.DARK_GRAY);
        imageLabel.setOpaque(true);
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(filterPanel, BorderLayout.WEST);
        add(sliderPanel, BorderLayout.SOUTH);

        // Actions
        openBtn.addActionListener(e -> openImage());
        saveBtn.addActionListener(e -> saveImage());
        resetBtn.addActionListener(e -> resetImage());
        undoBtn.addActionListener(e -> undo());
        redoBtn.addActionListener(e -> redo());

        grayBtn.addActionListener(e -> applyFilter("grayscale"));
        sepiaBtn.addActionListener(e -> applyFilter("sepia"));
        blurBtn.addActionListener(e -> applyFilter("blur"));
        sharpenBtn.addActionListener(e -> applyFilter("sharpen"));
        edgeBtn.addActionListener(e -> applyFilter("edge"));

        brightnessSlider.addChangeListener(this::updateBrightnessContrast);
        contrastSlider.addChangeListener(this::updateBrightnessContrast);

        // Drag & Drop
        new DropTarget(imageLabel, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    java.util.List<File> droppedFiles =
                            (java.util.List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!droppedFiles.isEmpty()) {
                        loadImage(droppedFiles.get(0));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadImage(chooser.getSelectedFile());
        }
    }

    private void loadImage(File file) {
        try {
            originalImage = ImageIO.read(file);
            currentImage = deepCopy(originalImage);
            undoManager.push(deepCopy(originalImage));
            displayImage(currentImage);
        } catch (Exception ex) {
            showError("Error loading image: " + ex.getMessage());
        }
    }

    private void saveImage() {
        if (currentImage == null) return;
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ImageIO.write(currentImage, "png", chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Image saved!");
            } catch (Exception ex) {
                showError("Error saving image: " + ex.getMessage());
            }
        }
    }

    private void resetImage() {
        if (originalImage != null) {
            currentImage = deepCopy(originalImage);
            undoManager.push(deepCopy(currentImage));
            displayImage(currentImage);
        }
    }

    private void undo() {
        BufferedImage img = undoManager.undo();
        if (img != null) {
            currentImage = deepCopy(img);
            displayImage(currentImage);
        }
    }

    private void redo() {
        BufferedImage img = undoManager.redo();
        if (img != null) {
            currentImage = deepCopy(img);
            displayImage(currentImage);
        }
    }

    private void applyFilter(String name) {
        if (currentImage == null) return;
        BufferedImage filtered = null;

        switch (name) {
            case "grayscale" -> filtered = ImageFilterPro.grayscale(currentImage);
            case "sepia" -> filtered = ImageFilterPro.sepia(currentImage);
            case "blur" -> filtered = ImageFilterPro.blur(currentImage, blurSlider.getValue());
            case "sharpen" -> filtered = ImageFilterPro.sharpen(currentImage);
            case "edge" -> filtered = ImageFilterPro.edgeDetect(currentImage);
        }

        if (filtered != null) {
            undoManager.push(deepCopy(filtered));
            currentImage = filtered;
            displayImage(currentImage);
        }
    }

    private void updateBrightnessContrast(ChangeEvent e) {
        if (currentImage == null) return;
        int brightness = brightnessSlider.getValue();
        double contrast = contrastSlider.getValue() / 100.0;
        BufferedImage adjusted = ImageFilterPro.adjustBrightnessContrast(currentImage, brightness, contrast);
        displayImage(adjusted);
    }

    private void displayImage(BufferedImage img) {
        if (img == null) return;
        ImageIcon icon = new ImageIcon(img);
        imageLabel.setIcon(icon);
        imageLabel.revalidate();
    }

    private static BufferedImage deepCopy(BufferedImage bi) {
        BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        return copy;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageEditorApp().setVisible(true));
    }
}
