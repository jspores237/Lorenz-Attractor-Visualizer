import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LorenzAttractor extends JPanel {
    private double x = 0.01; //x is the horizontal velocity of the fluid
    private double y = 0; //y is the temperature difference between ascending and descending fluid
    private double z = 0; //z is the deviation of the system from thermal equilibrium (how much the system is disturbed from a steady state)
    private final double dt = 0.01; //dt is the time step. dt represents the size of each time step in the numerical method (Euler's method in this case), used to approximate the solutions. The smaller the dt, the more accurate the approximation, but the longer it takes to compute the solution. Typical values for dt are small like 0.01 here, to ensure the system evolves smoothly.
    private final double sigma = 10; //sigma is the Prandtl Number (a constant representing ratio of fluid viscosity to thermal diffusivity. AKA how heat diffuses relative to the movement of the fluid. He chose 10 because it is a commonly used value in the context of the atmospheric convection model.)
    private final double rho = 28; //rho is the Rayleigh Number (this constant represents the temperature difference between the top and bottom of the system. It describes how much heat is being supplied to the system, which affects the overall convection motion. Lorenz used 28 because it produces chaotic behavior (other values may create stable or oscillatory behavior, but NOT chaotic.))
    private final double beta = 8.0 / 3.0; //beta is the geometric factor. It is related to the physical aspect ratio of the convection cells (shape of the cells where the fluid circulates). Lorenz set this to 8/3 = ~2.67 which is another standard lorenz attractor value.

    private List<Point> points;
    private List<Color> colors;

    public LorenzAttractor() {
        points = new ArrayList<>();
        colors = new ArrayList<>();
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        new Timer(16, e -> updateAndRepaint()).start();
    }

    private void updateLorenz() {
        double dx = sigma * (y - x);
        double dy = x * (rho - z) - y;
        double dz = x * y - (beta * z);

        x += dx * dt;
        y += dy * dt;
        z += dz * dt;

        //Map the x and y values to screen space and store the point
        int screenX = (int) (getWidth() / 2 + x * 10);
        int screenY = (int) (getHeight() / 2 + y * 10);
        points.add(new Point(screenX, screenY));

        //Generate a color based on the z value (for a cool effect)
        Color color = Color.getHSBColor((float) ((z + 30) / 60), 1, 1);
        colors.add(color);

        //Limit the number of points to avoid memory issues
        if (points.size() > 5000) {
            points.remove(0);
            colors.remove(0);
        }
    }
    
        //Update the system and repaint the visualization
        private void updateAndRepaint() {
            updateLorenz();
            repaint();
        }

        private void playSound(String filePath) {
    try {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop continuously
        clip.start();
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        e.printStackTrace();
    }
}


        //Paint the points representing the Lorenz attractor
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            //Draw each point with its corresponding color
            for (int i = 0; i < points.size(); i++) {
                g2d.setColor(colors.get(i));
                Point p = points.get(i);
                g2d.fillOval(p.x, p.y, 3, 3);
            }
        }

        public static void main(String[] args) {
            JFrame frame = new JFrame("Lorenz Attractor");
            LorenzAttractor attractor = new LorenzAttractor();

            frame.add(attractor);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            attractor.playSound("zimmer.wav");
        }

}
