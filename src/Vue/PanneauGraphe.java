package Vue; // Ou 'vue' selon ton dossier

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
    private boolean afficherFondCarte = true; // Pour activer/désactiver la map

    // Animation
    private Itineraire itineraireActuel;
    private int etapeAnimation = 0;
    private Timer timerAnimation;
    private Sommet positionCamion;

    public PanneauGraphe(Graphe graphe) {
        this.graphe = graphe;
        this.setBackground(Color.WHITE);

        // Charger l'image de la carte OSM
        try {
            mapImage = ImageIO.read(new File("data/map_vitry.png"));
        } catch (IOException e) {
            System.err.println("Info: Pas d'image de fond (data/map_vitry.png).");
        }

        // Vitesse d'animation : 600ms (plus lent)
        timerAnimation = new Timer(600, e -> avancerCamion());
    }

    // Permet de changer de graphe dynamiquement (ex: passer de Vitry au fictif)
    public void setGraphe(Graphe nouveauGraphe, boolean avecFond) {
        this.graphe = nouveauGraphe;
        this.afficherFondCarte = avecFond;
        this.itineraireActuel = null; // Reset animation
        this.positionCamion = null;
        repaint();
    }

    public void animerItineraire(Itineraire itineraire) {
        if (itineraire == null || itineraire.getChemin().isEmpty()) return;

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
            timerAnimation.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. DESSINER LA CARTE EN FOND (Seulement si demandé)
        if (mapImage != null && afficherFondCarte) {
            g2.drawImage(mapImage, 0, 0, this.getWidth(), this.getHeight(), null);
        }

        // 2. DESSINER LES ARÊTES
        // Gris très clair et transparent pour ne pas gêner
        g2.setColor(new Color(200, 200, 200, 60));
        g2.setStroke(new BasicStroke(1));

        if (graphe != null) {
            for (Arete arete : graphe.getToutesAretes()) {
                Sommet s1 = arete.getSommetA();
                Sommet s2 = arete.getSommetB();
                // On utilise les coordonnées mises à l'échelle si on est sur la map,
                // ou brutes si c'est le graphe fictif (car ses coords sont déjà en pixels)
                // Ici pour simplifier on utilise toujours scaleX/scaleY, il faut juste que les graphes fictifs soient dans les bornes 0-1000
                g2.drawLine(scaleX(s1.getX()), scaleY(s1.getY()),
                        scaleX(s2.getX()), scaleY(s2.getY()));
            }
        }

        // 3. DESSINER L'ITINÉRAIRE
        if (itineraireActuel != null) {
            g2.setColor(new Color(255, 0, 0, 200));
            g2.setStroke(new BasicStroke(4));
            List<Sommet> chemin = itineraireActuel.getChemin();
            for (int i = 0; i < etapeAnimation; i++) {
                Sommet s1 = chemin.get(i);
                Sommet s2 = chemin.get(i+1);
                g2.drawLine(scaleX(s1.getX()), scaleY(s1.getY()),
                        scaleX(s2.getX()), scaleY(s2.getY()));
            }
        }

        // 4. DESSINER LES SOMMETS
        if (graphe != null) {
            for (Sommet s : graphe.getTousSommets()) {
                if (s.equals(positionCamion)) {
                    drawCamion(g2, scaleX(s.getX()), scaleY(s.getY()));
                } else {
                    g2.setColor(Color.BLUE);
                    int size = 6;
                    // Sur un graphe fictif (peu de points), on les fait plus gros
                    if (!afficherFondCarte) size = 12;
                    g2.fillOval(scaleX(s.getX()) - size/2, scaleY(s.getY()) - size/2, size, size);

                    // Afficher les noms sur le graphe fictif pour comprendre
                    if (!afficherFondCarte) {
                        g2.setColor(Color.BLACK);
                        g2.drawString(s.getId(), scaleX(s.getX()) + 10, scaleY(s.getY()));
                    }
                }
            }
        }
    }

    private void drawCamion(Graphics2D g2, int x, int y) {
        g2.setColor(Color.ORANGE);
        g2.fillOval(x - 12, y - 12, 24, 24);
        g2.setColor(Color.BLACK);
        g2.drawOval(x - 12, y - 12, 24, 24);
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        g2.drawString("\uD83D\uDE9A", x - 10, y + 8);
    }

    private int scaleX(int x) {
        // Adaptation simple : le graphe est conçu pour du 1000x800
        return (int) ((x / 1000.0) * this.getWidth());
    }

    private int scaleY(int y) {
        return (int) ((y / 800.0) * this.getHeight());
    }
}