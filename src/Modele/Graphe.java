package Modele;

import java.util.*;

public class Graphe {
    private Map<String, Sommet> sommets;
    private List<Arete> aretes;
    private boolean estOriente;

    public Graphe(boolean estOriente) {
        this.sommets = new HashMap<>();
        this.aretes = new ArrayList<>();
        this.estOriente = estOriente;
    }

    public void ajouterSommet(Sommet sommet) {
        sommets.put(sommet.getId(), sommet);
    }

    public void ajouterArete(String idA, String idB, double poids) {
        Sommet a = sommets.get(idA);
        Sommet b = sommets.get(idB);

        Arete arete = new Arete(a, b, poids);
        aretes.add(arete);

        a.ajouterVoisin(b, poids);
        if (!estOriente) {
            b.ajouterVoisin(a, poids);
        }
    }

    public Sommet getSommet(String id) {
        return sommets.get(id);
    }

    public Collection<Sommet> getTousSommets() {
        return sommets.values();
    }

    public List<Arete> getToutesAretes() {
        return aretes;
    }

    public void reinitialiser() {
        for (Sommet s : sommets.values()) {
            s.reinitialiser();
        }
    }
}


