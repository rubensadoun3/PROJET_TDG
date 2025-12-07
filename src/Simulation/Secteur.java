package Simulation;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Secteur implements Comparable<Secteur> {
    private int id;
    private String nom;
    private double quantiteDechets;
    private List<Secteur> voisins;
    private int jourAttribue; // -1 si pas encore planifié

    public List<IntersectionSimu> pointsCollecte;
    public int camionsRequis;
    public Rectangle zoneGeo; // Pour savoir où dessiner le nom

    public Secteur(int id, String nom, Rectangle zone) {
        this.id = id;
        this.nom = nom;
        this.zoneGeo = zone;
        this.voisins = new ArrayList<>();
        this.pointsCollecte = new ArrayList<>();
        this.jourAttribue = -1;
    }

    public void ajouterVoisin(Secteur s) {
        if (s != this && !voisins.contains(s)) {
            voisins.add(s);
        }
    }

    public int getDegre() {
        return voisins.size();
    }

    // Tri pour l'algorithme Welsh-Powell 
    @Override
    public int compareTo(Secteur autre) {
        int res = Integer.compare(autre.getDegre(), this.getDegre());
        if (res == 0) {
            return Double.compare(autre.quantiteDechets, this.quantiteDechets);
        }
        return res;
    }

    // Getters / Setters
    public String getNom() { return nom; }
    public double getQuantiteDechets() { return quantiteDechets; }
    public void setQuantiteDechets(double q) { this.quantiteDechets = q; }
    public int getJourAttribue() { return jourAttribue; }
    public void setJourAttribue(int jour) { this.jourAttribue = jour; }
    public List<Secteur> getVoisins() { return voisins; }
}
