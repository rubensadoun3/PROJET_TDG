package Algorithmes; 

import Modele.*;    
import java.util.*;

public class CheminEulerien {

   // CAS 1: Tous degrés pairs donc cylce eulérien
    public static Itineraire trouverCycleEulerien(Graphe graphe, Sommet depart) {
        for (Sommet s : graphe.getTousSommets()) {
            if (!s.estDegrePair()) {
                System.err.println("ERREUR GRAVE: Le sommet " + s.getId() + " est de degré impair (" + s.getDegre() + ").");
                System.err.println("Impossible de former une boucle parfaite (Cycle Eulérien).");
                return new Itineraire(); // Retourne vide
            }
        }

        System.out.println("✓ Graphe valide (Tous degrés pairs). Calcul du cycle...");
        return algorithmeHierholzer(graphe, depart);
    }

    // CAS 2: Exactement 2 degrés impairs donc chemin eulérien
    public static Itineraire trouverCheminEulerien(Graphe graphe) {
        List<Sommet> impairs = new ArrayList<>();

        for (Sommet s : graphe.getTousSommets()) {
            if (!s.estDegrePair()) {
                impairs.add(s);
            }
        }

        // Vérification : un chemin eulérien nécessite EXACTEMENT 2 sommets impairs
        if (impairs.size() != 2) {
            System.err.println("ERREUR: Le graphe contient " + impairs.size() + " sommets de degré impair.");
            System.err.println("Il en faut exactement 2 pour un Chemin Eulérien.");
            return new Itineraire(); // Retourne vide
        }

        System.out.println("✓ Graphe valide (2 degrés impairs). Calcul du chemin...");
        System.out.println("  -> Départ forcé : " + impairs.get(0).getId());
        System.out.println("  -> Arrivée prévue : " + impairs.get(1).getId());

        return algorithmeHierholzer(graphe, impairs.get(0));
    }

    private static Itineraire algorithmeHierholzer(Graphe graphe, Sommet depart) {
        Map<Sommet, List<Sommet>> aretesRestantes = new HashMap<>();

        for (Sommet s : graphe.getTousSommets()) {
            aretesRestantes.put(s, new ArrayList<>(s.getVoisins().keySet()));
        }

        Stack<Sommet> pile = new Stack<>();
        List<Sommet> circuit = new ArrayList<>();

        pile.push(depart);

        while (!pile.isEmpty()) {
            Sommet u = pile.peek(); // On regarde le sommet actuel sans l'enlever

            if (aretesRestantes.containsKey(u) && !aretesRestantes.get(u).isEmpty()) {

                Sommet v = aretesRestantes.get(u).remove(0);

                if (aretesRestantes.containsKey(v)) {
                    aretesRestantes.get(v).remove(u);
                }

                pile.push(v);
            } else {

                circuit.add(pile.pop());
            }
        }

        Collections.reverse(circuit);

        Itineraire itineraire = new Itineraire();
        double distanceTotale = 0;

        for (int i = 0; i < circuit.size(); i++) {
            Sommet s = circuit.get(i);
            itineraire.ajouterSommet(s);

            // Calcul de la distance cumulée
            if (i > 0) {
                Sommet precedent = circuit.get(i - 1);

                Double dist = precedent.getVoisins().get(s);

                if (dist != null) {
                    distanceTotale += dist;
                } else {
                    System.err.println("Attention: Arête manquante entre " + precedent.getId() + " et " + s.getId());
                }
            }
        }

        itineraire.ajouterDistance(distanceTotale);
        return itineraire;
    }
}
