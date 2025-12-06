package Algorithmes;


import Modele.*;
import java.util.*;

public class Dijkstra {

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

            if (courant.estVisite()) {
                continue;
            }

            courant.setVisite(true);

            // Explorer voisins
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

    private static Itineraire reconstruireChemin(Sommet source, Sommet destination) {
        Itineraire itineraire = new Itineraire();

        if (destination.getDistance() == Double.POSITIVE_INFINITY) {
            System.out.println("Aucun chemin trouvé!");
            return itineraire;
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