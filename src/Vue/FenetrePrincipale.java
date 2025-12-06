package Vue;

import Modele.*;
import Algorithmes.*;
import Utilitaire.*;
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

        // Panneau Carte
        panneauMap = new PanneauGraphe(grapheVitry);
        this.add(panneauMap, BorderLayout.CENTER);

        // Menu Latéral
        JPanel panneauMenu = new JPanel();
        panneauMenu.setLayout(new GridLayout(8, 1, 10, 10));
        panneauMenu.setBackground(Color.LIGHT_GRAY);
        panneauMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panneauMenu.setPreferredSize(new Dimension(280, 900));

        JLabel lblTitre = new JLabel("SCÉNARIOS", SwingConstants.CENTER);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 18));
        panneauMenu.add(lblTitre);

        // Boutons
        JButton btnP1H1 = new JButton("Prob 1 - Hyp 1 (1 Client)");
        btnP1H1.addActionListener(e -> lancerP1H1());
        panneauMenu.add(btnP1H1);

        JButton btnP1H2 = new JButton("Prob 1 - Hyp 2 (Tournée TSP)");
        btnP1H2.addActionListener(e -> lancerP1H2());
        panneauMenu.add(btnP1H2);

        JButton btnP2C1 = new JButton("Prob 2 - Cas 1 (Cycle Eulérien)");
        btnP2C1.addActionListener(e -> lancerP2C1());
        panneauMenu.add(btnP2C1);

        JButton btnP2C2 = new JButton("Prob 2 - Cas 2 (Chemin Eulérien)");
        btnP2C2.addActionListener(e -> lancerP2C2());
        panneauMenu.add(btnP2C2);

        JButton btnP2C3 = new JButton("Prob 2 - Cas 3 (Postier Chinois)");
        btnP2C3.addActionListener(e -> lancerP2C3());
        panneauMenu.add(btnP2C3);

        JButton btnReset = new JButton("Retour Carte Vitry");
        btnReset.setBackground(new Color(173, 216, 230));
        btnReset.addActionListener(e -> panneauMap.setGraphe(grapheVitry, true));
        panneauMenu.add(btnReset);

        this.add(panneauMenu, BorderLayout.WEST);
        this.setVisible(true);
    }

    // --- LOGIQUE METIER ---

    private void lancerP1H1() {
        panneauMap.setGraphe(grapheVitry, true);
        List<Sommet> liste = new ArrayList<>(grapheVitry.getTousSommets());
        if (liste.size() < 2) return;
        Itineraire itineraire = Dijkstra.calculerPlusCourtChemin(grapheVitry, liste.get(0), liste.get(liste.size() - 1));
        panneauMap.animerItineraire(itineraire);
    }

    private void lancerP1H2() {
        panneauMap.setGraphe(grapheVitry, true);
        List<Sommet> liste = new ArrayList<>(grapheVitry.getTousSommets());
        if (liste.size() < 20) return;
        List<Sommet> clients = new ArrayList<>();
        Random rand = new Random();
        for(int i=0; i<5; i++) clients.add(liste.get(rand.nextInt(liste.size())));
        Itineraire tournee = VoyageurCommerce.calculerTournee(grapheVitry, liste.get(0), clients);
        panneauMap.animerItineraire(tournee);
    }

    // --- CAS 1 : CYCLE EULERIEN ---
    private void lancerP2C1() {
        int choix = JOptionPane.showConfirmDialog(this,
                "La carte de Vitry ne permet pas un Cycle Eulérien.\n" +
                        "Voulez-vous générer un graphe lisible (30 sommets) parfait pour la démo ?",
                "Choix du Scénario", JOptionPane.YES_NO_OPTION);

        if (choix == JOptionPane.YES_OPTION) {
            // ICI : 30 sommets au lieu de 100
            Graphe gFictif = GenerateurGraphe.genererGrandCycleEulerien(30);

            panneauMap.setGraphe(gFictif, false);
            List<Sommet> liste = new ArrayList<>(gFictif.getTousSommets());
            // Pour être sûr de partir d'un bon index
            Itineraire cycle = CheminEulerien.trouverCycleEulerien(gFictif, liste.get(0));
            panneauMap.animerItineraire(cycle);
        } else {
            panneauMap.setGraphe(grapheVitry, true);
            List<Sommet> liste = new ArrayList<>(grapheVitry.getTousSommets());
            CheminEulerien.trouverCycleEulerien(grapheVitry, liste.get(0));
        }
    }

    // --- CAS 2 : CHEMIN EULERIEN ---
    private void lancerP2C2() {
        int choix = JOptionPane.showConfirmDialog(this,
                "La carte de Vitry ne permet pas un Chemin Eulérien.\n" +
                        "Voulez-vous générer un graphe lisible (30 sommets) avec 2 impasses ?",
                "Choix du Scénario", JOptionPane.YES_NO_OPTION);

        if (choix == JOptionPane.YES_OPTION) {
            // ICI : 30 sommets au lieu de 100
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
        JOptionPane.showMessageDialog(this, "Cas Général (Postier Chinois) sur Vitry.\nSimulation de couverture (DFS).");
        Itineraire couverture = parcouriToutesRues(grapheVitry, liste.get(0));
        panneauMap.animerItineraire(couverture);
    }

    private Itineraire parcouriToutesRues(Graphe g, Sommet depart) {
        Itineraire result = new Itineraire();
        Stack<Sommet> pile = new Stack<>();
        Set<Sommet> visites = new HashSet<>();
        pile.push(depart); result.ajouterSommet(depart); visites.add(depart);

        while(!pile.isEmpty()) {
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
                    Sommet retour = pile.peek(); result.ajouterSommet(retour);
                    if(u.getVoisins().get(retour) != null) result.ajouterDistance(u.getVoisins().get(retour));
                }
            }
        }
        return result;
    }
}