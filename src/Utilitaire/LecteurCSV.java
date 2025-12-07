package Utilitaire;

import Modele.*;
import java.io.*;
import java.util.*;

public class LecteurCSV {

    // Bornes de Vitry (doivent correspondre à celles du GenerateurGPS)
    private static final double MIN_LAT = 48.7750;
    private static final double MAX_LAT = 48.8050;
    private static final double MIN_LON = 2.3700;
    private static final double MAX_LON = 2.4100;

    // Taille de la fenêtre d'affichage
    private static final int LARGEUR_ECRAN = 1000;
    private static final int HAUTEUR_ECRAN = 800;

    // Lecture du graphe principal (Carte)
    public static Graphe lireGraphe(String cheminFichier) {
        Graphe graphe = new Graphe(false);
        Map<String, List<Sommet>> rueVersSommets = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            br.readLine(); // Sauter l'en-tête

            while ((ligne = br.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] data = ligne.split(",");

                if (data.length < 4) continue; // Sécurité

                String id = data[0].trim();
                double lat = 0, lon = 0;
                try {
                    lat = Double.parseDouble(data[data.length - 2]);
                    lon = Double.parseDouble(data[data.length - 1]);
                } catch (Exception e) { continue; }

                Sommet sommet = new Sommet(id, "Croisement");
                sommet.setLatitude(lat);
                sommet.setLongitude(lon);

                // Conversion GPS -> Pixels
                int x = (int) ((lon - MIN_LON) / (MAX_LON - MIN_LON) * LARGEUR_ECRAN);
                int y = (int) (HAUTEUR_ECRAN - ((lat - MIN_LAT) / (MAX_LAT - MIN_LAT) * HAUTEUR_ECRAN));

                sommet.setX(Math.min(LARGEUR_ECRAN - 20, Math.max(20, x)));
                sommet.setY(Math.min(HAUTEUR_ECRAN - 20, Math.max(20, y)));

                graphe.ajouterSommet(sommet);

                // Rues
                for (int i = 1; i < data.length - 2; i++) {
                    String nomRue = data[i].trim();
                    if (!nomRue.isEmpty() && !nomRue.equals("0.0")) {
                        rueVersSommets.putIfAbsent(nomRue, new ArrayList<>());
                        rueVersSommets.get(nomRue).add(sommet);
                    }
                }
            }

            // Création des Arêtes
            for (List<Sommet> sommetsRue : rueVersSommets.values()) {
                sommetsRue.sort(Comparator.comparingDouble(Sommet::getLongitude));
                for (int i = 0; i < sommetsRue.size() - 1; i++) {
                    Sommet s1 = sommetsRue.get(i);
                    Sommet s2 = sommetsRue.get(i + 1);
                    if (!s1.getVoisins().containsKey(s2)) {
                        double dist = Math.sqrt(Math.pow(s1.getX() - s2.getX(), 2) + Math.pow(s1.getY() - s2.getY(), 2));
                        graphe.ajouterArete(s1.getId(), s2.getId(), dist);
                    }
                }
            }
            System.out.println("✓ Carte chargée : " + graphe.getTousSommets().size() + " sommets.");

        } catch (IOException e) {
            System.err.println("Erreur lecture graphe: " + e.getMessage());
        }
        return graphe;
    }

    // === NOUVELLE MÉTHODE POUR LE THÈME 2 ===
    public static List<Sommet> chargerPointsCollecte(String fichier, Graphe graphe) {
        List<Sommet> points = new ArrayList<>();
        System.out.println("Chargement des points de collecte : " + fichier);

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            br.readLine(); // Ignorer l'en-tête (idPoint;idSommet;contenance)
            String ligne;

            while ((ligne = br.readLine()) != null) {
                // ATTENTION : le fichier utilise des points-virgules ";"
                String[] data = ligne.split(";");
                if (data.length < 3) continue;

                String idSommetGraphe = data[1].trim(); // ex: Croisement565
                int contenance = 0;
                try {
                    contenance = Integer.parseInt(data[2].trim());
                } catch (NumberFormatException e) { continue; }

                Sommet s = graphe.getSommet(idSommetGraphe);
                if (s != null) {
                    s.setEstPointCollecte(true);
                    s.setQuantiteDechets(contenance);
                    points.add(s);
                }
            }
            System.out.println("✓ Points de collecte chargés : " + points.size());
        } catch (IOException e) {
            System.err.println("Erreur lecture collecte: " + e.getMessage());
        }
        return points;
    }
}