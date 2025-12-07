package Simulation;

import Modele.Graphe;
import Modele.Sommet;
import java.awt.Rectangle;

public class AdaptateurProjet {

    public static VilleSimu convertir(Graphe grapheEtudiant) {
        VilleSimu ville = new VilleSimu();

        // Découpage géographique de Vitry 
        // On crée 5 zones rectangulaires fictives
        Secteur no = new Secteur(0, "Nord-Ouest", new Rectangle(0, 0, 400, 350));
        Secteur ne = new Secteur(1, "Nord-Est", new Rectangle(400, 0, 600, 350));
        Secteur ce = new Secteur(2, "Centre", new Rectangle(250, 350, 500, 200));
        Secteur so = new Secteur(3, "Sud-Ouest", new Rectangle(0, 550, 500, 250));
        Secteur se = new Secteur(4, "Sud-Est", new Rectangle(500, 550, 500, 250));

        ville.secteurs.put(0, no); ville.secteurs.put(1, ne); ville.secteurs.put(2, ce);
        ville.secteurs.put(3, so); ville.secteurs.put(4, se);

        // Définir les voisins 
        // Le Centre touche tout le monde
        ce.ajouterVoisin(no); ce.ajouterVoisin(ne); ce.ajouterVoisin(so); ce.ajouterVoisin(se);
        no.ajouterVoisin(ce); no.ajouterVoisin(ne); no.ajouterVoisin(so);
        ne.ajouterVoisin(ce); ne.ajouterVoisin(no); ne.ajouterVoisin(se);
        so.ajouterVoisin(ce); so.ajouterVoisin(no); so.ajouterVoisin(se);
        se.ajouterVoisin(ce); se.ajouterVoisin(ne); se.ajouterVoisin(so);

        // Répartir les sommets dans ces secteurs
        for (Sommet s : grapheEtudiant.getTousSommets()) {
            IntersectionSimu inter = new IntersectionSimu(s.getId(), s.getX(), s.getY());

            // On regarde dans quel rectangle tombe le point
            if (ce.zoneGeo.contains(s.getX(), s.getY())) ce.pointsCollecte.add(inter);
            else if (no.zoneGeo.contains(s.getX(), s.getY())) no.pointsCollecte.add(inter);
            else if (ne.zoneGeo.contains(s.getX(), s.getY())) ne.pointsCollecte.add(inter);
            else if (so.zoneGeo.contains(s.getX(), s.getY())) so.pointsCollecte.add(inter);
            else se.pointsCollecte.add(inter);
        }

        // Générer des déchets aléatoires pour la simu
        for(Secteur s : ville.secteurs.values()) {
            s.setQuantiteDechets(s.pointsCollecte.size() * 0.15 + (Math.random() * 2));
        }

        return ville;
    }
}
