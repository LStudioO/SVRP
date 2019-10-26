/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lstudio.ui;

import com.lstudio.algorithms.antcolony.island.IslandOptimization;
import com.lstudio.algorithms.antcolony.island.topology.HypercubeTopology;
import com.lstudio.algorithms.antcolony.island.topology.RingTopology;
import com.lstudio.algorithms.antcolony.island.topology.Topology;
import com.lstudio.algorithms.antcolony.island.topology.TorusTopology;
import com.lstudio.data.TaskReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

/**
 * @author Admin
 */
public class MainForm extends javax.swing.JFrame {

    private File taskFile;
    private IslandOptimization islandOptimization;
    private Visualizer visualizer;

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jVisualizer = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (visualizer != null)
                    visualizer.draw(g);
            }
        };
        jStart = new javax.swing.JButton();
        jStop = new javax.swing.JButton();
        jAlphaTitle = new javax.swing.JLabel();
        jBetaTitle = new javax.swing.JLabel();
        jEvaportionTitle = new javax.swing.JLabel();
        jStagnationTitle = new javax.swing.JLabel();
        jStagnationValue = new javax.swing.JSpinner();
        jRandomFactorTitle = new javax.swing.JLabel();
        jIterationsTitle = new javax.swing.JLabel();
        jIterationsValue = new javax.swing.JSpinner();
        jTopology = new javax.swing.JComboBox<>();
        jIslandTitle = new javax.swing.JLabel();
        jIslandValue = new javax.swing.JSpinner();
        jMigrationTitle = new javax.swing.JLabel();
        jMigrationValue = new javax.swing.JSpinner();
        jAlphaValue = new javax.swing.JTextField();
        jBetaValue = new javax.swing.JTextField();
        jEvaportionValue = new javax.swing.JTextField();
        jRandomFactorValue = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jFile = new javax.swing.JMenu();
        jLoadTask = new javax.swing.JMenuItem();
        jFindParameters = new javax.swing.JMenuItem();
        jHelp = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Optimization of routes of a group of unmanned aircraft systems");

        javax.swing.GroupLayout jVisualizerLayout = new javax.swing.GroupLayout(jVisualizer);
        jVisualizer.setLayout(jVisualizerLayout);
        jVisualizerLayout.setHorizontalGroup(
                jVisualizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 541, Short.MAX_VALUE)
        );
        jVisualizerLayout.setVerticalGroup(
                jVisualizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 390, Short.MAX_VALUE)
        );

        jStart.setText("Start");
        jStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStartActionPerformed(evt);
            }
        });

        jStop.setText("Stop");
        jStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStopActionPerformed(evt);
            }
        });

        jAlphaTitle.setText("Alpha");

        jBetaTitle.setText("Beta");

        jEvaportionTitle.setText("Evaportion rate");

        jStagnationTitle.setText("Stagnation interval");

        jStagnationValue.setModel(new javax.swing.SpinnerNumberModel(1, 0, null, 1));
        jStagnationValue.setToolTipText("");
        jStagnationValue.setValue(1);
        jStagnationValue.setVerifyInputWhenFocusTarget(false);

        jRandomFactorTitle.setText("Random factor");

        jIterationsTitle.setText("Iterations w/o improvements");

        jIterationsValue.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jTopology.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Hypercube topology", "Ring topology", "Torus topology", "Complete topology"}));
        jTopology.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jTopologyItemStateChanged(evt);
            }
        });
        jTopology.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTopologyActionPerformed(evt);
            }
        });

        jIslandTitle.setText("Island count");

        jIslandValue.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jMigrationTitle.setText("Migration interval");

        jMigrationValue.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jAlphaValue.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jBetaValue.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jBetaValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBetaValueActionPerformed(evt);
            }
        });

        jEvaportionValue.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jRandomFactorValue.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jFile.setText("Options");

        jLoadTask.setText("Load task");
        jLoadTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLoadTaskActionPerformed(evt);
            }
        });
        jFile.add(jLoadTask);

        jFindParameters.setText("Find parameters");
        jFindParameters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFindParametersActionPerformed(evt);
            }
        });
        jFile.add(jFindParameters);

        jMenuBar1.add(jFile);

        jHelp.setText("Help");
        jHelp.setToolTipText("");

        jMenuItem1.setText("About");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jHelp.add(jMenuItem1);

        jMenuBar1.add(jHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jVisualizer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jStagnationTitle)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jStagnationValue, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jTopology, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(jIslandTitle)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jIslandValue, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(jMigrationTitle)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jMigrationValue, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(jStart, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jStop, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jEvaportionTitle)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jEvaportionValue, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jBetaTitle)
                                                        .addComponent(jAlphaTitle))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jAlphaValue, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                                                        .addComponent(jBetaValue)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jIterationsTitle)
                                                        .addComponent(jRandomFactorTitle))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jRandomFactorValue)
                                                        .addComponent(jIterationsValue, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jVisualizer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jAlphaTitle)
                                        .addComponent(jAlphaValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jBetaTitle)
                                        .addComponent(jBetaValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jEvaportionTitle)
                                        .addComponent(jEvaportionValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jStagnationTitle)
                                        .addComponent(jStagnationValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jRandomFactorTitle)
                                        .addComponent(jRandomFactorValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jIterationsTitle)
                                        .addComponent(jIterationsValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jTopology, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jIslandTitle)
                                        .addComponent(jIslandValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jMigrationTitle)
                                        .addComponent(jMigrationValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jStart)
                                        .addComponent(jStop))
                                .addContainerGap())
        );

        jTopology.setSelectedIndex(0);

        pack();
    }// </editor-fold>

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jLoadTaskActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileopen = new JFileChooser();
        int ret = fileopen.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            taskFile = fileopen.getSelectedFile();
        }
    }

    private void jFindParametersActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jTopologyActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jTopologyItemStateChanged(java.awt.event.ItemEvent evt) {
        // TODO add your handling code here:
    }

    private void jStartActionPerformed(java.awt.event.ActionEvent evt) {
        TaskReader taskReader = new TaskReader();
        taskReader.readTask(taskFile.getPath());
        System.out.println(
                "Task name: ${taskReader.name} \n" +
                        "City count: ${taskReader.cityCount}\n" +
                        "Vehicle сount: ${taskReader.vehicleCount}"
        );

        double[][] weigths = taskReader.getWeigths();
        int[] startDepots = taskReader.getStartDepots();
        HashMap<Integer, Integer> endDepots = taskReader.getEndDepots();
        IslandOptimization.Companion.setIslandsCount((int) jIslandValue.getValue());
        IslandOptimization.Companion.setCycleIterations((int) jMigrationValue.getValue());
        Topology topology = null;
        switch (jTopology.getSelectedIndex()) {
            case 0: {
                topology = new HypercubeTopology(IslandOptimization.Companion.getIslandsCount());
                break;
            }
            case 1: {
                topology = new RingTopology(IslandOptimization.Companion.getIslandsCount());
                break;
            }
            case 2: {
                topology = new TorusTopology(IslandOptimization.Companion.getIslandsCount());
                break;
            }
        }
        islandOptimization = new IslandOptimization(weigths, startDepots, endDepots, topology);
        islandOptimization.setupAco();
        visualizer = new Visualizer(jVisualizer, islandOptimization.getCities());
        visualizer.show();
        islandOptimization.setVisualizer(visualizer);
        islandOptimization.start();
    }

    private void jStopActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jBetaValueActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JLabel jAlphaTitle;
    private javax.swing.JTextField jAlphaValue;
    private javax.swing.JLabel jBetaTitle;
    private javax.swing.JTextField jBetaValue;
    private javax.swing.JLabel jEvaportionTitle;
    private javax.swing.JTextField jEvaportionValue;
    private javax.swing.JMenu jFile;
    private javax.swing.JMenuItem jFindParameters;
    private javax.swing.JMenu jHelp;
    private javax.swing.JLabel jIslandTitle;
    private javax.swing.JSpinner jIslandValue;
    private javax.swing.JLabel jIterationsTitle;
    private javax.swing.JSpinner jIterationsValue;
    private javax.swing.JMenuItem jLoadTask;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JLabel jMigrationTitle;
    private javax.swing.JSpinner jMigrationValue;
    private javax.swing.JLabel jRandomFactorTitle;
    private javax.swing.JTextField jRandomFactorValue;
    private javax.swing.JLabel jStagnationTitle;
    private javax.swing.JSpinner jStagnationValue;
    private javax.swing.JButton jStart;
    private javax.swing.JButton jStop;
    private javax.swing.JComboBox<String> jTopology;
    private javax.swing.JPanel jVisualizer;
    // End of variables declaration
}
