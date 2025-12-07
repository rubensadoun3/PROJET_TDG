package Utilitaire;

import Modele.*;

public class GenerateurGraphe {

    //GRAPHES BASIQUES 
    public static Graphe genererGrapheSimple() {
        Graphe g = new Graphe(false);
        Sommet depot = new Sommet("Depot", "Centre"); depot.setX(100); depot.setY(300);
        Sommet m1 = new Sommet("M1", "Maison 1");     m1.setX(300); m1.setY(100);
        Sommet m2 = new Sommet("M2", "Maison 2");     m2.setX(500); m2.setY(100);
        Sommet m3 = new Sommet("M3", "Maison 3");     m3.setX(700); m3.setY(300);
        g.ajouterSommet(depot); g.ajouterSommet(m1); g.ajouterSommet(m2); g.ajouterSommet(m3);
        g.ajouterArete("Depot", "M1", 100); g.ajouterArete("M1", "M2", 50);
        g.ajouterArete("M2", "M3", 75); g.ajouterArete("M3", "Depot", 120);
        return g;
    }

    public static Graphe genererCas1DegresPairs() {
        Graphe g = new Graphe(false);
        Sommet c1 = new Sommet("C1", "Centre"); c1.setX(400); c1.setY(300);
        Sommet c2 = new Sommet("C2", "HG"); c2.setX(200); c2.setY(100);
        Sommet c3 = new Sommet("C3", "BG"); c3.setX(200); c3.setY(500);
        Sommet c4 = new Sommet("C4", "BD"); c4.setX(600); c4.setY(500);
        Sommet c5 = new Sommet("C5", "MD"); c5.setX(800); c5.setY(300);
        Sommet c6 = new Sommet("C6", "HD"); c6.setX(600); c6.setY(100);
        g.ajouterSommet(c1); g.ajouterSommet(c2); g.ajouterSommet(c3);
        g.ajouterSommet(c4); g.ajouterSommet(c5); g.ajouterSommet(c6);
        g.ajouterArete("C1", "C2", 100); g.ajouterArete("C2", "C3", 150); g.ajouterArete("C3", "C1", 120);
        g.ajouterArete("C1", "C6", 110); g.ajouterArete("C6", "C5", 140); g.ajouterArete("C5", "C4", 130); g.ajouterArete("C4", "C1", 100);
        return g;
    }

    public static Graphe genererCas2DeuxImpairs() {
        Graphe g = new Graphe(false);
        Sommet c1 = new Sommet("C1", "BG"); c1.setX(300); c1.setY(500);
        Sommet c2 = new Sommet("C2", "BD"); c2.setX(700); c2.setY(500);
        Sommet c3 = new Sommet("C3", "HG"); c3.setX(300); c3.setY(200);
        Sommet c4 = new Sommet("C4", "HD"); c4.setX(700); c4.setY(200);
        Sommet c5 = new Sommet("C5", "Toit"); c5.setX(500); c5.setY(50);
        g.ajouterSommet(c1); g.ajouterSommet(c2); g.ajouterSommet(c3); g.ajouterSommet(c4); g.ajouterSommet(c5);
        g.ajouterArete("C1", "C2", 100); g.ajouterArete("C1", "C3", 150); g.ajouterArete("C1", "C4", 120);
        g.ajouterArete("C2", "C3", 110); g.ajouterArete("C3", "C4", 130); g.ajouterArete("C3", "C5", 150); g.ajouterArete("C4", "C5", 160);
        return g;
    }

    // GRAPHES MOYENS 30 Sommets

    public static Graphe genererGrandCycleEulerien(int nbSommets) {
        Graphe g = new Graphe(false);
        Sommet[] sommets = new Sommet[nbSommets];

        int centreX = 500;
        int centreY = 400;
        int rayon = 300; // Un peu plus petit pour bien centrer

        // Création des sommets en cercle
        for (int i = 0; i < nbSommets; i++) {
            double angle = 2 * Math.PI * i / nbSommets;
            int x = (int) (centreX + rayon * Math.cos(angle));
            int y = (int) (centreY + rayon * Math.sin(angle));

            sommets[i] = new Sommet("S" + (i+1), "" + (i+1));
            sommets[i].setX(x);
            sommets[i].setY(y);
            g.ajouterSommet(sommets[i]);
        }

        // Création des arêtes 
        for (int i = 0; i < nbSommets; i++) {
            Sommet s1 = sommets[i];

            // Voisin immédiat 
            Sommet s2 = sommets[(i + 1) % nbSommets];

            // Voisin N+2 (forme un deuxième anneau interne parallèle)
            // C'est beaucoup plus lisible que N+5
            Sommet s3 = sommets[(i + 2) % nbSommets];

            addAreteUnique(g, s1, s2);
            addAreteUnique(g, s1, s3);
        }

        return g;
    }

    public static Graphe genererGrandCheminEulerien(int nbSommets) {
        Graphe g = new Graphe(false);
        Sommet[] sommets = new Sommet[nbSommets];

        int centreX = 500; int centreY = 400; int rayon = 300;

        for (int i = 0; i < nbSommets; i++) {
            double angle = 2 * Math.PI * i / nbSommets;
            sommets[i] = new Sommet("S" + (i+1), "" + (i+1));
            sommets[i].setX((int) (centreX + rayon * Math.cos(angle)));
            sommets[i].setY((int) (centreY + rayon * Math.sin(angle)));
            g.ajouterSommet(sommets[i]);
        }

        for (int i = 0; i < nbSommets; i++) {
            Sommet sCurrent = sommets[i];
            Sommet sNext1 = sommets[(i + 1) % nbSommets];
            Sommet sNext2 = sommets[(i + 2) % nbSommets];

            // ON SUPPRIME UNE SEULE ARÊTE DU CERCLE EXTÉRIEUR
            // S0 et S1 deviennent impairs (Degré 3), les autres restent pairs (Degré 4).
            if (i != 0) {
                addAreteUnique(g, sCurrent, sNext1);
            }
            addAreteUnique(g, sCurrent, sNext2);
        }

        return g;
    }

    private static void addAreteUnique(Graphe g, Sommet s1, Sommet s2) {
        if (!s1.getVoisins().containsKey(s2)) {
            double dist = Math.sqrt(Math.pow(s1.getX()-s2.getX(), 2) + Math.pow(s1.getY()-s2.getY(), 2));
            g.ajouterArete(s1.getId(), s2.getId(), dist);
        }
    }
}
