package Modele;

import java.util.ArrayList;
import java.util.List;

public class Tournee {
    private final List<Sommet> chemin = new ArrayList<>();
    private double distanceTotale = 0;
    private int chargeActuelle = 0;

    public void ajouterSommet(Sommet s) {
        chemin.add(s);
    }

    public void ajouterDistance(double d) {
        distanceTotale += d;
    }

    public void ajouterCharge(int q) {
        chargeActuelle += q;
    }

    public List<Sommet> getChemin() { return chemin; }
    public double getDistanceTotale() { return distanceTotale; }
    public int getChargeActuelle() { return chargeActuelle; }
}