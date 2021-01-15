import javax.swing.*;
import java.awt.*;

public class Window {
    final int WIDTH = 900;
    final int HEIGHT = 600;
    JFrame frame = new JFrame();

    public Window() {
        frame.setSize(WIDTH, HEIGHT);
        frame.setTitle("Аттрактор Лоренца");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.white);
        frame.setForeground(Color.white);
        frame.setVisible(true);
    }
}
