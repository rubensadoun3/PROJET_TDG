package Simulation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PanneauSimulation extends JPanel {
    private VilleSimu ville;
    private int jourActuel = 1;
    private int maxJours = 0;

    // Animation
    private List<Camion> camionsActifs = new ArrayList<>();
    private final double VITESSE = 5.0; // Plus rapide pour que ce soit fluide
    private javax.swing.Timer timerAnimation; // Timer Swing pour l'animation fluide

    // Couleurs
    private Color[] palette = {Color.LIGHT_GRAY, Color.RED, Color.BLUE, new Color(0, 160, 0), Color.ORANGE, Color.MAGENTA, Color.CYAN};

    public PanneauSimulation(VilleSimu ville) {
        this.ville = ville;
        this.setBackground(Color.WHITE);

        // Timer d'animation (30 FPS environ -> 33ms)
        timerAnimation = new javax.swing.Timer(33, e -> {
            animerCamions();
            repaint();
        });
    }

    public void demarrer(int totalJours) {
        this.maxJours = totalJours;
        lancerJournee(1);
        timerAnimation.start();
    }

    private void lancerJournee(int jour) {
        this.jourActuel = jour;
        camionsActifs.clear();

        // Réinitialiser l'état visité pour la semaine si on recommence
        if (jour == 1) {
            for(IntersectionSimu i : ville.reseau.values()) i.estVisite = false;
        }

        // Créer les camions pour les secteurs du jour
        for(Secteur s : ville.secteurs.values()) {
            if(s.getJourAttribue() == jour) {
                int k = s.camionsRequis;
                int nbPoints = s.pointsCollecte.size();
                if(nbPoints == 0) continue;

                // Répartir les points entre les camions du secteur
                int portion = nbPoints / k;
                for(int i=0; i<k; i++) {
                    int debut = i * portion;
                    int fin = (i == k-1) ? nbPoints : (i + 1) * portion;

                    List<IntersectionSimu> trajet = new ArrayList<>(s.pointsCollecte.subList(debut, fin));
                    if(!trajet.isEmpty()) {
                        trajet.get(0).estVisite = true;
                        Camion c = new Camion(trajet.get(0), trajet);
                        c.listePoints.remove(0); // On est déjà au départ
                        definirProchaineDestination(c);
                        camionsActifs.add(c);
                    }
                }
            }
        }
    }

    // Algorithme glouton (Plus proche voisin) pour le camion
    private void definirProchaineDestination(Camion c) {
        if(c.listePoints.isEmpty()) { c.destination = null; return; }

        IntersectionSimu meilleur = null;
        double distMin = Double.MAX_VALUE;

        for(IntersectionSimu pt : c.listePoints) {
            double d = Math.hypot(pt.x - c.x, pt.y - c.y);
            if(d < distMin) { distMin = d; meilleur = pt; }
        }
        c.destination = meilleur;
    }

    private void animerCamions() {
        boolean tousFinis = true;
        for(Camion c : camionsActifs) {
            if(c.destination != null) {
                tousFinis = false;
                double dx = c.destination.x - c.x;
                double dy = c.destination.y - c.y;
                double dist = Math.hypot(dx, dy);

                if(dist < VITESSE) {
                    c.x = c.destination.x;
                    c.y = c.destination.y;
                    c.destination.estVisite = true;
                    c.listePoints.remove(c.destination);
                    definirProchaineDestination(c);
                } else {
                    c.x += (dx/dist)*VITESSE;
                    c.y += (dy/dist)*VITESSE;
                }
            }
        }

        // Si la journée est finie, on attend 1s puis on passe au jour suivant
        if(tousFinis && !camionsActifs.isEmpty()) {
            camionsActifs.clear();
            new Timer().schedule(new TimerTask() {
                @Override public void run() {
                    int jourSuivant = (jourActuel % maxJours) + 1;
                    lancerJournee(jourSuivant);
                }
            }, 1000);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dessiner les Secteurs (Points)
        for(Secteur s : ville.secteurs.values()) {
            Color couleurSecteur = (s.getJourAttribue() > 0 && s.getJourAttribue() < palette.length)
                    ? palette[s.getJourAttribue()] : Color.BLACK;

            for(IntersectionSimu i : s.pointsCollecte) {
                if(i.estVisite) {
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.fillOval(i.x-2, i.y-2, 4, 4);
                } else {
                    g2.setColor(couleurSecteur);
                    g2.fillOval(i.x-3, i.y-3, 6, 6);
                }
            }

            // Etiquettes Secteurs
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            String libelle = s.getNom() + " (J" + s.getJourAttribue() + ")";
            g2.drawString(libelle, s.zoneGeo.x + s.zoneGeo.width/2 - 40, s.zoneGeo.y + s.zoneGeo.height/2);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.drawString(String.format("%.1ft / %d camions", s.getQuantiteDechets(), s.camionsRequis),
                    s.zoneGeo.x + s.zoneGeo.width/2 - 40, s.zoneGeo.y + s.zoneGeo.height/2 + 15);
        }

        // Dessiner les Camions
        for(Camion c : camionsActifs) {
            g2.setColor(Color.LIGHT_GRAY);
            if(c.destination != null) g2.drawLine((int)c.x, (int)c.y, c.destination.x, c.destination.y);

            g2.setColor(Color.BLACK);
            g2.fillRect((int)c.x-6, (int)c.y-6, 12, 12);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 9));
            g2.drawString("C", (int)c.x-3, (int)c.y+4);
        }

        // Info Interface
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString("Simulation en cours", 20, 30);
        g2.drawString("Jour Actuel : " + jourActuel + " / " + maxJours, 20, 55);
    }
}