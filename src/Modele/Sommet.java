package Modele;

import java.util.*;

public class Sommet {
    private String id;
    private String nom;
    private Map<Sommet, Double> voisins;

    // Coordonnées pour l'affichage (Pixels)
    private int x;
    private int y;

    // Coordonnées Géographiques (GPS)
    private double latitude;
    private double longitude;

    // Variables pour Dijkstra
    private boolean visite;
    private double distance;
    private Sommet predecesseur;

    public Sommet(String id, String nom) {
        this.id = id;
        this.nom = nom;
        this.voisins = new HashMap<>();
        reinitialiser();
    }

    public void ajouterVoisin(Sommet voisin, double distance) {
        voisins.put(voisin, distance);
    }

    public void reinitialiser() {
        this.visite = false;
        this.distance = Double.POSITIVE_INFINITY;
        this.predecesseur = null;
    }
    // --- MÉTHODES UTILES POUR LES GRAPHES ---

    // Retourne le nombre de routes connectées à ce croisement
    public int getDegre() {
        return voisins.size();
    }

    // Vérifie si le nombre de routes est pair (Utile pour Euler)
    public boolean estDegrePair() {
        return getDegre() % 2 == 0;
    }

    // GETTERS & SETTERS
    public String getId() { return id; }
    public Map<Sommet, Double> getVoisins() { return voisins; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double lat) { this.latitude = lat; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double lon) { this.longitude = lon; }

    public double getDistance() { return distance; }
    public void setDistance(double d) { this.distance = d; }
    public boolean estVisite() { return visite; }
    public void setVisite(boolean v) { this.visite = v; }
    public Sommet getPredecesseur() { return predecesseur; }
    public void setPredecesseur(Sommet p) { this.predecesseur = p; }

    @Override
    public String toString() { return id; }
}