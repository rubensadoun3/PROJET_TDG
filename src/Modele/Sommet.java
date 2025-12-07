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

    // === NOUVEAUX ATTRIBUTS (THEME 2 - DECHETS) ===
    private int quantiteDechets = 0;
    private boolean estPointCollecte = false;

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

    // --- MÉTHODES UTILES ---
    public int getDegre() {
        return voisins.size();
    }

    public boolean estDegrePair() {
        return getDegre() % 2 == 0;
    }

    // --- GETTERS & SETTERS CLASSIQUES ---
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

    // --- GETTERS & SETTERS (THEME 2) ---
    public int getQuantiteDechets() { return quantiteDechets; }
    public void setQuantiteDechets(int q) { this.quantiteDechets = q; }

    public boolean estPointCollecte() { return estPointCollecte; }
    public void setEstPointCollecte(boolean b) { this.estPointCollecte = b; }

    @Override
    public String toString() { return id; }
}