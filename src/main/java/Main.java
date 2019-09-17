import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.List;


public class Main {
    static String pickedCurrency;
    static String pickedCountry;

    final public static Map <String, String> countryCurrency = new HashMap<String, String>();
    public static void main(String[] args) {
        Service s = new Service("Poland");
        String weatherJson = s.getWeather("Warsaw");
        Double rate1 = s.getRateFor("USD");
        Double rate2 = s.getNBPRate();

        System.out.println("weatherJson: " + '\n' + weatherJson + '\n');
        System.out.println("rate1: " + rate1 + '\n');
        System.out.println("rate2: " + rate2);
        launchGui();

    }

    public static void launchGui() {
        JFrame jFrame = new JFrame();
        jFrame.setLayout(new BorderLayout());

        JPanel jPanelNorth = new JPanel();
        jPanelNorth.setLayout(new GridLayout(1, 3));
        jFrame.add(jPanelNorth, BorderLayout.NORTH);

        JPanel jPanelCenter = new JPanel();
        jPanelCenter.setLayout(new GridLayout(2, 2));
        jFrame.add(jPanelCenter, BorderLayout.CENTER);

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setBounds(600, 400, 600, 400);

        Locale[] allLocales = Locale.getAvailableLocales();
        final List<String> allCountries = new ArrayList<>();
        List<String> allCurrencies = new ArrayList<>();
        for(int i = 0; i < allLocales.length; i++) {
            if (!allLocales[i].getCountry().trim().equals("")) {
                allCountries.add(allLocales[i].getDisplayCountry(new Locale("en_GB")).trim());
                allCurrencies.add(Currency.getInstance(allLocales[i]).getCurrencyCode().trim());

            }

        }



        for (int i = 0; i < allCountries.size(); i++) {
            countryCurrency.put(allCountries.get(i), allCurrencies.get(i));
        }
        Collections.sort(allCountries);



        JComboBox countries = new JComboBox(allCountries.toArray());
        Set unikCurr = new HashSet();
        unikCurr.addAll(allCurrencies);
        JComboBox currencies = new JComboBox(unikCurr.toArray());


        countries.setSize(60, 30);
        JTextField cityChoose = new JTextField("Choose the city");
        cityChoose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cityChoose.selectAll();
            }
        });


        jPanelNorth.add(countries);
        jPanelNorth.add(currencies);
        jPanelNorth.add(cityChoose);
        pickedCountry = "Albania";
        pickedCurrency = "HRK";

        countries.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                pickedCountry = (String)e.getItem();
                pickedCurrency = countryCurrency.get(e.getItem()) + e.getStateChange();
                if(pickedCurrency.contains("1")){
                    pickedCurrency = Arrays.toString(pickedCurrency.split("1")).substring(1, 4);
                }

            }
        });

        currencies.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                pickedCurrency = (String)e.getItem();

            }
        });




        JButton jb1 = new JButton("Weather for chosen city");
        jb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog jDialog = new JDialog(jFrame,"Weather for " + cityChoose.getText());
                jDialog.setResizable(false);
                jDialog.setBounds(400, 300, 400, 300);

                Service forWeather = new Service();
                String weather = forWeather.getWeather(cityChoose.getText());

                JLabel info = new JLabel();
                info.setVerticalAlignment(SwingUtilities.CENTER);
                info.setHorizontalAlignment(SwingUtilities.CENTER);
                info.setText(weather);
                jDialog.add(info);
                jDialog.setVisible(true);
            }
        });

        JButton jb2 = new JButton("Exchange rate for chosen currencies");
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog jDialog = new JDialog(jFrame,"Exchange rate");
                jDialog.setResizable(false);
                jDialog.setBounds(400, 300, 400, 300);

                Service forExRate = new Service(pickedCountry);
                Double rate1 = forExRate.getRateFor(pickedCurrency);


                JLabel info = new JLabel();
                info.setVerticalAlignment(SwingUtilities.CENTER);
                info.setHorizontalAlignment(SwingUtilities.CENTER);
                info.setText("<html>Exchange rate " + countryCurrency.get(pickedCountry) + " to " + pickedCurrency + "<br />" + rate1);
                jDialog.add(info);
                jDialog.setVisible(true);
            }
        });

        JButton jb3 = new JButton("NBP rate");
        jb3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                JDialog jDialog = new JDialog(jFrame,"NBP rate for " + countryCurrency.get(pickedCountry));
                jDialog.setResizable(false);
                jDialog.setBounds(400, 300, 400, 300);

                Service forNBP = new Service(pickedCountry);
                Double rate2 = forNBP.getNBPRate();

                JLabel info = new JLabel();
                info.setVerticalAlignment(SwingUtilities.CENTER);
                info.setHorizontalAlignment(SwingUtilities.CENTER);
                info.setText("<html>NBP rate for " + countryCurrency.get(pickedCountry) + "<br />"+rate2+"</html>");
                jDialog.add(info);
                jDialog.setVisible(true);
            }
        });

        JButton jb4 = new JButton("Info about chosen city");
        jb4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame jf = new JFrame("Info about " + cityChoose.getText());
                JFXPanel jfxPanel = new JFXPanel();
                jf.add(jfxPanel);

                Platform.runLater(() -> {
                    WebView webView = new WebView();
                    jfxPanel.setScene(new Scene(webView));
                    webView.getEngine().load("https://en.wikipedia.org/wiki/" + cityChoose.getText());
                });
//                jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                jf.setBounds(900, 600, 900, 600);
                jf.setVisible(true);
            }
        });

        jPanelCenter.add(jb1);
        jPanelCenter.add(jb2);
        jPanelCenter.add(jb3);
        jPanelCenter.add(jb4);


        jFrame.setVisible(true);
    }
}
