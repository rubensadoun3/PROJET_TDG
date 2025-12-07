package Algorithmes;

import Modele.*;
import java.util.*;

public class PlusProcheVoisin {

    public static List<Tournee> resoudre(Graphe graphe, Sommet depot, List<Sommet> pointsCollecte, int capaciteMax) {
        List<Tournee> solution = new ArrayList<>();

        // Liste des points restant à visiter
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

            // Trouver le point le plus proche parmi ceux restants
            
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

                continue;
            }

            completerTournee(tourneeCourante, cheminVersMeilleur);
            tourneeCourante.ajouterCharge(meilleurVoisin.getQuantiteDechets());

            
            positionActuelle = meilleurVoisin;
            aVisiter.remove(meilleurVoisin);
        }

        //Retour au dépôt pour la dernière tournée
        Itineraire retourFinal = Dijkstra.calculerPlusCourtChemin(graphe, positionActuelle, depot);
        completerTournee(tourneeCourante, retourFinal);
        solution.add(tourneeCourante);

        return solution;
    }

    private static void completerTournee(Tournee t, Itineraire it) {
        List<Sommet> chemin = it.getChemin();
     
        for (int i = 1; i < chemin.size(); i++) {
            t.ajouterSommet(chemin.get(i));
        }
        t.ajouterDistance(it.getDistanceTotale());
    }
}
