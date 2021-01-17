import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.geom.Line2D;

public class Lorenz extends JPanel {
    private static final double EPS = 0.000001;
    private final double[] u = new double[3];
    public int vShift = 0, hShift = 0;
    private double[] normalVector = new double[3];
    private double[] parameters = new double[3];
    private long iterations = 60000;
    private double rotationAngle = 0.0;
    private double zoom = 0.0;
    private double xMax = 0;
    private double xMin = 0;
    private double yMax = 0;
    private double yMin = 0;

    public void init() {
        normalVector[0] = 1;
        normalVector[1] = 1;
        normalVector[2] = 1;
        parameters[0] = 10;
        parameters[1] = 28;
        parameters[2] = 2.6666666666666665;
    }

    public void rotateLeft() {
        rotationAngle += 10;
        repaint();
    }

    public void rotateNull() {
        rotationAngle = 0;
        repaint();
    }

    public void rotateRight() {
        rotationAngle -= 10;
        repaint();
    }

    public void up() {
        vShift++;
        repaint();
    }

    public void down() {
        vShift--;
        repaint();
    }

    public void left() {
        hShift++;
        repaint();
    }

    public void right() {
        hShift--;
        repaint();
    }

    public void plus() {
        zoom += 10;
        repaint();
    }

    public void minus() {
        zoom -= 10;
        repaint();
    }

    public void draw() {
        normalVector = Window.getNormalVector();
        parameters = Window.getParameters();
        zoom = vShift = hShift = 0;
        yMin = yMax = xMin = xMax = rotationAngle = 0;
        double iterations;
        if ((iterations = Window.getIterationCount()) > 100)
            this.iterations = (long) iterations;
    }

    /**
     * Calls the UI delegate's paint method, if the UI delegate
     * is non-<code>null</code>.  We pass the delegate a copy of the
     * <code>Graphics</code> object to protect the rest of the
     * paint code from irrevocable changes
     * (for example, <code>Graphics.translate</code>).
     * <p>
     * If you override this in a subclass you should not make permanent
     * changes to the passed in <code>Graphics</code>. For example, you
     * should not alter the clip <code>Rectangle</code> or modify the
     * transform. If you need to do these operations you may find it
     * easier to create a new <code>Graphics</code> from the passed in
     * <code>Graphics</code> and manipulate it. Further, if you do not
     * invoke super's implementation you must honor the opaque property, that is
     * if this component is opaque, you must completely fill in the background
     * in an opaque color. If you do not honor the opaque property you
     * will likely see visual artifacts.
     * <p>
     * The passed in <code>Graphics</code> object might
     * have a transform other than the identify transform
     * installed on it.  In this case, you might get
     * unexpected results if you cumulatively apply
     * another transform.
     *
     * @param graphics the <code>Graphics</code> object to protect
     * @see #paint
     * @see ComponentUI
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(Color.WHITE);
        graphics2D.setStroke(new BasicStroke(0.3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        graphics2D.fillRect(0, 0, getSize().width, getSize().height);
        calculatePoints(graphics2D);
    }

    private double findNormal(double[] a) {
        double normal = 0;
        for (double i : a)
            normal += i * i;
        normal = Math.sqrt(normal);
        return normal;
    }

    private double[] findDirectionVector(double[] a, double[] b) {
        double[] directionVector = new double[3];
        directionVector[0] = a[1] * b[2] - a[2] * b[1];
        directionVector[1] = a[2] * b[0] - a[0] * b[2];
        directionVector[2] = a[0] * b[1] - a[1] * b[0];
        return directionVector;
    }

    private void calculatePoints(Graphics2D graphics2D) {
        int fixedPointsCount = 0;
        double[] fixedPoint = new double[3];
        double x, y, z, x1, y1, z1;
        double nx, ny, savePx, savePy;
        double px = 0, py = 0;
        double height = getSize().height, width = getSize().width;
        double normal;
        for (int i = 0; i < 3; i++) {
            if (Math.abs(normalVector[i]) > EPS) {
                //  Делаем все параллельные плоскости эквивалентными сменив их на плоскость нормали
                normal = 1.0;
                if (normalVector[i] < EPS)
                    normal = -1;
                //  Делаем u на плоскости нормали
                u[(i + 1) % 3] = 1 + normal * normalVector[(i + 1) % 3] / findNormal(normalVector);
                u[(i + 2) % 3] = 1;
                u[i] = findNormal(normalVector) - (u[(i + 1) % 3] * normalVector[(i + 1) % 3] + u[(i + 2) % 3] * normalVector[(i + 2) % 3]) * normal;
                u[i] /= normalVector[i] * normal;
                for (int j = 0; j < 3; j++)
                    u[j] = u[j] - (normal * normalVector[j] / findNormal(normalVector));
                //  Делаем u единичным вектором (нормализуем)
                normal = findNormal(u);
                for (int j = 0; j < 3; j++)
                    u[j] /= normal;
                //  Делаем v перпендикулярным u и вектор нормали
                double[] v = findDirectionVector(normalVector, u);
                //  Делаем v единичным вектором (нормализуем)
                normal = findNormal(v);
                if (normalVector[i] < EPS)
                    normal *= -1;
                for (int j = 0; j < 3; j++)
                    v[j] /= normal;
                break;
            } else if (findNormal(normalVector) < EPS) {
                //  Обновляем вектор нормали
                normalVector[0] = normalVector[1] = normalVector[2] = 1;
                i = 0;
            }
        }

        for (int i = 0; i < 2; i++) {
            x = 3.051522;
            y = 1.582542;
            z = 15.62388;
            for (int j = 0; j < iterations; j++) {
                double dt = 0.01;
                x1 = x + parameters[0] * (y - x) * dt;
                y1 = y + (x * (parameters[1] - z) - y) * dt;
                z1 = z + (x * y - parameters[2] * z) * dt;

                x = x1;
                y = y1;
                z = z1;

                if (Math.abs(x1 - fixedPoint[0]) < EPS && Math.abs(y1 - fixedPoint[1]) < EPS && Math.abs(z1 - fixedPoint[2]) < EPS)
                    fixedPointsCount++;
                else {
                    fixedPoint[0] = x1;
                    fixedPoint[1] = y1;
                    fixedPoint[2] = z1;
                    fixedPointsCount = 0;
                }

                //  Ошибки
                if (Double.isNaN(x)) {
                    graphics2D.setColor(Color.RED);
                    graphics2D.drawString("Точки стали не ограниченными", 20, 20);
                    break;
                } else if (fixedPointsCount > iterations / 3) {
                    graphics2D.setColor(Color.RED);
                    graphics2D.drawString("Аттрактор сходится к единой точке", 20, 20);
                    break;
                }

                //  Переводим x y в нормаль благодаря u, v
                nx = (x1 - normalVector[0]) * u[0] + (y1 - normalVector[1]) * u[1] + (z1 - normalVector[2]) * u[2];
                ny = (x1 - normalVector[0]) + (y1 - normalVector[1]) + (z1 - normalVector[2]);

                //  Поворачиваем
                normal = nx * Math.cos(2.0 * Math.PI * rotationAngle / 360) - ny * Math.sin(2.0 * Math.PI * rotationAngle / 360);
                ny = nx * Math.sin(2.0 * Math.PI * rotationAngle / 360) + ny * Math.cos(2.0 * Math.PI * rotationAngle / 360);
                nx = normal;

                long iterationLapse = 500;
                if (j > iterationLapse) {
                    if (nx > xMax)
                        xMax = nx;
                    else if (nx < xMin)
                        xMin = nx;

                    if (ny > yMax)
                        yMax = ny;
                    else if (ny < yMin)
                        yMin = ny;
                }


                //  Теперь подгоняем размеры под экран
                savePx = px;
                savePy = py;
                py = height * (ny - yMin) / (yMax - yMin);
                py = height - py + height * vShift / 10;
                px = width * (nx - xMin) / (xMax - xMin) + width * hShift / 10;

                //  Меняем приближение
                px = (px - width / 2) * (100.0 + zoom) / 100.0 + width / 2.0;
                py = (py - height / 2) * (100.0 + zoom) / 100.0 + height / 2.0;

                //  Рисуем
                if (i == 1) {
                    graphics2D.draw(new Line2D.Double(savePx, savePy, px, py));
                    switch ((j / ((int) (iterations / 40))) % 7) {
                        case 0 -> graphics2D.setColor(Color.RED);
                        case 1 -> graphics2D.setColor(Color.ORANGE);
                        case 2 -> graphics2D.setColor(Color.YELLOW);
                        case 3 -> graphics2D.setColor(Color.GREEN);
                        case 4 -> graphics2D.setColor(Color.CYAN);
                        case 5 -> graphics2D.setColor(Color.BLUE);
                        case 6 -> graphics2D.setColor(Color.MAGENTA);
                        default -> graphics2D.setColor(Color.BLACK);
                    }
                }
            }
        }
    }
}
