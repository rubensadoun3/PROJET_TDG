package Modele;

public class Particulier {
    private String id;
    private Sommet emplacement;
    private boolean collecte;

    public Particulier(String id, Sommet emplacement) {
        this.id = id;
        this.emplacement = emplacement;
        this.collecte = false;
    }

    public String getId() { return id; }
    public Sommet getEmplacement() { return emplacement; }
    public boolean estCollecte() { return collecte; }
    public void setCollecte(boolean collecte) { this.collecte = collecte; }
}