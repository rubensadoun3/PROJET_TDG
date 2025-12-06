package Modele;

public class CentreTraitement {
    private Sommet emplacement;
    private String nom;

    public CentreTraitement(Sommet emplacement, String nom) {
        this.emplacement = emplacement;
        this.nom = nom;
    }

    public Sommet getEmplacement() { return emplacement; }
    public String getNom() { return nom; }
}