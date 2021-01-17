import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Window extends Lorenz implements ActionListener, MouseListener, MouseWheelListener {
    //  Text field
    static JTextField fieldParameterA;
    static JTextField fieldParameterB;
    static JTextField fieldParameterC;
    static JTextField fieldNormalVectorX;
    static JTextField fieldNormalVectorY;
    static JTextField fieldNormalVectorZ;
    static JTextField fieldIterationsCount;
    //  Size of frame
    final int WIDTH = 900;
    final int HEIGHT = 600;
    //  Frame
    JFrame frame;
    //  Panels
    JPanel containerPanel;
    JPanel controlPanel;
    //  Labels
    JLabel labelParameters;
    JLabel labelNormalVector;
    JLabel labelIterationsCount;
    //  Buttons
    JButton buttonReset;
    JButton buttonDraw;

    GridBagConstraints gridBagConstraints;

    KeyListener listener;

    public Window() {
        listener = new KeyListener() {
            /**
             * Invoked when a key has been typed.
             * See the class description for {@link KeyEvent} for a definition of
             * a key typed event.
             *
             * @param e the event to be processed
             */
            @Override
            public void keyTyped(KeyEvent e) {
                //  no operation
            }

            /**
             * Invoked when a key has been pressed.
             * See the class description for {@link KeyEvent} for a definition of
             * a key pressed event.
             *
             * @param e the event to be processed
             */
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> up();
                    case KeyEvent.VK_DOWN -> down();
                    case KeyEvent.VK_LEFT -> left();
                    case KeyEvent.VK_RIGHT -> right();
                    case KeyEvent.VK_PLUS, KeyEvent.VK_EQUALS -> plus();
                    case KeyEvent.VK_MINUS -> minus();
                    case KeyEvent.VK_A -> rotateLeft();
                    case KeyEvent.VK_D -> rotateRight();
                }
            }

            /**
             * Invoked when a key has been released.
             * See the class description for {@link KeyEvent} for a definition of
             * a key released event.
             *
             * @param e the event to be processed
             */
            @Override
            public void keyReleased(KeyEvent e) {
                //  no operation
            }
        };
        frame = new JFrame();
        frame.addKeyListener(listener);
        frame.setSize(WIDTH, HEIGHT);
        frame.setTitle("Аттрактор Лоренца");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.white);
        frame.setForeground(Color.white);
        controlPanel = new JPanel();
        controlPanel.addKeyListener(listener);
        controlPanel.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.EAST;

        //  Parameters
        labelParameters = new JLabel("Параметры (σ, r, b): ");
        labelParameters.addKeyListener(listener);
        controlPanel.add(labelParameters, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        fieldParameterA = new JTextField("10");
        fieldParameterA.setColumns(5);
        controlPanel.add(fieldParameterA, gridBagConstraints);

        fieldParameterB = new JTextField("28");
        fieldParameterB.setColumns(5);
        gridBagConstraints.gridx = 3;
        controlPanel.add(fieldParameterB, gridBagConstraints);

        fieldParameterC = new JTextField("2.6666");
        fieldParameterC.setColumns(5);
        gridBagConstraints.gridx = 4;
        controlPanel.add(fieldParameterC, gridBagConstraints);

        //  Reset
        buttonReset = new JButton("Сбросить");
        buttonReset.addKeyListener(listener);
        gridBagConstraints.gridx = 5;
        controlPanel.add(buttonReset, gridBagConstraints);
        buttonReset.addActionListener(this);

        //  Normal vector
        labelNormalVector = new JLabel("Вектор нормали: ");
        labelNormalVector.addKeyListener(listener);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        controlPanel.add(labelNormalVector, gridBagConstraints);

        fieldNormalVectorX = new JTextField("1");
        fieldNormalVectorX.setColumns(5);
        gridBagConstraints.gridx = 2;
        controlPanel.add(fieldNormalVectorX, gridBagConstraints);

        fieldNormalVectorY = new JTextField("1");
        fieldNormalVectorY.setColumns(5);
        gridBagConstraints.gridx = 3;
        controlPanel.add(fieldNormalVectorY, gridBagConstraints);

        fieldNormalVectorZ = new JTextField("1");
        fieldNormalVectorZ.setColumns(5);
        gridBagConstraints.gridx = 4;
        controlPanel.add(fieldNormalVectorZ, gridBagConstraints);

        buttonDraw = new JButton("Нарисовать");
        gridBagConstraints.gridx = 5;
        buttonDraw.addKeyListener(listener);
        buttonDraw.addActionListener(this);
        controlPanel.add(buttonDraw, gridBagConstraints);

        //  Iterations count
        labelIterationsCount = new JLabel("Количество итераций: ");
        labelIterationsCount.addKeyListener(listener);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        controlPanel.add(labelIterationsCount, gridBagConstraints);
        fieldIterationsCount = new JTextField("60000");
        fieldIterationsCount.setColumns(5);
        gridBagConstraints.gridx = 2;
        controlPanel.add(fieldIterationsCount, gridBagConstraints);
        controlPanel.setBackground(Color.getHSBColor(255, 210, 133));

        containerPanel = new JPanel();
        containerPanel.addKeyListener(listener);
        containerPanel.addMouseListener(this);
        containerPanel.addMouseWheelListener(this);
        containerPanel.setLayout(new BorderLayout());
        containerPanel.add(controlPanel, BorderLayout.NORTH);
        containerPanel.add(this, BorderLayout.CENTER);
        containerPanel.setDoubleBuffered(true);
        frame.setFocusable(true);
        frame.add(containerPanel);
        frame.setVisible(true);
        init();
    }

    public static double[] getNormalVector() {
        double[] normalVector = new double[3];
        normalVector[0] = Double.parseDouble(fieldNormalVectorX.getText());
        normalVector[1] = Double.parseDouble(fieldNormalVectorY.getText());
        normalVector[2] = Double.parseDouble(fieldNormalVectorZ.getText());
        return normalVector;
    }

    public static double[] getParameters() {
        double[] parameters = new double[3];
        parameters[0] = Double.parseDouble(fieldParameterA.getText());
        parameters[1] = Double.parseDouble(fieldParameterB.getText());
        parameters[2] = Double.parseDouble(fieldParameterC.getText());
        return parameters;
    }

    public static long getIterationCount() {
        return Long.parseLong(fieldIterationsCount.getText());
    }

    /**
     * Invoked when an action occurs.
     *
     * @param event the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source.equals(buttonReset)) {
            fieldNormalVectorX.setText("1.0");
            fieldNormalVectorY.setText("1.0");
            fieldNormalVectorZ.setText("1.0");
            fieldParameterA.setText("10.0");
            fieldParameterB.setText("28.0");
            fieldParameterC.setText("2.6666");
            fieldIterationsCount.setText("60000");
            draw();
            repaint();
        } else if (source.equals(buttonDraw)) {
            draw();
            repaint();
        }
    }

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
            case 1 -> rotateLeft();
            case 2 -> rotateNull();
            case 3 -> rotateRight();
        }
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Invoked when the mouse wheel is rotated.
     *
     * @param e the event to be processed
     * @see MouseWheelEvent
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() == 1)
            minus();
        else if (e.getWheelRotation() == -1)
            plus();
    }
}
