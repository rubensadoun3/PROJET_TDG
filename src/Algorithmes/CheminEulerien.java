package Algorithmes; // Attention à la Majuscule si votre dossier est "Algorithmes"

import Modele.*;    // Attention à la Majuscule si votre dossier est "Modele"
import java.util.*;

public class CheminEulerien {

   // CAS 1: Tous degrés pairs (Cycle Eulérien) [cite: 183]
    public static Itineraire trouverCycleEulerien(Graphe graphe, Sommet depart) {
        // Vérification : un cycle eulérien nécessite que TOUS les sommets soient de degré pair
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

    // CAS 2: Exactement 2 degrés impairs (Chemin Eulérien) [cite: 184]
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

        // On DOIT partir de l'un des deux sommets impairs
        return algorithmeHierholzer(graphe, impairs.get(0));
    }

    // --- CŒUR DE L'ALGORITHME DE HIERHOLZER ---
    private static Itineraire algorithmeHierholzer(Graphe graphe, Sommet depart) {
        // 1. Copie locale des arêtes pour les "consommer" sans détruire le graphe original
        Map<Sommet, List<Sommet>> aretesRestantes = new HashMap<>();

        for (Sommet s : graphe.getTousSommets()) {
            // On crée une liste modifiable des voisins pour chaque sommet
            aretesRestantes.put(s, new ArrayList<>(s.getVoisins().keySet()));
        }

        Stack<Sommet> pile = new Stack<>();
        List<Sommet> circuit = new ArrayList<>();

        pile.push(depart);

        // Boucle principale
        while (!pile.isEmpty()) {
            Sommet u = pile.peek(); // On regarde le sommet actuel sans l'enlever

            // Vérifie si u a encore des voisins à visiter
            if (aretesRestantes.containsKey(u) && !aretesRestantes.get(u).isEmpty()) {

                // On emprunte l'arête vers le premier voisin disponible (v)
                Sommet v = aretesRestantes.get(u).remove(0);

                // IMPORTANT (Graphe Non Orienté) :
                // Si on va de U à V, on coupe aussi le chemin retour de V à U
                if (aretesRestantes.containsKey(v)) {
                    aretesRestantes.get(v).remove(u);
                }

                // On empile V pour continuer d'avancer
                pile.push(v);
            } else {
                // Si U n'a plus de chemin inexploré, on l'ajoute au circuit final et on recule
                circuit.add(pile.pop());
            }
        }

        // L'algorithme construit le chemin à l'envers, on le remet à l'endroit
        Collections.reverse(circuit);

        // 2. Conversion de la liste de sommets en objet Itineraire avec calcul des distances
        Itineraire itineraire = new Itineraire();
        double distanceTotale = 0;

        for (int i = 0; i < circuit.size(); i++) {
            Sommet s = circuit.get(i);
            itineraire.ajouterSommet(s);

            // Calcul de la distance cumulée
            if (i > 0) {
                Sommet precedent = circuit.get(i - 1);

                // On récupère la distance réelle depuis le graphe original
                Double dist = precedent.getVoisins().get(s);

                if (dist != null) {
                    distanceTotale += dist;
                } else {
                    // Sécurité si le graphe est mal formé
                    System.err.println("Attention: Arête manquante entre " + precedent.getId() + " et " + s.getId());
                }
            }
        }

        itineraire.ajouterDistance(distanceTotale);
        return itineraire;
    }
}
