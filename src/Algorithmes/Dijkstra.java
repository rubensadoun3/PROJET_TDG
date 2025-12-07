package Algorithmes;

import Modele.*;
import java.util.*;

public class Dijkstra {

    // Méthode pour aller d'un point A à un point B
    public static Itineraire calculerPlusCourtChemin(Graphe graphe, Sommet source, Sommet destination) {
        graphe.reinitialiser();

        PriorityQueue<Sommet> file = new PriorityQueue<>(
                Comparator.comparingDouble(Sommet::getDistance)
        );

        source.setDistance(0);
        file.add(source);

        while (!file.isEmpty()) {
            Sommet courant = file.poll();

            if (courant.equals(destination)) {
                break;
            }

            if (courant.estVisite()) continue;
            courant.setVisite(true);

            for (Map.Entry<Sommet, Double> entry : courant.getVoisins().entrySet()) {
                Sommet voisin = entry.getKey();
                double poids = entry.getValue();

                if (!voisin.estVisite()) {
                    double nouvelleDistance = courant.getDistance() + poids;
                    if (nouvelleDistance < voisin.getDistance()) {
                        voisin.setDistance(nouvelleDistance);
                        voisin.setPredecesseur(courant);
                        file.add(voisin);
                    }
                }
            }
        }
        return reconstruireChemin(source, destination);
    }
    public static Map<Sommet, Double> calculerDistancesVersTous(Graphe graphe, Sommet source) {
        graphe.reinitialiser();
        Map<Sommet, Double> distances = new HashMap<>();

        PriorityQueue<Sommet> file = new PriorityQueue<>(
                Comparator.comparingDouble(Sommet::getDistance)
        );

        source.setDistance(0);
        file.add(source);

        while (!file.isEmpty()) {
            Sommet courant = file.poll();

            if (courant.estVisite()) continue;
            courant.setVisite(true);

            // On enregistre la distance calculée
            distances.put(courant, courant.getDistance());

            for (Map.Entry<Sommet, Double> entry : courant.getVoisins().entrySet()) {
                Sommet voisin = entry.getKey();
                double poids = entry.getValue();

                if (!voisin.estVisite()) {
                    double nouvelleDistance = courant.getDistance() + poids;
                    if (nouvelleDistance < voisin.getDistance()) {
                        voisin.setDistance(nouvelleDistance);
                        voisin.setPredecesseur(courant);
                        file.add(voisin);
                    }
                }
            }
        }
        return distances;
    }

    private static Itineraire reconstruireChemin(Sommet source, Sommet destination) {
        Itineraire itineraire = new Itineraire();

        if (destination.getDistance() == Double.POSITIVE_INFINITY) {
            return itineraire; // Vide si pas de chemin
        }

        List<Sommet> cheminInverse = new ArrayList<>();
        Sommet courant = destination;

        while (courant != null) {
            cheminInverse.add(courant);
            courant = courant.getPredecesseur();
        }
        Collections.reverse(cheminInverse);

        for (Sommet s : cheminInverse) {
            itineraire.ajouterSommet(s);
        }
        itineraire.ajouterDistance(destination.getDistance());

        return itineraire;
    }
}
