import com.google.gson.Gson;

import com.google.gson.reflect.*;

import org.w3c.dom.Node;


import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


public class Service {
    String country;
    String city;
    String currency;

    public Service(String country) {
        this.country = country;

    }

    public Service() {
    }

    public String getWeather(String city) {
        this.city = city;
        String key = "94064b99abc150b066f8c52f8dcf5b85";
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric";
        try {
            StringBuilder result = new StringBuilder();
            URL link = new URL(url);
            URLConnection connection = link.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();

            Map<String, Object> respmap = jsonToMap(result.toString());
            Map<String, Object> mainMap = jsonToMap(respmap.get("main").toString());
            Map<String, Object> windMap = jsonToMap(respmap.get("wind").toString());

            String out = "<html>Current temperature: " + mainMap.get("temp") + "<br />"
                    + "Current Humidity: " + mainMap.get("humidity") + "<br />"
                    + "Wind speeds: " + windMap.get("speed") + "<br />"
                    + "Wind angle: " + windMap.get("deg") + "</html>";

            return out;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public Double getRateFor(String currency) {
        this.currency = currency;
        double out = 0.0;
        try {
            StringBuilder result = new StringBuilder();



            String url_str = "https://api.exchangeratesapi.io/latest?base=" + getCurrencyForCountry(country);

            URL url = new URL(url_str);
            URLConnection request = url.openConnection();
            request.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();

            Map<String, Object> respmap = jsonToMap(result.toString());
            Map<String, Object> mainMap = jsonToMap(respmap.get("rates").toString());
            try {
                out = Double.parseDouble(mainMap.get(currency).toString());
            } catch (NullPointerException e) {
                return -1.0;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    public Double getNBPRate() {

        String apiExchangeXML1 = "http://www.nbp.pl/kursy/xml/a058z190322.xml";
        String apiExchangeXML2 = "http://www.nbp.pl/kursy/xml/b012z190320.xml";
        Double out = getRate(apiExchangeXML1);
        if(out != null) {
            return out;
        }
        else {
            out = getRate(apiExchangeXML2);
            if (out != null)
                return out;
            if(out == null && getCurrencyForCountry(country).equals("PLN"))
                return 1.0;

        }



        return  -1.0;

    }



    public static Map<String, Object> jsonToMap(String string) {
        Map<String, Object> map;
        map = new Gson().fromJson(string, new TypeToken<HashMap<String, Object>>(){}.getType());
        return map;
    }



    public static String getCurrencyForCountry(String country) {
        Locale[] allLocales = Locale.getAvailableLocales();
        final List<String> allCountries = new ArrayList<>();
        List<String> allCurrencies = new ArrayList<>();
        for(int i = 0; i < allLocales.length; i++) {
            if (!allLocales[i].getCountry().trim().equals("")) {
                allCountries.add(allLocales[i].getDisplayCountry(new Locale("en_GB")).trim());
                allCurrencies.add(Currency.getInstance(allLocales[i]).getCurrencyCode().trim());

            }

        }
        Map <String, String> countryCurrency = new HashMap<String, String>();

        for (int i = 0; i < allCountries.size(); i++) {
            countryCurrency.put(allCountries.get(i), allCurrencies.get(i));
        }

        return countryCurrency.get(country);
    }

    public Double getRate(String link) {
        Double out;
        try {


            URL url = new URL(link);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(url.openStream());
            NodeList kod_waluty = document.getElementsByTagName("kod_waluty");
            NodeList kurs_sredni = document.getElementsByTagName("kurs_sredni");

            for (int i = 0; i < kod_waluty.getLength(); i++) {

                if (kod_waluty.item(i).getNodeType() == Node.ELEMENT_NODE && kurs_sredni.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    if (kod_waluty.item(i).getTextContent().equals(getCurrencyForCountry(country))) {
                        Element kurs = (Element) kurs_sredni.item(i);
                        out = Double.valueOf(kurs.getTextContent().replace(",", "."));
                        return out;
                    }


                }
            }
        }catch (IOException | ParserConfigurationException | SAXException e){

        }
        return null;
    }


}
