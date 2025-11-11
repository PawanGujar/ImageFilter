package com.example.imagefilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * ImageFilterApp — a small Swing-based image filter studio.
 *
 * Features:
 * - Open image (JPG/PNG/GIF)
 * - Apply Grayscale / Sepia / Blur
 * - Preview result and save to file
 *
 * Run main() to start the app.
 *
 * @author Pawan Gujar
 * @version 1.0
 */
public class ImageFilterApp extends JFrame {

    private final JLabel imageLabel = new JLabel();
    private BufferedImage original;
    private BufferedImage working;

    public ImageFilterApp() {
        super("Image Filter Studio");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton openBtn = new JButton("Open");
        JButton saveBtn = new JButton("Save As...");
        JButton grayBtn = new JButton("Grayscale");
        JButton sepiaBtn = new JButton("Sepia");
        JButton blurBtn = new JButton("Blur");
        JButton resetBtn = new JButton("Reset");

        top.add(openBtn);
        top.add(saveBtn);
        top.add(grayBtn);
        top.add(sepiaBtn);
        top.add(blurBtn);
        top.add(resetBtn);

        add(top, BorderLayout.NORTH);

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);

        openBtn.addActionListener(e -> openImage());
        saveBtn.addActionListener(e -> saveImage());
        grayBtn.addActionListener(e -> applyFilter("grayscale"));
        sepiaBtn.addActionListener(e -> applyFilter("sepia"));
        blurBtn.addActionListener(e -> applyFilter("blur"));
        resetBtn.addActionListener(e -> resetImage());
    }

    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        int ok = chooser.showOpenDialog(this);
        if (ok == JFileChooser.APPROVE_OPTION) {
            try {
                File f = chooser.getSelectedFile();
                original = ImageIO.read(f);
                working = original;
                displayImage(working);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to open image: " + ex.getMessage());
            }
        }
    }

    private void saveImage() {
        if (working == null) { JOptionPane.showMessageDialog(this, "No image to save."); return; }
        JFileChooser chooser = new JFileChooser();
        int ok = chooser.showSaveDialog(this);
        if (ok == JFileChooser.APPROVE_OPTION) {
            try {
                File out = chooser.getSelectedFile();
                String ext = getExtension(out.getName());
                if (ext == null) ext = "png"; // default
                ImageIO.write(working, ext, out);
                JOptionPane.showMessageDialog(this, "Saved to " + out.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
            }
        }
    }

    private String getExtension(String name) {
        int i = name.lastIndexOf('.');
        if (i < 0) return null;
        return name.substring(i + 1).toLowerCase();
    }

    private void applyFilter(String name) {
        if (working == null) { JOptionPane.showMessageDialog(this, "Open an image first."); return; }
        try {
            switch (name) {
                case "grayscale" -> working = ImageFilter.applyGrayscale(working);
                case "sepia" -> working = ImageFilter.applySepia(working);
                case "blur" -> working = ImageFilter.applyBlur(working);
            }
            displayImage(working);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Filter failed: " + ex.getMessage());
        }
    }

    private void resetImage() {
        if (original != null) {
            working = original;
            displayImage(working);
        }
    }

    private void displayImage(BufferedImage img) {
        if (img == null) { imageLabel.setIcon(null); return; }
        int maxW = getWidth() - 50;
        int maxH = getHeight() - 150;
        Image scaled = img.getScaledInstance(Math.min(img.getWidth(), maxW), Math.min(img.getHeight(), maxH), Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageFilterApp app = new ImageFilterApp();
            app.setVisible(true);
        });
    }
}
