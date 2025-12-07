package Simulation;

import java.util.ArrayList;
import java.util.List;

public class Camion {
    public double x, y;
    public IntersectionSimu destination;
    public List<IntersectionSimu> listePoints;

    public Camion(IntersectionSimu depart, List<IntersectionSimu> mission) {
        this.x = depart.x;
        this.y = depart.y;
        this.listePoints = new ArrayList<>(mission);
        if(!listePoints.isEmpty()) this.destination = listePoints.get(0);
    }
}