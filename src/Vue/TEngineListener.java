package Vue;

import Modele.Hexagone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class TEngineListener extends MouseAdapter implements MouseWheelListener {
    private TEngine tengine;

    public TEngineListener(TEngine t) {
        super();
        tengine = t;
    }

}
