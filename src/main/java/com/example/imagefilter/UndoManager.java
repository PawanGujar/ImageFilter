package com.example.imagefilter;

import java.awt.image.BufferedImage;
import java.util.Stack;

/**
 * Manages undo/redo image history for ImageEditorApp.
 */
public class UndoManager {
    private final Stack<BufferedImage> undoStack = new Stack<>();
    private final Stack<BufferedImage> redoStack = new Stack<>();

    public void push(BufferedImage img) {
        if (img != null) {
            undoStack.push(img);
            redoStack.clear();
        }
    }

    public BufferedImage undo() {
        if (!undoStack.isEmpty()) {
            BufferedImage top = undoStack.pop();
            redoStack.push(top);
            return !undoStack.isEmpty() ? undoStack.peek() : top;
        }
        return null;
    }

    public BufferedImage redo() {
        if (!redoStack.isEmpty()) {
            BufferedImage img = redoStack.pop();
            undoStack.push(img);
            return img;
        }
        return null;
    }
}
