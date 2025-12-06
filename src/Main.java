import Modele.*;
import Utilitaire.*;
import Vue.FenetrePrincipale;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== PROJET COLLECTE DÉCHETS ===");

        // ATTENTION : On charge le fichier généré à l'étape 1
        Graphe graphe = LecteurCSV.lireGraphe("data/vitry_gps.csv");

        SwingUtilities.invokeLater(() -> {
            new FenetrePrincipale(graphe);
        });
    }
}