package Modele;

public class Arete {
    private Sommet sommetA;
    private Sommet sommetB;
    private double poids;

    public Arete(Sommet sommetA, Sommet sommetB, double poids) {
        this.sommetA = sommetA;
        this.sommetB = sommetB;
        this.poids = poids;
    }

    public Sommet getSommetA() { return sommetA; }
    public Sommet getSommetB() { return sommetB; }
    public double getPoids() { return poids; }

    @Override
    public String toString() {
        return sommetA.getId() + " --[" + poids + "]-- " + sommetB.getId();
    }
}