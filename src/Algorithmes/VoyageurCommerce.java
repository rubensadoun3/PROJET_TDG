package Algorithmes;

import Modele.*;
import java.util.*;

public class VoyageurCommerce {

    // Approche simple "Plus proche voisin" (Greedy)
    public static Itineraire calculerTournee(Graphe graphe, Sommet depart, List<Sommet> aVisiter) {
        Itineraire itineraireComplet = new Itineraire();
        List<Sommet> resteAVisiter = new ArrayList<>(aVisiter);

        Sommet courant = depart;
        itineraireComplet.ajouterSommet(courant);

        while (!resteAVisiter.isEmpty()) {
            Sommet plusProche = null;
            double distMin = Double.MAX_VALUE;
            Itineraire cheminVersPlusProche = null;

            // Trouver le voisin le plus proche parmi la liste
            for (Sommet cible : resteAVisiter) {
                Itineraire chemin = Dijkstra.calculerPlusCourtChemin(graphe, courant, cible);
                if (chemin.getDistanceTotale() < distMin && chemin.getDistanceTotale() > 0) {
                    distMin = chemin.getDistanceTotale();
                    plusProche = cible;
                    cheminVersPlusProche = chemin;
                }
            }

            if (plusProche != null) {
                // Ajouter le chemin trouvé à l'itinéraire global
                // On saute le premier point car il est déjà dans l'itinéraire
                List<Sommet> segment = cheminVersPlusProche.getChemin();
                for (int i = 1; i < segment.size(); i++) {
                    itineraireComplet.ajouterSommet(segment.get(i));
                    // Note: Il faudrait ajouter les distances correctement ici
                }
                itineraireComplet.ajouterDistance(distMin);

                courant = plusProche;
                resteAVisiter.remove(plusProche);
            } else {
                break; // Bloqué ou fini
            }
        }

        // Retour au dépôt (optionnel selon le sujet, mais logique pour un camion)
        Itineraire retour = Dijkstra.calculerPlusCourtChemin(graphe, courant, depart);
        List<Sommet> segmentRetour = retour.getChemin();
        for (int i = 1; i < segmentRetour.size(); i++) {
            itineraireComplet.ajouterSommet(segmentRetour.get(i));
        }

        return itineraireComplet;
    }
}