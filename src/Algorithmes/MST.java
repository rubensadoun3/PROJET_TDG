package Algorithmes;

import Modele.*;
import java.util.*;

public class MST {

    public static List<Tournee> resoudre(Graphe graphe, Sommet depot, List<Sommet> pointsCollecte, int capaciteMax) {
        // 1. Créer le graphe complet (distances entre tous les points)
        GrapheComplet gComplet = new GrapheComplet(graphe, depot, pointsCollecte);

        // 2. Calculer l'Arbre Couvrant Minimum (Prim)
        int[] parent = calculerPrim(gComplet);

        // 3. Convertir l'arbre en liste d'adjacence pour le parcours
        Map<Integer, List<Integer>> arbreAdj = new HashMap<>();
        for (int i = 0; i < gComplet.getNbSommets(); i++) arbreAdj.put(i, new ArrayList<>());

        for (int i = 1; i < gComplet.getNbSommets(); i++) {
            int p = parent[i];
            if (p != -1) {
                arbreAdj.get(p).add(i);
                arbreAdj.get(i).add(p); // Arbre non orienté
            }
        }

        // 4. Parcours en Profondeur (DFS) pour obtenir l'ordre géant
        List<Sommet> ordreVisite = new ArrayList<>();
        boolean[] visite = new boolean[gComplet.getNbSommets()];
        dfs(0, arbreAdj, gComplet, visite, ordreVisite);

        // 5. Découper en tournées (Splitting) selon la capacité
        return decouperEnTournees(graphe, depot, ordreVisite, capaciteMax);
    }

    // --- ALGO DE PRIM ---
    private static int[] calculerPrim(GrapheComplet g) {
        int n = g.getNbSommets();
        double[] keys = new double[n];
        int[] parent = new int[n];
        boolean[] inMST = new boolean[n];

        Arrays.fill(keys, Double.MAX_VALUE);
        Arrays.fill(parent, -1);

        keys[0] = 0; // Commencer par le dépôt

        for (int count = 0; count < n - 1; count++) {
            // Trouver le min
            double min = Double.MAX_VALUE;
            int u = -1;
            for (int v = 0; v < n; v++) {
                if (!inMST[v] && keys[v] < min) {
                    min = keys[v];
                    u = v;
                }
            }

            if (u == -1) break;
            inMST[u] = true;

            // Mettre à jour les voisins
            for (int v = 0; v < n; v++) {
                double dist = g.getDistance(u, v);
                if (!inMST[v] && dist < keys[v]) {
                    parent[v] = u;
                    keys[v] = dist;
                }
            }
        }
        return parent;
    }

    // --- PARCOURS DFS (Préordre) ---
    private static void dfs(int u, Map<Integer, List<Integer>> adj, GrapheComplet g, boolean[] visite, List<Sommet> ordre) {
        visite[u] = true;
        // On n'ajoute pas le dépôt (index 0) tout de suite dans la liste des points à visiter,
        // car la méthode de découpage gère le départ/retour dépôt.
        if (u != 0) {
            ordre.add(g.getSommet(u));
        }

        for (int v : adj.get(u)) {
            if (!visite[v]) {
                dfs(v, adj, g, visite, ordre);
            }
        }
    }

    // --- DECOUPAGE (SPLITTING) ---
    private static List<Tournee> decouperEnTournees(Graphe g, Sommet depot, List<Sommet> ordre, int capMax) {
        List<Tournee> solution = new ArrayList<>();
        Tournee courante = new Tournee();
        Sommet position = depot;
        courante.ajouterSommet(depot);

        for (Sommet cible : ordre) {
            // Si ça déborde, on rentre au dépôt et on change de camion
            if (courante.getChargeActuelle() + cible.getQuantiteDechets() > capMax) {
                // Retour dépôt
                Itineraire retour = Dijkstra.calculerPlusCourtChemin(g, position, depot);
                ajouterChemin(courante, retour);
                solution.add(courante);

                // Nouveau camion
                courante = new Tournee();
                position = depot;
                courante.ajouterSommet(depot);
            }

            // Aller vers la cible
            Itineraire aller = Dijkstra.calculerPlusCourtChemin(g, position, cible);
            ajouterChemin(courante, aller);
            courante.ajouterCharge(cible.getQuantiteDechets());
            position = cible;
        }

        // Finir la dernière tournée
        Itineraire fin = Dijkstra.calculerPlusCourtChemin(g, position, depot);
        ajouterChemin(courante, fin);
        solution.add(courante);

        return solution;
    }

    private static void ajouterChemin(Tournee t, Itineraire it) {
        List<Sommet> pts = it.getChemin();
        for (int i = 1; i < pts.size(); i++) {
            t.ajouterSommet(pts.get(i));
        }
        t.ajouterDistance(it.getDistanceTotale());
    }
}