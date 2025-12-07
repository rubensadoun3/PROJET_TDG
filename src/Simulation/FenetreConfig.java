package Simulation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class FenetreConfig extends JFrame {

    private VilleSimu ville;

    public FenetreConfig(VilleSimu ville) {
        this.ville = ville;

        setTitle("Configuration de la Planification");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ferme juste cette fenêtre

        JPanel panneau = new JPanel(new GridBagLayout());
        panneau.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(panneau);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Titre
        JLabel lblTitre = new JLabel("Configuration de la Planification");
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitre.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panneau.add(lblTitre, gbc);

        // 2. Champs de saisie
        gbc.gridwidth = 1;
        gbc.gridy = 1; panneau.add(new JLabel("Nombre de Camions (N) :"), gbc);
        JTextField txtN = new JTextField("4");
        gbc.gridx = 1; panneau.add(txtN, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panneau.add(new JLabel("Charge Max par Camion (C) :"), gbc);
        JTextField txtC = new JTextField("8.0");
        gbc.gridx = 1; panneau.add(txtC, gbc);

        // 3. Choix Algorithme
        gbc.gridx = 0; gbc.gridy = 3; panneau.add(new JLabel("Algorithme :"), gbc);
        JRadioButton rbH1 = new JRadioButton("Hypothèse 1 : Voisinage Seul (Welsh-Powell)");
        JRadioButton rbH2 = new JRadioButton("Hypothèse 2 : Capacité Limitée (First-Fit Capacité)");
        rbH2.setSelected(true); // Par défaut

        ButtonGroup grp = new ButtonGroup();
        grp.add(rbH1);
        grp.add(rbH2);

        JPanel radioPanel = new JPanel(new GridLayout(2, 1));
        radioPanel.add(rbH1);
        radioPanel.add(rbH2);
        gbc.gridx = 1; panneau.add(radioPanel, gbc);

        // 4. Bouton Lancer
        JButton btnLancer = new JButton("Lancer la Planification");
        btnLancer.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLancer.setBackground(new Color(50, 150, 250));
        btnLancer.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panneau.add(btnLancer, gbc);

        // --- Action du bouton ---
        btnLancer.addActionListener(e -> {
            try {
                // Récupération des valeurs
                int n = Integer.parseInt(txtN.getText());
                double c = Double.parseDouble(txtC.getText());

                // Réinitialiser avant calcul
                ville.reinitialiserPlanning();

                int nbJours;
                // Choix de l'algo
                if(rbH1.isSelected()) {
                    nbJours = AlgoPlanification.resoudreWelshPowell(new ArrayList<>(ville.secteurs.values()), n, c);
                } else {
                    nbJours = AlgoPlanification.resoudreFirstFit(new ArrayList<>(ville.secteurs.values()), n, c);
                }

                // Fermer la config et lancer l'animation
                lancerSimulation(nbJours);
                this.dispose();

            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des nombres valides !");
            }
        });
    }

    private void lancerSimulation(int nbJours) {
        JFrame frameSimu = new JFrame("Simulation Animée");
        frameSimu.setSize(1000, 700);
        frameSimu.setLocationRelativeTo(null);

        // Création du panneau d'animation
        PanneauSimulation pan = new PanneauSimulation(ville);
        frameSimu.add(pan);

        frameSimu.setVisible(true);
        pan.demarrer(nbJours);
    }
}