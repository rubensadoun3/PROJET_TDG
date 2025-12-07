package Algorithmes;

import Modele.*;
import java.util.*;

public class GrapheComplet {
    private List<Sommet> sommets; // Dépôt + Points de collecte
    private double[][] matriceDistances;

    public GrapheComplet(Graphe grapheRoutier, Sommet depot, List<Sommet> pointsCollecte) {
        // 1. Liste des points d'intérêt (Index 0 = Dépôt)
        this.sommets = new ArrayList<>();
        this.sommets.add(depot);
        this.sommets.addAll(pointsCollecte);

        int n = sommets.size();
        this.matriceDistances = new double[n][n];

        System.out.println("Calcul des distances pour le MST (" + n + " points)...");

        // 2. Calculer les distances "One-to-All" pour chaque point d'intérêt
        for (int i = 0; i < n; i++) {
            Sommet source = sommets.get(i);

            // Appel de la méthode corrigée : on récupère une Map de toutes les distances
            Map<Sommet, Double> distancesDepuisSource = Dijkstra.calculerDistancesVersTous(grapheRoutier, source);

            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matriceDistances[i][j] = 0;
                } else {
                    Sommet cible = sommets.get(j);
                    // On récupère la distance dans la Map (ou INFINITY si pas atteignable)
                    Double dist = distancesDepuisSource.get(cible);
                    if (dist != null) {
                        matriceDistances[i][j] = dist;
                    } else {
                        matriceDistances[i][j] = Double.POSITIVE_INFINITY;
                    }
                }
            }
        }
    }

    public int getNbSommets() { return sommets.size(); }
    public double getDistance(int i, int j) { return matriceDistances[i][j]; }
    public Sommet getSommet(int i) { return sommets.get(i); }
}