package tokoibuelin.storesystem.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tokoibuelin.storesystem.model.response.ShippingCostResponse;

import java.time.OffsetDateTime;

@Service
public class RajaOngkirService {
    @Autowired
    private RestTemplate restTemplate;

    private static final String API_KEY = "54c907a8d98e1b45d135a47cd2afd5c5";
    private static final  String CITY_URL = "https://api.rajaongkir.com/starter/city";
    private static final String COST_URL = "https://api.rajaongkir.com/starter/cost";
    private static final String[] VALID_COURIERS = {"pos", "tiki", "jne"};


    public ShippingCostResponse getShippingCost(String origin, String destination, int weight, String courier) {
        OffsetDateTime now = OffsetDateTime.now(); // Initialize current date and time
        OffsetDateTime estimatedDeliveryDate = now; // Default estimated delivery date

        System.out.printf("Requesting shipping cost with origin: %s, destination: %s, weight: %d, courier: %s%n",
                origin, destination, weight, courier);

        if (!isValidCourier(courier.trim())) {
            return new ShippingCostResponse("Invalid courier. Valid options are: pos, tiki, jne.", 0, "N/A", estimatedDeliveryDate);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("key", API_KEY);
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        String body = String.format("origin=%s&destination=%s&weight=%d&courier=%s", origin, destination, weight, courier.trim());
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    COST_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            String responseBody = response.getBody();
            System.out.println("API Response: " + responseBody);

            return parseShippingCostResponse(responseBody, courier.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return new ShippingCostResponse("Error calculating shipping cost", 0, "N/A", estimatedDeliveryDate);
        }
    }

    private ShippingCostResponse parseShippingCostResponse(String responseJson, String courier) {
        Gson gson = new Gson();
        JsonObject responseObj = gson.fromJson(responseJson, JsonObject.class);
        JsonArray results = responseObj.getAsJsonObject("rajaongkir").getAsJsonArray("results");

        for (JsonElement courierElem : results) {
            JsonObject courierObj = courierElem.getAsJsonObject();
            String code = courierObj.get("code").getAsString();
            if (code.equalsIgnoreCase(courier)) {
                JsonArray costs = courierObj.getAsJsonArray("costs");
                for (JsonElement costElem : costs) {
                    JsonObject costObj = costElem.getAsJsonObject();
                    String service = costObj.get("service").getAsString();
                    if (service.equalsIgnoreCase("REG")) {
                        JsonArray costDetails = costObj.getAsJsonArray("cost");
                        if (costDetails.size() > 0) {
                            JsonObject detail = costDetails.get(0).getAsJsonObject();
                            int value = detail.get("value").getAsInt();
                            String etd = detail.get("etd").getAsString(); // e.g., "2-3 days"

                            // Parse etd to get min and max days
                            String[] etdParts = etd.split("-");
                            int minDays = Integer.parseInt(etdParts[0].trim());
                            int maxDays = Integer.parseInt(etdParts[1].split(" ")[0].trim());

                            OffsetDateTime now = OffsetDateTime.now();
                            OffsetDateTime estimatedDeliveryDate = now.plusDays(minDays); // For simplicity, take minDays

                            return new ShippingCostResponse(service, value, etd, estimatedDeliveryDate);
                        }
                    }
                }
            }
        }

        // Default response if no matching courier/service found
        OffsetDateTime now = OffsetDateTime.now();
        return new ShippingCostResponse("No data available", 0, "No data available for the given request", now);
    }

    private boolean isValidCourier(String courier) {
        for (String validCourier : VALID_COURIERS) {
            if (validCourier.equalsIgnoreCase(courier)) {
                return true;
            }
        }
        return false;
    }

    public String findCityIdByName(String cityName) {
        String citiesJson = getCities();
        return parseCityIdFromResponse(citiesJson, cityName);
    }

    private String parseCityIdFromResponse(String responseJson, String cityName) {
        // Implementasi parsing JSON dengan Gson
        Gson gson = new Gson();
        JsonObject responseObj = gson.fromJson(responseJson, JsonObject.class);
        JsonArray cities = responseObj.getAsJsonObject("rajaongkir").getAsJsonArray("results");

        for (int i = 0; i < cities.size(); i++) {
            JsonObject city = cities.get(i).getAsJsonObject();
            String name = city.get("city_name").getAsString();
            if (name.equalsIgnoreCase(cityName)) {
                return city.get("city_id").getAsString();
            }
        }
        return null;
    }
    public String getCities() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("key", API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                CITY_URL,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }
}


//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class RajaOngkirService {
//    @Autowired
//    private RestTemplate restTemplate;
//
//    private static final String API_KEY = "54c907a8d98e1b45d135a47cd2afd5c5";
//    private static final String CITY_URL = "https://api.rajaongkir.com/starter/city";
//    private static final String COST_URL = "https://api.rajaongkir.com/starter/cost";
//    private static final String[] VALID_COURIERS = {"pos", "tiki", "jne"};
//
//    public String getCities() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("key", API_KEY);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                CITY_URL,
//                HttpMethod.GET,
//                entity,
//                String.class
//        );
//
//        return response.getBody();
//    }
//
//    public String getShippingCost(String origin, String destination, int weight, String courier) {
//        // Debugging: Print the courier value
//        System.out.println("Courier value: " + courier);
//
//        // Validasi courier dengan trim
//        if (!isValidCourier(courier.trim())) {
//            return "Invalid courier. Valid options are: pos, tiki, jne.";
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("key", API_KEY);
//        headers.set("Content-Type", "application/x-www-form-urlencoded");
//
//        String body = String.format("origin=%s&destination=%s&weight=%d&courier=%s", origin, destination, weight, courier.trim());
//        HttpEntity<String> entity = new HttpEntity<>(body, headers);
//
//        try {
//            System.out.println("Requesting shipping cost with body: " + body);
//            ResponseEntity<String> response = restTemplate.exchange(
//                    COST_URL,
//                    HttpMethod.POST,
//                    entity,
//                    String.class
//            );
//            String responseBody = response.getBody();
//            System.out.println("Response from RajaOngkir: " + responseBody);
//            return responseBody;
//        } catch (Exception e) {
//            // Log error dan berikan pesan kesalahan yang lebih informatif
//            System.err.println("Error occurred: " + e.getMessage());
//            e.printStackTrace();
//            return "Error calculating shipping cost";
//        }
//    }
//
//    private boolean isValidCourier(String courier) {
//        for (String validCourier : VALID_COURIERS) {
//            // Debugging: Print valid courier for comparison
//            System.out.println("Valid courier for comparison: " + validCourier);
//            if (validCourier.equalsIgnoreCase(courier)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public String findCityIdByName(String cityName) {
//        String citiesJson = getCities();
//        return parseCityIdFromResponse(citiesJson, cityName);
//    }
//
//    private String parseCityIdFromResponse(String responseJson, String cityName) {
//        // Implementasi parsing JSON dengan Gson
//        Gson gson = new Gson();
//        JsonObject responseObj = gson.fromJson(responseJson, JsonObject.class);
//        JsonArray cities = responseObj.getAsJsonObject("rajaongkir").getAsJsonArray("results");
//
//        for (int i = 0; i < cities.size(); i++) {
//            JsonObject city = cities.get(i).getAsJsonObject();
//            String name = city.get("city_name").getAsString();
//            if (name.equalsIgnoreCase(cityName)) {
//                return city.get("city_id").getAsString();
//            }
//        }
//        return null;
//    }
//}
