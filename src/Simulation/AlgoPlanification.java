package Simulation;

import java.util.*;

public class AlgoPlanification {

    // Hypothèse 1 : Welsh-Powell (Coloration pure, ignore la capacité C pour le jour)
    public static int resoudreWelshPowell(List<Secteur> secteurs, int nbCamions, double chargeMax) {
        List<Secteur> tries = new ArrayList<>(secteurs);
        Collections.sort(tries); // Tri par degré décroissant

        int jour = 0;
        int nbTraites = 0;

        while (nbTraites < tries.size()) {
            jour++;
            for (Secteur s : tries) {
                if (s.getJourAttribue() == -1) {
                    boolean conflit = false;
                    for (Secteur v : s.getVoisins()) {
                        if (v.getJourAttribue() == jour) { conflit = true; break; }
                    }
                    if (!conflit) {
                        s.setJourAttribue(jour);
                        // Calcul camions requis
                        s.camionsRequis = (int) Math.ceil(s.getQuantiteDechets() / chargeMax);
                        if(s.camionsRequis < 1) s.camionsRequis = 1;
                        nbTraites++;
                    }
                }
            }
            // Sécurité anti-boucle infinie (si graphe non colorable, rare ici)
            if(jour > 100) break;
        }
        return jour;
    }

    // Hypothèse 2 : First-Fit avec contrainte de capacité (CORRIGÉ)
    public static int resoudreFirstFit(List<Secteur> secteurs, int nbCamionsFlotte, double chargeMax) {
        List<Secteur> tries = new ArrayList<>(secteurs);
        // Tri : Degré décroissant, puis quantité de déchets décroissante (pour placer les gros d'abord)
        Collections.sort(tries);

        int jour = 0;
        int nbTraites = 0;

        // SÉCURITÉ : Vérifier qu'aucun secteur n'est plus gros que la flotte entière
        for(Secteur s : tries) {
            int besoin = (int) Math.ceil(s.getQuantiteDechets() / chargeMax);
            if (besoin > nbCamionsFlotte) {
                System.err.println("ERREUR CRITIQUE : Le secteur " + s.getNom() + " nécessite " + besoin + " camions, mais la flotte est de " + nbCamionsFlotte + ".");
                // On force le besoin au max possible pour éviter le crash, mais c'est une incohérence logique
                besoin = nbCamionsFlotte;
            }
        }

        while (nbTraites < tries.size()) {
            jour++;
            int camionsDispoCeJour = nbCamionsFlotte;
            boolean auMoinsUnSecteurPlaceCeJour = false;

            for (Secteur s : tries) {
                if (s.getJourAttribue() == -1) {
                    int besoin = (int) Math.ceil(s.getQuantiteDechets() / chargeMax);
                    if (besoin < 1) besoin = 1;
                    // Plafonnement sécurité si besoin > flotte (ne devrait plus arriver avec le check ci-dessus)
                    if (besoin > nbCamionsFlotte) besoin = nbCamionsFlotte;

                    boolean conflitVoisin = false;
                    for (Secteur v : s.getVoisins()) {
                        if (v.getJourAttribue() == jour) { conflitVoisin = true; break; }
                    }

                    // On place si : Pas de conflit ET Assez de camions restants ce jour-là
                    if (!conflitVoisin && camionsDispoCeJour >= besoin) {
                        s.setJourAttribue(jour);
                        s.camionsRequis = besoin;
                        camionsDispoCeJour -= besoin;
                        nbTraites++;
                        auMoinsUnSecteurPlaceCeJour = true;
                    }
                }
            }

            // DETECTEUR DE BLOCAGE (Anti boucle infinie)
            // Si on passe une journée entière sans pouvoir placer AUCUN secteur restant, on est coincé.
            if (!auMoinsUnSecteurPlaceCeJour && nbTraites < tries.size()) {
                System.err.println("BLOCAGE DÉTECTÉ : Impossible de placer les secteurs restants avec les contraintes actuelles.");
                // On force le placement sur des jours suivants un par un pour débloquer
                for(Secteur s : tries) {
                    if(s.getJourAttribue() == -1) {
                        jour++;
                        s.setJourAttribue(jour);
                        s.camionsRequis = Math.min((int) Math.ceil(s.getQuantiteDechets() / chargeMax), nbCamionsFlotte);
                        nbTraites++;
                    }
                }
                break;
            }
        }
        return jour;
    }
}