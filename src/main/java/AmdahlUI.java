import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.*;

public final class AmdahlUI {

    private JFrame frame = new JFrame("Amdahl Calculator");
    private JPanel background;
    private Container pane;

    private int numberOfCPU;
    private double sequentialPercentage;

    private double speedUpFactor;
    private double efficiencyFactor;


    public AmdahlUI(int numberOfCPU, double sequentialPercentage) {
        this.numberOfCPU = numberOfCPU;
        this.sequentialPercentage = sequentialPercentage;
        performAmdahlCalculations();
        showCalculationsInUI();
    }

    /**
     * @return void
     * @throws IllegalArgumentException when giving invalid arguments to calculations
     */
    private void performAmdahlCalculations() throws IllegalArgumentException {
        speedUpFactor 		= Amdahl.calculateSpeedUpFactor(numberOfCPU, sequentialPercentage);
        efficiencyFactor 	= Amdahl.calculateEfficiencyFactor(speedUpFactor, numberOfCPU);
    }

    private void showCalculationsInUI() {
        setUpJFrame();
        drawCalculationsToFrame();
        showFrame();
    }

    private void setUpJFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setPreferredSize(new Dimension(300, 220));
        frame.setResizable(false);

        background = new JPanel();
        background.setLayout(null);
        frame.setContentPane(background);
    }

    private void drawCalculationsToFrame() {
        pane = frame.getContentPane();

        drawSpeedUpFactorToPane(pane);
        drawEfficiencyFactorToPane(pane);
        drawCreatorNamesToPane(pane);

        drawTextFieldToPane();
    }

    private void drawTextFieldToPane() {
        final JTextField textField = new JTextField("" + numberOfCPU + "");
        textField.setBounds(0, 200, 100, 30);
        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                try
                {
                    numberOfCPU = Integer.parseInt( textField.getText() );
                    performAmdahlCalculations();
                }
                catch( Exception ex )
                {
                    numberOfCPU = 1;
                }

                showCalculationsInUI();
            }
        });

        pane.add(textField);
        textField.requestFocus();
        textField.setCaretPosition(textField.getText().length()-1);
    }

    private void drawSpeedUpFactorToPane( Container pane ) {
        drawLabelToPane(pane,
                "Speedup Factor: (" + sequentialPercentage + "% seq. " + numberOfCPU + " CPU's)",
                10, 5, 300, 20,
                SwingConstants.LEFT);

        drawLabelToPane(pane,
                "" + roundFourDecimals(speedUpFactor) + " =",
                10, 37, 80, 20,
                SwingConstants.RIGHT);

        drawLabelToPane(pane,
                "" + numberOfCPU + "",
                60, 29, 230, 20,
                SwingConstants.CENTER);

        drawLabelToPane(pane,
                "______________________",
                60, 32, 230, 20,
                SwingConstants.CENTER);

        drawLabelToPane(pane,
                "1 + " + sequentialPercentage + " * ( " + numberOfCPU + " - 1 )",
                60, 48, 230, 20,
                SwingConstants.CENTER);
    }

    private void drawEfficiencyFactorToPane( Container pane ) {
        drawLabelToPane(pane,
                "Efficiency Factor:",
                10, 95, 300, 20,
                SwingConstants.LEFT);

        drawLabelToPane(pane,
                "" + roundFourDecimals(efficiencyFactor) + " =",
                10, 127, 80, 20,
                SwingConstants.RIGHT);

        drawLabelToPane(pane,
                "" + roundFourDecimals(speedUpFactor) + "",
                60, 119, 135, 20,
                SwingConstants.CENTER);

        drawLabelToPane(pane,
                "_________",
                60, 122, 135, 20,
                SwingConstants.CENTER);

        drawLabelToPane(pane,
                "" + numberOfCPU + "",
                60, 138, 135, 20,
                SwingConstants.CENTER);
    }

    private void drawCreatorNamesToPane( Container pane ) {
        JLabel creatorNamesLabel = new JLabel("by M. Diepenbroek and W. Konecny");
        creatorNamesLabel.setForeground(Color.gray);
        creatorNamesLabel.setBounds(0, 175, 292, 20);
        creatorNamesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        pane.add(creatorNamesLabel);
    }

    private void showFrame() {
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    private void drawLabelToPane( Container pane, final String label, final int x, final int y, final int width, final int height, final int textAlignment ) {
        JLabel newLabel = new JLabel(label);
        newLabel.setHorizontalAlignment(textAlignment);
        newLabel.setBounds(x, y, width, height);
        pane.add(newLabel);
    }

    private double roundFourDecimals(final double d) {
        DecimalFormat newNumberFormat = new DecimalFormat("#.####");
        return Double.valueOf(newNumberFormat.format(d).replace("$","."));
    }
}