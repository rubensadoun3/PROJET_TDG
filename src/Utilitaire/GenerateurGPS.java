package Utilitaire;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateurGPS {

    public static void main(String[] args) {
        // Fichiers
        String fichierEntree = "data/vitry_sur_seine_intersections.csv";
        String fichierSortie = "data/vitry_gps.csv";

        System.out.println("=== DÉMARRAGE DU GÉOCODAGE ===");

        try (BufferedReader br = new BufferedReader(new FileReader(fichierEntree));
             BufferedWriter bw = new BufferedWriter(new FileWriter(fichierSortie))) {

            // On copie l'en-tête et on ajoute les colonnes GPS
            String header = br.readLine();
            bw.write(header + ",Latitude,Longitude");
            bw.newLine();

            String ligne;
            int compteur = 0;

            while ((ligne = br.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] data = ligne.split(",");
                if (data.length < 2) continue;

                // On construit l'adresse : "Rue 1, Rue 2, Vitry-sur-Seine"
                String rue1 = data[1].replace("\"", "").trim();
                String rue2 = (data.length > 2) ? data[2].replace("\"", "").trim() : "";
                String requete = rue1 + " " + rue2 + " Vitry-sur-Seine France";

                // Appel API Nominatim (OpenStreetMap)
                double[] coord = interrogerNominatim(requete);

                // Si échec, on essaie juste avec la première rue
                if (coord == null) {
                    coord = interrogerNominatim(rue1 + " Vitry-sur-Seine France");
                }

                // Écriture du résultat
                if (coord != null) {
                    bw.write(ligne + "," + coord[0] + "," + coord[1]);
                    System.out.printf("[%d] Trouvé : %s -> %f, %f%n", ++compteur, data[0], coord[0], coord[1]);
                } else {
                    // Si pas trouvé, on met 0,0 (on filtrera après)
                    bw.write(ligne + ",0.0,0.0");
                    System.err.printf("[%d] ÉCHEC : %s%n", ++compteur, data[0]);
                }
                bw.newLine();

                // PAUSE OBLIGATOIRE de 1 seconde (pour ne pas se faire bannir par l'API)
                Thread.sleep(1100);
            }
            System.out.println("=== FINI ! Fichier créé : " + fichierSortie + " ===");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double[] interrogerNominatim(String adresse) {
        try {
            String encodedQuery = URLEncoder.encode(adresse, StandardCharsets.UTF_8);
            URL url = new URL("https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" + encodedQuery);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "ProjetEtudiant_ECE/1.0"); // Obligatoire

            if (conn.getResponseCode() != 200) return null;

            Scanner scanner = new Scanner(url.openStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) response.append(scanner.nextLine());
            scanner.close();

            String json = response.toString();
            if (json.length() < 5) return null; // Réponse vide

            // Extraction manuelle sans librairie JSON
            Pattern latPattern = Pattern.compile("\"lat\":\"([^\"]+)\"");
            Pattern lonPattern = Pattern.compile("\"lon\":\"([^\"]+)\"");
            Matcher mLat = latPattern.matcher(json);
            Matcher mLon = lonPattern.matcher(json);

            if (mLat.find() && mLon.find()) {
                return new double[]{Double.parseDouble(mLat.group(1)), Double.parseDouble(mLon.group(1))};
            }
        } catch (Exception e) { return null; }
        return null;
    }
}