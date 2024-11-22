import com.google.gson.*;
import java.io.FileReader;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        // Debugging: Print the arguments
        System.out.println("Arguments received: " + Arrays.toString(args));
        try {
            if (args.length != 2) {
                System.out.println("Usage: java -jar DestinationHashGenerator.jar <roll_number> <file_path>");
                return;
            }

            String rollNumber = args[0].toLowerCase().replaceAll("\\s+", "");
            String filePath = args[1];

            String destinationValue = getDestinationValue(filePath);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            String randomString = generateRandomString(8);
            String concatenatedString = rollNumber + destinationValue + randomString;
            String hash = generateMD5Hash(concatenatedString);

            System.out.println(hash + ";" + randomString);
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static String getDestinationValue(String filePath) throws Exception {
        JsonElement jsonElement = JsonParser.parseReader(new FileReader(filePath));
        return findDestination(jsonElement);
    }

    private static String findDestination(JsonElement element) {
        if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                if (entry.getKey().equals("destination")) {
                    return entry.getValue().getAsString();
                }
                String result = findDestination(entry.getValue());
                if (result != null) return result;
            }
        } else if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                String result = findDestination(item);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomString.toString();
    }

    private static String generateMD5Hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
