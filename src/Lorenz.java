import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.geom.Line2D;

public class Lorenz extends JPanel {
    private static final double TINY = 0.000001;
    private final double[] normalVector = new double[3];
    private final double[] u = new double[3];
    private final double[] parameters = new double[3];
    private final double[] p = new double[3];
    private final double[] l_max = new double[3];
    private final double[] l_min = new double[3];
    private final long iterations = 26000;
    private final long iterationLapse = Math.min(500, iterations / 2);
    private double[] v = new double[3];
    private final double rotationAngle = 0.0;
    private final double dZoom = 0.0;
    private double xmax = 0;
    private double xmin = 0;
    private double ymax = 0;
    private double ymin = 0;
    private boolean reDraw;
    private final int orbit = 0;

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
        if (orbit == 0) {
            paint();
        }
    }

    private void paint() {

    }

    private double vectorMag(double[] a) {
        double mag = 0;
        for (double i : a)
            mag += i * i;
        mag = Math.sqrt(mag);
        return mag;
    }

    private double[] orthogonalization(double[] a, double[] b) {
        double[] c = new double[a.length];
        c[0] = a[1] * b[2] - a[2] * b[1];
        c[1] = a[2] * b[0] - a[0] * b[2];
        c[2] = a[0] * b[1] - a[1] * b[0];
        return c;
    }

    private void lorenzBoundingBox(double x, double y, double z) {
        double[] dtemp = new double[3];
        dtemp[0] = x;
        dtemp[1] = y;
        dtemp[2] = z;

        for (int i = 0; i < 3; i++)
            if (l_max[i] < dtemp[i])
                l_max[i] = dtemp[i];
            else if (l_min[i] > dtemp[i])
                l_min[i] = dtemp[i];
    }

    private void calculatePoints(Graphics2D graphics2D) {
        normalVector[0] = 8;
        normalVector[1] = -4;
        normalVector[2] = 3;
        parameters[0] = 10;
        parameters[1] = 28;
        parameters[2] = 8 / 3;
        int fixedPointsCount = 0;
        double[] fixedPoint = new double[3];
        double x0, y0, z0, x1, y1, z1;
        double nxmin = 0, nxmax = 0, nymin = 0, nymax = 0;
        double pxold, pyold, nx, ny;
        double px = 0, py = 0;
        double pHeight = getSize().height;
        double pWidth = getSize().width;
        double dtemp;
        boolean rangesSet = false;
        for (int i = 0; i < 3 && orbit == 0; i++) {
            if (Math.abs(normalVector[i]) > TINY) {
                //Make all parallel planes equivalent by switching to unit n-plane that
                //has the first non-zero component which is positive
                dtemp = 1.0;
                if (normalVector[i] < TINY)
                    dtemp = -1;
                //Make u a position vector ON the UNIT-n-plane
                u[(i + 1) % 3] = 1 + dtemp * normalVector[(i + 1) % 3] / vectorMag(normalVector);
                u[(i + 2) % 3] = 1;
                u[i] = vectorMag(normalVector) - (u[(i + 1) % 3] * normalVector[(i + 1) % 3] + u[(i + 2) % 3] * normalVector[(i + 2) % 3]) * dtemp;
                u[i] /= normalVector[i] * dtemp;
                //Now make u a direction vector IN the n-plane
                for (int j = 0; j < 3; j++)
                    u[j] = u[j] - (dtemp * normalVector[j] / vectorMag(normalVector));
                //Make u a unit vector
                dtemp = vectorMag(u);
                for (int j = 0; j < 3; j++)
                    u[j] /= dtemp;
                //Make v perpendicular to u and n
                v = orthogonalization(normalVector, u);
                //Make v a unit vector
                dtemp = vectorMag(v);
                //Make v lie one the most positive N-plane
                if (normalVector[i] < TINY)
                    dtemp *= -1;
                for (int j = 0; j < 3; j++)
                    v[j] /= dtemp;
                break;
            } else if (vectorMag(normalVector) < TINY) {
                //n will be freshed in the JPanel  - see Component
                normalVector[0] = normalVector[1] = normalVector[2] = 1;
                i = 0;
            }
        }

        for (int j = 0; j < 2; j++) {
            if (j == 0 && (reDraw || orbit > 2))
                continue;
            x0 = 0.1;
            y0 = 0;
            z0 = 0;
            for (int i = 0; i < iterations; i++) {

                double h = 0.01;
                x1 = x0 + h * parameters[0] * (y0 - x0);
                y1 = y0 + h * (x0 * (parameters[1] - z0) - y0);
                z1 = z0 + h * (x0 * y0 - parameters[2] * z0);
                x0 = x1;
                y0 = y1;
                z0 = z1;


                if (Math.abs(x1 - x0) < TINY && Math.abs(y1 - y0) < TINY && Math.abs(z1 - z0) < TINY) {
                    if (Math.abs(x1 - fixedPoint[0]) < TINY && Math.abs(y1 - fixedPoint[1]) < TINY && Math.abs(z1 - fixedPoint[2]) < TINY)
                        fixedPointsCount++;
                    else {
                        fixedPoint[0] = x1;
                        fixedPoint[1] = y1;
                        fixedPoint[2] = z1;
                        fixedPointsCount = 0;
                    }
                }

                ///////////////////////// Fixed Points and NaNs
                if (Double.isNaN(x0)) {
                    graphics2D.setColor(Color.magenta);
                    graphics2D.drawString("Lorenz Points have become unbounded", 20, 20);
                    System.out.println("Lorenz Points have become unbounded");
                    break;
                } else if (fixedPointsCount > iterations / 3) {
                    graphics2D.setColor(Color.magenta);
                    graphics2D.drawString("Lorenze Points have converged to a fixed point - No Chaotic attractor to show", 20, 20);
                    System.out.println("Lorenze Points have converged to a fixed point\n - No Chaotic attractor to show");
                    break;
                }

                //Map x0 y0 z0 to the n-plane using u and v
                nx = (x1 - normalVector[0]) * u[0] + (y1 - normalVector[1]) * u[1] + (z1 - normalVector[2]) * u[2];
                ny = (x1 - normalVector[0]) * 0.491 + (y1 - normalVector[1]) * 0.405 + (z1 - normalVector[2]) * 0.77;

                //Now Rotate!
                dtemp = nx * Math.cos(2.0 * Math.PI * rotationAngle / 360) - ny * Math.sin(2.0 * Math.PI * rotationAngle / 360);
                ny = nx * Math.sin(2.0 * Math.PI * rotationAngle / 360) + ny * Math.cos(2.0 * Math.PI * rotationAngle / 360);
                nx = dtemp;

                if (i > iterationLapse) {
                    if (nx > xmax)
                        xmax = nx;
                    else if (nx < xmin)
                        xmin = nx;

                    if (ny > ymax)
                        ymax = ny;
                    else if (ny < ymin)
                        ymin = ny;
                    lorenzBoundingBox(x1, y1, z1);
                } else if (i == iterationLapse) {
                    xmax = xmin = nx;
                    xmax += TINY;
                    ymax = ymin = ny;
                    ymax += TINY;
                    lorenzBoundingBox(x1, y1, z1);
                }

                //Cant be updated during paintprocess!
                //Cant change during orbit eaither!
                if (!rangesSet && ((j == 0 && i == iterations - 1) || j == 1)) {
                    nymax = ymax;
                    nymin = ymin;
                    nxmax = xmax;
                    nxmin = xmin;
                    rangesSet = true;
                }

                if (orbit == 0 && iterations / 4 == i) {
                    for (int m = 0; m < 3; m++) {
                        p[m] = (l_max[m] + l_min[m]) / 2;
                        l_min[m] = l_max[m] = 0.0;
                    }
                }

                //Now scale to fit the screen
                pxold = px;
                pyold = py;

                py = pHeight * (ny - nymin) / (nymax - nymin);
                int vShift = 0;
                py = pHeight - py + pHeight * vShift / 10;
                int hShift = 0;
                px = pWidth * (nx - nxmin) / (nxmax - nxmin) + pWidth * hShift / 10;

                //calculate where P is on the screen
                if (orbit > 0) {
                    double p_py, p_px, p_ny, p_nx;
                    //Map p to the n-plane using u and v
                    p_nx = (0.847 - normalVector[0]) * u[0] + (1.496 - normalVector[1]) * u[1] + (26.155 - normalVector[2]) * u[2];
                    p_ny = (0.847 - normalVector[0]) * 0.491 + (1.496 - normalVector[1]) * 0.405 + (26.155 - normalVector[2]) * 0.77;
                    //Now Rotate!
                    dtemp = p_nx * Math.cos(2.0 * Math.PI * rotationAngle / 360) - p_ny * Math.sin(2.0 * Math.PI * rotationAngle / 360);
                    p_ny = p_nx * Math.sin(2.0 * Math.PI * rotationAngle / 360) + p_ny * Math.cos(2.0 * Math.PI * rotationAngle / 360);
                    p_nx = dtemp;
                    //Now scale to fit the screen
                    p_py = pHeight * (p_ny - nymin) / (nymax - nymin);
                    p_py = pHeight - p_py + pHeight * vShift / 10;
                    p_px = pWidth * (p_nx - nxmin) / (nxmax - nxmin) + pWidth * hShift / 10;

                    //Now shift the other points going to the screen.
                    py += pHeight / 2 - p_py;
                    px += pWidth / 2 - p_px;
                }

                //Zoom Correction
                px = (px - pWidth / 2) * (100.0 + dZoom) / 100.0 + pWidth / 2.0;
                py = (py - pHeight / 2) * (100.0 + dZoom) / 100.0 + pHeight / 2.0;

                if (j == 1 && i > iterationLapse && rangesSet) {
                    graphics2D.draw(new Line2D.Double(pxold, pyold, px, py));
                    switch ((i / ((int) (iterations / 40))) % 7) {
                        case 0 -> graphics2D.setColor(Color.red);
                        case 1 -> graphics2D.setColor(Color.getHSBColor(1, 1, 1));
                        case 2 -> graphics2D.setColor(Color.black);
                        case 3 -> graphics2D.setColor(Color.gray);
                        case 4 -> graphics2D.setColor(Color.yellow);
                        case 5 -> graphics2D.setColor(Color.blue);
                        case 6 -> graphics2D.setColor(Color.magenta);
                        default -> graphics2D.setColor(Color.getHSBColor(3, 5, 5));
                    }
                }
            }
        }
        reDraw = true;
    }
}
