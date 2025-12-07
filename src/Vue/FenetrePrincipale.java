package Vue;

import Modele.*;
import Algorithmes.*;
import Utilitaire.*;
import Simulation.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class FenetrePrincipale extends JFrame {
    private PanneauGraphe panneauMap;
    private Graphe grapheVitry;

    public FenetrePrincipale(Graphe grapheInitial) {
        this.grapheVitry = grapheInitial;

        this.setTitle("ECE - Projet Collecte Déchets - Vitry-sur-Seine");
        this.setSize(1200, 900);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // Panneau Carte (Centre)
        panneauMap = new PanneauGraphe(grapheVitry);
        this.add(panneauMap, BorderLayout.CENTER);

        // Panneau Menu (Gauche)
        JPanel panneauMenu = new JPanel();
        panneauMenu.setLayout(new GridLayout(12, 1, 10, 10));
        panneauMenu.setBackground(Color.LIGHT_GRAY);
        panneauMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panneauMenu.setPreferredSize(new Dimension(300, 900));

        JLabel lblTitre = new JLabel("SCÉNARIOS", SwingConstants.CENTER);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 18));
        panneauMenu.add(lblTitre);

        // --- THEME 1 (BASIQUE) ---
        JButton btnP1H1 = new JButton("Theme 1 - Prob 1 - Hyp 1 (1 Client)");
        btnP1H1.addActionListener(e -> lancerP1H1());
        panneauMenu.add(btnP1H1);

        // MODIFIÉ : Hypothèse 2 (TSP sur N points aléatoires)
        JButton btnP1H2 = new JButton("Theme 1 - Prob 1 - Hyp 2 (Tournée TSP)");
        btnP1H2.setToolTipText("Génère 10-15 points au hasard et optimise la tournée");
        btnP1H2.addActionListener(e -> lancerP1H2());
        panneauMenu.add(btnP1H2);

        // --- THEME 2 (OPTIMISATION AVANCÉE - 150 POINTS) ---
        JButton btnTh2 = new JButton("Theme 2 - Optimisation (Plus Proche Voisin)");
        btnTh2.setBackground(new Color(150, 250, 150)); // Vert clair
        btnTh2.addActionListener(e -> lancerTheme2());
        panneauMenu.add(btnTh2);

        JButton btnTh2MST = new JButton("Theme 2 - Optimisation (Approche MST)");
        btnTh2MST.setBackground(new Color(150, 200, 250)); // Bleu clair
        btnTh2MST.addActionListener(e -> lancerTheme2MST());
        panneauMenu.add(btnTh2MST);

        // --- THEME 3 (EULER & POSTIER CHINOIS) ---
        JButton btnP2C1 = new JButton("Theme 1 - Prob 2 - Cas 1 (Cycle Eulérien)");
        btnP2C1.addActionListener(e -> lancerP2C1());
        panneauMenu.add(btnP2C1);

        JButton btnP2C2 = new JButton("Theme 1 - Prob 2 - Cas 2 (Chemin Eulérien)");
        btnP2C2.addActionListener(e -> lancerP2C2());
        panneauMenu.add(btnP2C2);

        JButton btnP2C3 = new JButton("Theme 1 - Prob 2 - Cas 3 (Postier Chinois)");
        btnP2C3.addActionListener(e -> lancerP2C3());
        panneauMenu.add(btnP2C3);

        // --- THEME 3 (PLANIFICATION / SIMULATION) ---
        JButton btnP3 = new JButton("Theme 3 - Planification (Secteurs)");
        btnP3.setBackground(new Color(255, 200, 100)); // Orange
        btnP3.addActionListener(e -> lancerPlanification());
        panneauMenu.add(btnP3);

        // --- RESET ---
        JButton btnReset = new JButton("Retour Carte Vitry Sur Seine");
        btnReset.setBackground(new Color(173, 216, 230));
        btnReset.addActionListener(e -> panneauMap.setGraphe(grapheVitry, true));
        panneauMenu.add(btnReset);

        this.add(panneauMenu, BorderLayout.WEST);
        this.setVisible(true);
    }

    // ============================================================
    // LOGIQUE METIER - THEME 1
    // ============================================================

    private void lancerP1H1() {
        panneauMap.setGraphe(grapheVitry, true);
        List<Sommet> liste = new ArrayList<>(grapheVitry.getTousSommets());
        if (liste.size() < 2) return;
        // Calcul simple entre le premier et le dernier sommet de la liste du graphe
        Itineraire itineraire = Dijkstra.calculerPlusCourtChemin(grapheVitry, liste.get(0), liste.get(liste.size() - 1));
        panneauMap.animerItineraire(itineraire);
    }

    /**
     * HYPOTHESE 2 : TSP avec Graphe Complet (Réduction)
     * Demande le nombre de points (10-15) et calcule la tournée via VoyageurCommerce.
     */
    private void lancerP1H2() {
        panneauMap.setGraphe(grapheVitry, true);

        // 1. Demander à l'utilisateur le nombre de points
        String input = JOptionPane.showInputDialog(this,
                "Combien de points de collecte voulez-vous visiter ?\n(Conseillé : entre 10 et 15)",
                "10");

        int nombrePoints = 10;
        try {
            if (input != null && !input.trim().isEmpty()) {
                nombrePoints = Integer.parseInt(input.trim());
            } else {
                return; // Annulé
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nombre invalide !");
            return;
        }

        // 2. Sélectionner aléatoirement ces points dans la ville
        List<Sommet> tousSommets = new ArrayList<>(grapheVitry.getTousSommets());
        if (tousSommets.isEmpty()) return;

        // On prend le premier sommet comme Dépôt (pour avoir une référence fixe)
        Sommet depot = tousSommets.get(0);
        List<Sommet> clientsAVisiter = new ArrayList<>();

        Random rand = new Random();
        // On remplit la liste jusqu'à avoir le nombre demandé
        while (clientsAVisiter.size() < nombrePoints) {
            Sommet s = tousSommets.get(rand.nextInt(tousSommets.size()));
            // On évite les doublons et le dépôt
            if (!clientsAVisiter.contains(s) && !s.equals(depot)) {
                clientsAVisiter.add(s);
            }
        }

        JOptionPane.showMessageDialog(this,
                "Calcul de la tournée optimisée pour " + nombrePoints + " clients...\n" +
                        "1. Calcul des distances réelles (Dijkstra)\n" +
                        "2. Résolution du TSP (Graphe réduit)\n" +
                        "3. Reconstruction de l'itinéraire",
                "Calcul TSP", JOptionPane.INFORMATION_MESSAGE);

        // 3. Lancer l'algorithme mis à jour (via GrapheComplet)
        try {
            // Note : Assurez-vous que VoyageurCommerce a bien été mis à jour avec la logique GrapheComplet
            Itineraire tournee = VoyageurCommerce.calculerTournee(grapheVitry, depot, clientsAVisiter);
            panneauMap.animerItineraire(tournee);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur calcul : " + e.getMessage());
        }
    }

    // ============================================================
    // LOGIQUE METIER - THEME 2 (OPTIMISATION AVEC CAPACITÉ)
    // ============================================================

    private void lancerTheme2() {
        List<Sommet> pointsCollecte = LecteurCSV.chargerPointsCollecte("data/vitry_collecte150.csv", grapheVitry);
        if (pointsCollecte.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Erreur : Vérifiez 'data/vitry_collecte150.csv'");
            return;
        }

        Sommet depot = grapheVitry.getSommet("Croisement1");
        if (depot == null && !pointsCollecte.isEmpty()) depot = grapheVitry.getTousSommets().iterator().next();

        JOptionPane.showMessageDialog(this, "Calcul (Plus Proche Voisin) en cours...");

        Sommet finalDepot = depot;
        new Thread(() -> {
            // Capacité camion = 30
            List<Tournee> solution = PlusProcheVoisin.resoudre(grapheVitry, finalDepot, pointsCollecte, 30);

            SwingUtilities.invokeLater(() -> {
                Itineraire affichage = new Itineraire();
                for (Tournee t : solution) {
                    for (Sommet s : t.getChemin()) affichage.ajouterSommet(s);
                }
                panneauMap.setGraphe(grapheVitry, true);
                panneauMap.animerItineraire(affichage);

                JOptionPane.showMessageDialog(this, "Optimisation (PPV) Terminée !\nTournées : " + solution.size());
            });
        }).start();
    }

    private void lancerTheme2MST() {
        List<Sommet> pointsCollecte = LecteurCSV.chargerPointsCollecte("data/vitry_collecte150.csv", grapheVitry);
        if (pointsCollecte.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Erreur chargement points.");
            return;
        }

        Sommet depot = grapheVitry.getSommet("Croisement1");
        if (depot == null) depot = grapheVitry.getTousSommets().iterator().next();

        JOptionPane.showMessageDialog(this, "Calcul MST en cours (peut être long)...");

        Sommet finalDepot = depot;
        new Thread(() -> {
            List<Tournee> solution = MST.resoudre(grapheVitry, finalDepot, pointsCollecte, 30);

            SwingUtilities.invokeLater(() -> {
                Itineraire affichage = new Itineraire();
                for (Tournee t : solution) {
                    for (Sommet s : t.getChemin()) affichage.ajouterSommet(s);
                }
                panneauMap.setGraphe(grapheVitry, true);
                panneauMap.animerItineraire(affichage);

                JOptionPane.showMessageDialog(this, "Optimisation (MST) Terminée !\nTournées : " + solution.size());
            });
        }).start();
    }

    // ============================================================
    // LOGIQUE METIER - GRAPHES EULERIENS & POSTIER CHINOIS
    // ============================================================

    private void lancerP2C1() {
        int choix = JOptionPane.showConfirmDialog(this,
                "Voulez-vous générer un graphe lisible (30 sommets) parfait pour la démo ?",
                "Choix du Scénario", JOptionPane.YES_NO_OPTION);

        if (choix == JOptionPane.YES_OPTION) {
            Graphe gFictif = GenerateurGraphe.genererGrandCycleEulerien(30);
            panneauMap.setGraphe(gFictif, false);
            List<Sommet> liste = new ArrayList<>(gFictif.getTousSommets());
            Itineraire cycle = CheminEulerien.trouverCycleEulerien(gFictif, liste.get(0));
            panneauMap.animerItineraire(cycle);
        } else {
            panneauMap.setGraphe(grapheVitry, true);
            List<Sommet> liste = new ArrayList<>(grapheVitry.getTousSommets());
            CheminEulerien.trouverCycleEulerien(grapheVitry, liste.get(0));
        }
    }

    private void lancerP2C2() {
        int choix = JOptionPane.showConfirmDialog(this,
                "Voulez-vous générer un graphe lisible (30 sommets) avec 2 impasses ?",
                "Choix du Scénario", JOptionPane.YES_NO_OPTION);

        if (choix == JOptionPane.YES_OPTION) {
            Graphe gFictif = GenerateurGraphe.genererGrandCheminEulerien(30);
            panneauMap.setGraphe(gFictif, false);
            Itineraire chemin = CheminEulerien.trouverCheminEulerien(gFictif);
            panneauMap.animerItineraire(chemin);
        } else {
            panneauMap.setGraphe(grapheVitry, true);
            CheminEulerien.trouverCheminEulerien(grapheVitry);
        }
    }

    private void lancerP2C3() {
        panneauMap.setGraphe(grapheVitry, true);
        List<Sommet> liste = new ArrayList<>(grapheVitry.getTousSommets());

        String input = JOptionPane.showInputDialog(this,
                "Entrez le nombre maximum d'intersections à parcourir :\n(Pour éviter que la simulation soit trop longue)", "100");
        int maxEtapes = 100;
        try { if(input != null) maxEtapes = Integer.parseInt(input); } catch(Exception e) {}

        Itineraire couverture = parcouriToutesRues(grapheVitry, liste.get(0), maxEtapes);
        panneauMap.animerItineraire(couverture);
    }

    // Helper pour parcourir le graphe (DFS simple)
    private Itineraire parcouriToutesRues(Graphe g, Sommet depart, int limite) {
        Itineraire result = new Itineraire();
        Stack<Sommet> pile = new Stack<>();
        Set<Sommet> visites = new HashSet<>();
        pile.push(depart); result.ajouterSommet(depart); visites.add(depart);

        while(!pile.isEmpty()) {
            if (result.getChemin().size() >= limite) break;
            Sommet u = pile.peek();
            Sommet v = null;
            for (Sommet voisin : u.getVoisins().keySet()) {
                if (!visites.contains(voisin)) { v = voisin; break; }
            }
            if (v != null) {
                visites.add(v); result.ajouterSommet(v); pile.push(v);
                if(u.getVoisins().get(v) != null) result.ajouterDistance(u.getVoisins().get(v));
            } else {
                pile.pop();
                if (!pile.isEmpty()) {
                    if (result.getChemin().size() >= limite) break;
                    Sommet retour = pile.peek(); result.ajouterSommet(retour);
                    if(u.getVoisins().get(retour) != null) result.ajouterDistance(u.getVoisins().get(retour));
                }
            }
        }
        return result;
    }

    // ============================================================
    // LOGIQUE METIER - THEME 3 (PLANIFICATION SECTEURS)
    // ============================================================

    private void lancerPlanification() {
        // 1. Adapter ton graphe Vitry vers la structure de Simulation
        VilleSimu villeSimu = AdaptateurProjet.convertir(this.grapheVitry);

        // 2. Ouvrir la fenêtre de configuration (Écran 1)
        new FenetreConfig(villeSimu).setVisible(true);
    }
}