package Modele;


import java.util.*;

public class Itineraire {
    private List<Sommet> chemin;
    private double distanceTotale;

    public Itineraire() {
        this.chemin = new ArrayList<>();
        this.distanceTotale = 0;
    }

    public void ajouterSommet(Sommet s) {
        chemin.add(s);
    }

    public void ajouterDistance(double dist) {
        distanceTotale += dist;
    }

    public List<Sommet> getChemin() { return chemin; }
    public double getDistanceTotale() { return distanceTotale; }

    public void afficher() {
        System.out.println("\n=== ITINÉRAIRE ===");
        System.out.println("Distance totale: " + String.format("%.2f", distanceTotale) + " m");
        System.out.println("\nChemin:");
        for (int i = 0; i < chemin.size(); i++) {
            System.out.println("  " + (i+1) + ". " + chemin.get(i));
        }
    }
}