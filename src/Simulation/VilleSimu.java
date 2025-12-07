package Simulation;

import java.util.*;

public class VilleSimu {
    // Liste des secteurs (Nord, Sud, etc.)
    public Map<Integer, Secteur> secteurs;

    // Liste de TOUTES les intersections 
    public Map<String, IntersectionSimu> reseau;

    public VilleSimu() {
        this.secteurs = new HashMap<>();
        this.reseau = new HashMap<>();
    }

    public void reinitialiserPlanning() {
        for (Secteur s : secteurs.values()) {
            s.setJourAttribue(-1);
            s.camionsRequis = 0;
        }
    }
}
