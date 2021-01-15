import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Window implements ActionListener {
    //  Size of frame
    final int WIDTH = 900;
    final int HEIGHT = 600;

    //  Frame
    JFrame frame;

    //  Panels
    JPanel containerPanel;
    JPanel controlPanel;
    JPanel movePanel;

    //  Labels
    JLabel labelParameters;
    JLabel labelNormalVector;
    JLabel labelIterationsCount;

    //  Text field
    JTextField fieldParameterA;
    JTextField fieldParameterB;
    JTextField fieldParameterC;
    JTextField fieldNormalVectorX;
    JTextField fieldNormalVectorY;
    JTextField fieldNormalVectorZ;
    JTextField fieldIterationsCount;

    //  Buttons
    JButton buttonReset;
    JButton buttonUp;
    JButton buttonLeft;
    JButton buttonDown;
    JButton buttonRight;
    JButton buttonZoomUp;
    JButton buttonZoomDown;


    GridBagConstraints gridBagConstraints;

    KeyListener listener;

    public Window() {
        listener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> System.out.println("↑");
                    case KeyEvent.VK_DOWN -> System.out.println("↓");
                    case KeyEvent.VK_LEFT -> System.out.println("←");
                    case KeyEvent.VK_RIGHT -> System.out.println("→");
                    case KeyEvent.VK_PLUS -> System.out.println("+");
                    case KeyEvent.VK_MINUS, KeyEvent.VK_EQUALS -> System.out.println("-");
                }
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
        labelParameters = new JLabel("Параметры (a, b, c): ");
        labelParameters.addKeyListener(listener);
        controlPanel.add(labelParameters, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        fieldParameterA = new JTextField("a: ");
        fieldParameterA.setColumns(5);
        controlPanel.add(fieldParameterA, gridBagConstraints);

        fieldParameterB = new JTextField("b: ");
        fieldParameterB.setColumns(5);
        gridBagConstraints.gridx = 3;
        controlPanel.add(fieldParameterB, gridBagConstraints);

        fieldParameterC = new JTextField("c: ");
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

        fieldNormalVectorX = new JTextField("x: ");
        fieldNormalVectorX.setColumns(5);
        gridBagConstraints.gridx = 2;
        controlPanel.add(fieldNormalVectorX, gridBagConstraints);

        fieldNormalVectorY = new JTextField("y: ");
        fieldNormalVectorY.setColumns(5);
        gridBagConstraints.gridx = 3;
        controlPanel.add(fieldNormalVectorY, gridBagConstraints);

        fieldNormalVectorZ = new JTextField("z: ");
        fieldNormalVectorZ.setColumns(5);
        gridBagConstraints.gridx = 4;
        controlPanel.add(fieldNormalVectorZ, gridBagConstraints);

        //  Iterations count
        labelIterationsCount = new JLabel("Количество итераций: ");
        labelIterationsCount.addKeyListener(listener);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        controlPanel.add(labelIterationsCount, gridBagConstraints);
        fieldIterationsCount = new JTextField("26000");
        fieldIterationsCount.setColumns(5);
        gridBagConstraints.gridx = 2;
        controlPanel.add(fieldIterationsCount, gridBagConstraints);

        //  Move panel
        GridBagConstraints moveConstraints = new GridBagConstraints();
        movePanel = new JPanel();
        movePanel.addKeyListener(listener);
        movePanel.setLayout(new GridBagLayout());
        moveConstraints.fill = GridBagConstraints.NONE;
        moveConstraints.weightx = 0;
        moveConstraints.ipady = -10;

        buttonUp = new JButton("Вверх");
        buttonUp.addKeyListener(listener);
        movePanel.add(buttonUp, moveConstraints);
        buttonUp.addActionListener(this);

        buttonDown = new JButton("Вниз");
        buttonDown.addKeyListener(listener);
        movePanel.add(buttonDown, moveConstraints);
        buttonDown.addActionListener(this);

        buttonLeft = new JButton("Влево");
        buttonLeft.addKeyListener(listener);
        movePanel.add(buttonLeft, moveConstraints);
        buttonLeft.addActionListener(this);

        buttonRight = new JButton("Вправо");
        buttonRight.addKeyListener(listener);
        movePanel.add(buttonRight, moveConstraints);
        buttonRight.addActionListener(this);

        buttonZoomUp = new JButton("Увеличить");
        buttonZoomUp.addKeyListener(listener);
        movePanel.add(buttonZoomUp, moveConstraints);
        buttonZoomUp.addActionListener(this);

        buttonZoomDown = new JButton("Уменьшить");
        buttonZoomDown.addKeyListener(listener);
        movePanel.add(buttonZoomDown, moveConstraints);
        buttonZoomDown.addActionListener(this);


        containerPanel = new JPanel();
        containerPanel.addKeyListener(listener);
        containerPanel.setLayout(new BorderLayout());
        containerPanel.add(controlPanel, BorderLayout.NORTH);
        containerPanel.add(movePanel, BorderLayout.SOUTH);
        containerPanel.setDoubleBuffered(true);
        frame.setFocusable(true);
        frame.add(containerPanel);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source.equals(buttonReset)) {
            System.out.println(buttonReset.getText());
        } else if (source.equals(buttonUp)) {
            System.out.println(buttonUp.getText());
        } else if (source.equals(buttonDown)) {
            System.out.println(buttonDown.getText());
        } else if (source.equals(buttonLeft)) {
            System.out.println(buttonLeft.getText());
        } else if (source.equals(buttonRight)) {
            System.out.println(buttonRight.getText());
        } else if (source.equals(buttonZoomUp)) {
            System.out.println(buttonZoomUp.getText());
        } else if (source.equals(buttonZoomDown)) {
            System.out.println(buttonZoomDown.getText());
        }
    }
}
