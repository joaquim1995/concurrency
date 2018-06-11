import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

import javax.swing.*;

public class BarChart extends JFrame {

    ArrayList<Csv> dados = new ArrayList<>();
    Boolean lei;
    public BarChart(String palavra, String csvFile,Boolean lei) {

      this.lei = lei;
        try {
                String line = "";
                String cvsSplitBy = ",";

                BufferedReader br = new BufferedReader(new FileReader(csvFile));
                br.readLine();
                while ((line = br.readLine()) != null) {

                    // use comma as separator
                    String[] aux = line.split(cvsSplitBy);
                    if (aux[0].equalsIgnoreCase(palavra))
                        dados.add(new Csv(aux[0], Long.parseLong(aux[1]), Integer.parseInt(aux[2]), Double.parseDouble(aux[3])));
                }
            } catch(Exception e){
                e.printStackTrace();
            }

        initUI();
    }

    private void initUI() {

        CategoryDataset dataset = createDataset();

        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        add(chartPanel);

        pack();
        setTitle("Bar chart");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private CategoryDataset createDataset() {


        if (lei) {

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            List<Integer> threads = dados.stream().filter(distinctByKey(Csv::getThreds)).map(Csv::getThreds).collect(Collectors.toList());

            threads.forEach(item->
            {
                dataset.setValue(dados.stream().filter(y->item.equals(y.getThreds())).map(Csv::getSpeedUp).mapToDouble(Double::doubleValue).average().getAsDouble(), "Alfa", item);
            });


            return dataset;
        }

        else
        {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            List<Integer> threads = dados.stream().filter(distinctByKey(Csv::getThreds)).map(Csv::getThreds).collect(Collectors.toList());

            threads.forEach(item->
            {
                dataset.setValue(dados.stream().filter(y->item.equals(y.getThreds())).map(Csv::getNanoTime).mapToLong(Long::longValue).average().getAsDouble()/1E9, "Tempo (s)", item);
            });


            return dataset;

        }




    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private JFreeChart createChart(CategoryDataset dataset) {

        if (lei)
        {
            JFreeChart barChart = ChartFactory.createBarChart(
                    "TP",
                    "Threads",
                    "Alfa",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false, true, false);

            return barChart;
        }
        else
        {
            JFreeChart barChart = ChartFactory.createBarChart(
                    "TP",
                    "Threads",
                    "Tempo (s)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false, true, false);

            return barChart;
        }

    }
}