package Simulation;

import java.util.*;

public class VilleSimu {
    // Liste des secteurs (Nord, Sud, etc.)
    public Map<Integer, Secteur> secteurs;

    // Liste de TOUTES les intersections (C'est ce qui manquait et causait l'erreur)
    public Map<String, IntersectionSimu> reseau;

    public VilleSimu() {
        this.secteurs = new HashMap<>();
        this.reseau = new HashMap<>();
    }

    // Méthode appelée par le bouton "Lancer" pour remettre à zéro avant de recalculer
    public void reinitialiserPlanning() {
        for (Secteur s : secteurs.values()) {
            s.setJourAttribue(-1);
            s.camionsRequis = 0;
        }
    }
}