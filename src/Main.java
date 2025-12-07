import Modele.*;
import Utilitaire.*;
import Vue.FenetrePrincipale;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        /*Pour que le programme fonctionne :
        -run GenerateurGPS_Secours (GenerateurGPS ne fonctionne pas et n'arrive pas à
        transmettre les données à vitry_gps.csv)
        -run Main
         */

        System.out.println(" PROJET COLLECTE DÉCHETS AVEC CAMIONS");
        Graphe graphe = LecteurCSV.lireGraphe("data/vitry_gps.csv");
        SwingUtilities.invokeLater(() -> {
            new FenetrePrincipale(graphe);
        });
    }
}
