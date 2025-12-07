package Vue;

import Modele.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PanneauGraphe extends JPanel {
    private Graphe graphe;
    private BufferedImage mapImage;
    private boolean afficherFondCarte = true;

    // Animation
    private Itineraire itineraireActuel;
    private int etapeAnimation = 0;
    private Timer timerAnimation;
    private Sommet positionCamion;

    // --- REGLAGES DE VITESSE ET ECHELLE ---
    // 150ms = Vitesse modérée (lisible)
    private static final int VITESSE_ANIMATION = 150;
    private static final double METRES_PAR_PIXEL = 5.0;

    public PanneauGraphe(Graphe graphe) {
        this.graphe = graphe;
        this.setBackground(Color.WHITE);

        try {
            mapImage = ImageIO.read(new File("data/map_vitry.png"));
        } catch (IOException e) {
            System.err.println("Info: Pas d'image de fond (data/map_vitry.png).");
        }

        timerAnimation = new Timer(VITESSE_ANIMATION, e -> avancerCamion());
    }

    public void setGraphe(Graphe nouveauGraphe, boolean avecFond) {
        this.graphe = nouveauGraphe;
        this.afficherFondCarte = avecFond;
        this.itineraireActuel = null;
        this.positionCamion = null;
        if(timerAnimation.isRunning()) timerAnimation.stop();
        repaint();
    }

    public void animerItineraire(Itineraire itineraire) {
        if (itineraire == null || itineraire.getChemin().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Itinéraire vide ou impossible à calculer.");
            return;
        }

        this.itineraireActuel = itineraire;
        this.etapeAnimation = 0;
        this.positionCamion = itineraire.getChemin().get(0);

        timerAnimation.restart();
        repaint();
    }

    private void avancerCamion() {
        if (itineraireActuel != null && etapeAnimation < itineraireActuel.getChemin().size() - 1) {
            etapeAnimation++;
            positionCamion = itineraireActuel.getChemin().get(etapeAnimation);
            repaint();
        } else {
            // FIN DU TRAJET
            timerAnimation.stop();

            double pixels = itineraireActuel.getDistanceTotale();
            double km = (pixels * METRES_PAR_PIXEL) / 1000.0;

            String msg = String.format(
                    "Destination atteinte !\n" +
                            "Intersections traversées : %d\n" +
                            "Distance estimée : %.2f km",
                    etapeAnimation, km);

            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, msg, "Fin de Mission", JOptionPane.INFORMATION_MESSAGE)
            );
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // FOND
        if (mapImage != null && afficherFondCarte) {
            g2.drawImage(mapImage, 0, 0, this.getWidth(), this.getHeight(), null);
        }

        // ARÊTES
        g2.setColor(new Color(200, 200, 200, 100));
        g2.setStroke(new BasicStroke(1));

        if (graphe != null) {
            for (Arete arete : graphe.getToutesAretes()) {
                Sommet s1 = arete.getSommetA();
                Sommet s2 = arete.getSommetB();
                g2.drawLine(scaleX(s1.getX()), scaleY(s1.getY()),
                        scaleX(s2.getX()), scaleY(s2.getY()));
            }
        }

        // ITINÉRAIRE
        if (itineraireActuel != null) {
            g2.setColor(new Color(255, 0, 0, 180));
            g2.setStroke(new BasicStroke(3));
            List<Sommet> chemin = itineraireActuel.getChemin();
            for (int i = 0; i < etapeAnimation; i++) {
                Sommet s1 = chemin.get(i);
                Sommet s2 = chemin.get(i+1);
                g2.drawLine(scaleX(s1.getX()), scaleY(s1.getY()),
                        scaleX(s2.getX()), scaleY(s2.getY()));
            }
        }

        // SOMMETS
        if (graphe != null) {
            for (Sommet s : graphe.getTousSommets()) {
                if (s.equals(positionCamion)) {
                    drawCamion(g2, scaleX(s.getX()), scaleY(s.getY()));
                } else {
                    g2.setColor(Color.BLUE);
                    int size = afficherFondCarte ? 6 : 10;
                    g2.fillOval(scaleX(s.getX()) - size/2, scaleY(s.getY()) - size/2, size, size);
                    if (!afficherFondCarte) {
                        g2.setColor(Color.BLACK);
                        g2.drawString(s.getId(), scaleX(s.getX()) + 8, scaleY(s.getY()));
                    }
                }
            }
        }
    }

    private void drawCamion(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(0,0,0,50)); g2.fillOval(x - 8, y - 8, 26, 26);
        g2.setColor(new Color(255, 140, 0)); g2.fillOval(x - 10, y - 10, 20, 20);
        g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2)); g2.drawOval(x - 10, y - 10, 20, 20);
        g2.setColor(Color.BLACK); g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        g2.drawString("🚛", x - 7, y + 5);
    }

    private int scaleX(int x) { return (int) ((x / 1000.0) * this.getWidth()); }
    private int scaleY(int y) { return (int) ((y / 800.0) * this.getHeight()); }
}