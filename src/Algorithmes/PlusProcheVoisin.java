package Algorithmes;

import Modele.*;
import java.util.*;

public class PlusProcheVoisin {

    /**
     * Résout le problème en utilisant l'heuristique du plus proche voisin
     * avec contrainte de capacité (Retour au dépôt si plein).
     */
    public static List<Tournee> resoudre(Graphe graphe, Sommet depot, List<Sommet> pointsCollecte, int capaciteMax) {
        List<Tournee> solution = new ArrayList<>();

        // Liste des points restant à visiter (on exclut le dépôt s'il y est)
        Set<Sommet> aVisiter = new HashSet<>(pointsCollecte);
        aVisiter.remove(depot);

        Tournee tourneeCourante = new Tournee();
        // On commence la tournée au dépôt
        Sommet positionActuelle = depot;
        tourneeCourante.ajouterSommet(depot);

        while (!aVisiter.isEmpty()) {
            Sommet meilleurVoisin = null;
            double distMin = Double.MAX_VALUE;
            Itineraire cheminVersMeilleur = null;

            // 1. Trouver le point le plus proche parmi ceux restants
            // NOTE : Pour optimiser, on pourrait pré-calculer la matrice des distances,
            // mais ici on lance Dijkstra à la volée pour rester simple.
            for (Sommet candidat : aVisiter) {
                Itineraire it = Dijkstra.calculerPlusCourtChemin(graphe, positionActuelle, candidat);

                if (it.getDistanceTotale() < distMin) {
                    distMin = it.getDistanceTotale();
                    meilleurVoisin = candidat;
                    cheminVersMeilleur = it;
                }
            }

            if (meilleurVoisin == null) break; // Bloqué (graphe non connexe ?)

            // 2. Vérifier la capacité du camion
            if (tourneeCourante.getChargeActuelle() + meilleurVoisin.getQuantiteDechets() > capaciteMax) {
                // CAMION PLEIN : Retour au dépôt
                Itineraire retour = Dijkstra.calculerPlusCourtChemin(graphe, positionActuelle, depot);
                completerTournee(tourneeCourante, retour);

                // Valider cette tournée et en créer une nouvelle
                solution.add(tourneeCourante);

                tourneeCourante = new Tournee();
                positionActuelle = depot;
                tourneeCourante.ajouterSommet(depot);

                // On reprend la boucle sans avoir visité le point (on le fera avec le camion vide)
                continue;
            }

            // 3. Aller vers le voisin (Ajouter le chemin détaillé)
            completerTournee(tourneeCourante, cheminVersMeilleur);
            tourneeCourante.ajouterCharge(meilleurVoisin.getQuantiteDechets());

            // Mise à jour
            positionActuelle = meilleurVoisin;
            aVisiter.remove(meilleurVoisin);
        }

        // Fin : Retour au dépôt pour la dernière tournée
        Itineraire retourFinal = Dijkstra.calculerPlusCourtChemin(graphe, positionActuelle, depot);
        completerTournee(tourneeCourante, retourFinal);
        solution.add(tourneeCourante);

        return solution;
    }

    // Utilitaire pour ajouter les points intermédiaires d'un itinéraire à la tournée
    private static void completerTournee(Tournee t, Itineraire it) {
        List<Sommet> chemin = it.getChemin();
        // On commence à i=1 pour ne pas dupliquer le point de départ
        for (int i = 1; i < chemin.size(); i++) {
            t.ajouterSommet(chemin.get(i));
        }
        t.ajouterDistance(it.getDistanceTotale());
    }
}