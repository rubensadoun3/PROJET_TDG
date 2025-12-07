package Algorithmes;

import Modele.*;
import java.util.*;

public class VoyageurCommerce {

    public static Itineraire calculerTournee(Graphe graphe, Sommet depot, List<Sommet> clients) {
        System.out.println("--- Démarrage TSP (Réduction de Graphe) ---");

        //Création du Graphe Complet 
        // Calcule de toutes les distances Dijkstra entre les points d'intérêt
        GrapheComplet gVirtuel = new GrapheComplet(graphe, depot, clients);
        List<Integer> ordrePassageIndices = resoudreTSPSurMatrice(gVirtuel);

        //Reconstruction de l'itinéraire réel
        Itineraire itineraireFinal = new Itineraire();
        Sommet precedent = depot;
        itineraireFinal.ajouterSommet(depot); // Départ

        // On parcourt l'ordre trouvé 
        for (int i = 1; i < ordrePassageIndices.size(); i++) {
            int indexCible = ordrePassageIndices.get(i);
            Sommet cible = gVirtuel.getSommet(indexCible);

            // On demande à Dijkstra de retrouver le chemin réel entre ces deux points
            Itineraire segment = Dijkstra.calculerPlusCourtChemin(graphe, precedent, cible);

            // On ajoute ce segment à l'itinéraire global
            List<Sommet> rues = segment.getChemin();
            for (int k = 1; k < rues.size(); k++) { 
                itineraireFinal.ajouterSommet(rues.get(k));
            }
            itineraireFinal.ajouterDistance(segment.getDistanceTotale());

            precedent = cible;
        }

        System.out.println("--- Tournée calculée : " + itineraireFinal.getDistanceTotale() + "m ---");
        return itineraireFinal;
    }
    private static List<Integer> resoudreTSPSurMatrice(GrapheComplet g) {
        int n = g.getNbSommets();
        List<Integer> tournee = new ArrayList<>();
        boolean[] visite = new boolean[n];

        int courant = 0; // On part du dépôt (toujours index 0)
        tournee.add(0);
        visite[0] = true;

        for (int i = 0; i < n - 1; i++) {
            int meilleurSuivant = -1;
            double distMin = Double.MAX_VALUE;

            for (int candidat = 0; candidat < n; candidat++) {
                if (!visite[candidat]) {
                    double dist = g.getDistance(courant, candidat);
                    if (dist < distMin) {
                        distMin = dist;
                        meilleurSuivant = candidat;
                    }
                }
            }

            if (meilleurSuivant != -1) {
                visite[meilleurSuivant] = true;
                tournee.add(meilleurSuivant);
                courant = meilleurSuivant;
            }
        }

        // Retour au dépôt
        tournee.add(0);

        return tournee;
    }
}
