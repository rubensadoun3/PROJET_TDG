package Utilitaire;

import Modele.*;
import java.io.*;
import java.util.*;

public class LecteurCSV {

    // Bornes de Vitry (doivent correspondre à celles du GenerateurGPS_Secours)
    private static final double MIN_LAT = 48.7750;
    private static final double MAX_LAT = 48.8050;
    private static final double MIN_LON = 2.3700;
    private static final double MAX_LON = 2.4100;

    // Taille de la fenêtre d'affichage (pour la conversion)
    private static final int LARGEUR_ECRAN = 1000;
    private static final int HAUTEUR_ECRAN = 800;

    public static Graphe lireGraphe(String cheminFichier) {
        Graphe graphe = new Graphe(false);
        Map<String, List<Sommet>> rueVersSommets = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            br.readLine(); // Sauter l'en-tête

            while ((ligne = br.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] data = ligne.split(",");

                // On vérifie qu'on a bien les colonnes GPS (ajoutées à la fin)
                if (data.length < 4) continue;

                String id = data[0].trim();

                // Lecture GPS (Dernière et avant-dernière colonne)
                double lat = 0, lon = 0;
                try {
                    lat = Double.parseDouble(data[data.length - 2]);
                    lon = Double.parseDouble(data[data.length - 1]);
                } catch (Exception e) { continue; }

                Sommet sommet = new Sommet(id, "Croisement");
                sommet.setLatitude(lat);
                sommet.setLongitude(lon);

                // --- CONVERSION GPS VERS PIXELS ---
                // Formule de projection simple
                int x = (int) ((lon - MIN_LON) / (MAX_LON - MIN_LON) * LARGEUR_ECRAN);
                // On inverse Y car sur un écran le 0 est en haut, alors que la latitude monte vers le Nord
                int y = (int) (HAUTEUR_ECRAN - ((lat - MIN_LAT) / (MAX_LAT - MIN_LAT) * HAUTEUR_ECRAN));

                // Ajout d'une marge de 20px pour ne pas être collé au bord
                sommet.setX(Math.min(LARGEUR_ECRAN - 20, Math.max(20, x)));
                sommet.setY(Math.min(HAUTEUR_ECRAN - 20, Math.max(20, y)));

                graphe.ajouterSommet(sommet);

                // Gestion des rues (colonnes 1 à length-2)
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
                // On trie les points par longitude pour relier les voisins logiques
                sommetsRue.sort(Comparator.comparingDouble(Sommet::getLongitude));

                for (int i = 0; i < sommetsRue.size() - 1; i++) {
                    Sommet s1 = sommetsRue.get(i);
                    Sommet s2 = sommetsRue.get(i + 1);

                    if (!s1.getVoisins().containsKey(s2)) {
                        // Distance visuelle (Pythagore sur les pixels)
                        double dist = Math.sqrt(Math.pow(s1.getX() - s2.getX(), 2) + Math.pow(s1.getY() - s2.getY(), 2));
                        graphe.ajouterArete(s1.getId(), s2.getId(), dist);
                    }
                }
            }
            System.out.println("✓ Chargement terminé : " + graphe.getTousSommets().size() + " sommets positionnés.");

        } catch (IOException e) {
            System.err.println("Erreur lecture: " + e.getMessage());
        }

        return graphe;
    }
}