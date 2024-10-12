import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CurrencyConverter {

    // URL base para la API
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/a42af3089cce726d26e317cc/latest/USD";

    // Lista de monedas disponibles
    private static final String[] CURRENCIES = {"USD", "EUR", "GBP", "JPY", "CAD", "AUD", "MXN", "BRL"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Mostrar lista de monedas y permitir que el usuario seleccione
        System.out.println("Hola bienvenido a el conversor de divisas, seleccione la moneda de origen:");
        String fromCurrency = selectCurrency(scanner);

        System.out.println("Seleccione la moneda de destino:");
        String toCurrency = selectCurrency(scanner);

        // Preguntar el monto que desea convertir
        System.out.println("Ingrese el monto que desea convertir: ");
        double amount = scanner.nextDouble();

        // Obtener el tipo de cambio y hacer la conversión
        try {
            double exchangeRate = getExchangeRate(fromCurrency, toCurrency);
            if (exchangeRate != 0) {
                double convertedAmount = amount * exchangeRate;
                System.out.println(amount + " " + fromCurrency + " es igual a " + convertedAmount + " " + toCurrency);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener el tipo de cambio: " + e.getMessage());
        }
    }

    // Método para mostrar la lista de monedas y permitir la selección
    private static String selectCurrency(Scanner scanner) {
        for (int i = 0; i < CURRENCIES.length; i++) {
            System.out.println((i + 1) + ". " + CURRENCIES[i]);
        }
        System.out.print("Seleccione el número de la moneda: ");
        int choice = scanner.nextInt();

        // Validar la elección
        while (choice < 1 || choice > CURRENCIES.length) {
            System.out.print("Selección inválida. Inténtelo de nuevo: ");
            choice = scanner.nextInt();
        }

        return CURRENCIES[choice - 1];
    }

    // Método para obtener el tipo de cambio
    private static double getExchangeRate(String fromCurrency, String toCurrency) throws Exception {
        // Construir la URL de la API
        String apiUrlWithCurrency = API_URL.replace("USD", fromCurrency);

        // Crear conexión HTTP
        URL url = new URL(apiUrlWithCurrency);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Leer la respuesta de la API
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
        reader.close();

        // Verificar si la respuesta es exitosa
        if (jsonResponse.get("result").getAsString().equals("success")) {
            // Obtener las tasas de cambio
            JsonObject conversionRates = jsonResponse.getAsJsonObject("conversion_rates");

            // Obtener el tipo de cambio entre las dos monedas
            return conversionRates.get(toCurrency).getAsDouble();
        } else {
            throw new Exception("No se pudo obtener el tipo de cambio.");
        }
    }
}
