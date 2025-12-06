package Utilitaire;

import java.io.*;
import java.util.Random;

    public class GenerateurGPS_Secours {

        // Boite englobante de Vitry-sur-Seine (Lat/Lon approximatifs)
        private static final double MIN_LAT = 48.7750;
        private static final double MAX_LAT = 48.8050;
        private static final double MIN_LON = 2.3700;
        private static final double MAX_LON = 2.4100;

        public static void main(String[] args) {
            String fichierEntree = "data/vitry_sur_seine_intersections.csv";
            String fichierSortie = "data/vitry_gps.csv"; // On écrase l'ancien

            System.out.println("=== GÉNÉRATION GPS ALÉATOIRE (VITRY) ===");

            try (BufferedReader br = new BufferedReader(new FileReader(fichierEntree));
                 BufferedWriter bw = new BufferedWriter(new FileWriter(fichierSortie))) {

                String header = br.readLine();
                // On ajoute les colonnes Latitude et Longitude
                bw.write(header + ",Latitude,Longitude");
                bw.newLine();

                String ligne;
                Random random = new Random();
                int count = 0;

                while ((ligne = br.readLine()) != null) {
                    if (ligne.trim().isEmpty()) continue;

                    // Génération d'une position aléatoire DANS Vitry
                    double lat = MIN_LAT + (MAX_LAT - MIN_LAT) * random.nextDouble();
                    double lon = MIN_LON + (MAX_LON - MIN_LON) * random.nextDouble();

                    // On écrit la ligne originale + les fausses coordonnées GPS
                    bw.write(ligne + "," + lat + "," + lon);
                    bw.newLine();
                    count++;
                }

                System.out.println("✓ Terminé ! " + count + " sommets générés dans data/vitry_gps.csv");
                System.out.println("Vous pouvez maintenant lancer le Main !");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }