import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LocalScanServer {

    // Chemin complet vers naps2.console.exe — À ADAPTER SELON VOTRE INSTALLATION
    private static final String NAPS2_CONSOLE_PATH = "C:\\Program Files\\NAPS2\\naps2.console.exe";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8104), 0);
        server.createContext("/scan/lancer-scan", LocalScanServer::handleScanRequest);
        server.setExecutor(null);
        System.out.println("Serveur de scan lancé sur http://localhost:8104");
        server.start();
    }

    private static void handleScanRequest(HttpExchange exchange) {
        try {
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            String chequeId = params.get("chequeId");

            if (chequeId == null || chequeId.isEmpty()) {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"ID du chèque manquant\"}");
                return;
            }

            File outputFile = new File("scan_" + chequeId + ".jpg");

            // Vérification si l'exécutable existe
            File naps2Executable = new File(NAPS2_CONSOLE_PATH);
            if (!naps2Executable.exists()) {
                sendJson(exchange, 500, "{\"success\":false,\"message\":\"NAPS2 n'est pas installé ou le chemin est incorrect: " + NAPS2_CONSOLE_PATH.replace("\\", "\\\\") + "\"}");
                return;
            }

            ProcessBuilder pb = new ProcessBuilder(
                NAPS2_CONSOLE_PATH,
                "-o", outputFile.getAbsolutePath(),
                "-f", "jpg"
            );

            pb.redirectErrorStream(true); // redirige la sortie d'erreur vers la sortie standard
            Process process = pb.start();

            // Afficher les logs du processus pour debug
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[NAPS2] " + line);
                }
            }

            int result = process.waitFor();

            if (result != 0 || !outputFile.exists()) {
                sendJson(exchange, 500, "{\"success\":false,\"message\":\"Échec du scan\"}");
                return;
            }

            boolean uploaded = uploadToServer(outputFile, chequeId);
            if (!uploaded) {
                sendJson(exchange, 500, "{\"success\":false,\"message\":\"Échec de l'upload\"}");
                return;
            }

            sendJson(exchange, 200, "{\"success\":true}");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                sendJson(exchange, 500, "{\"success\":false,\"message\":\"Erreur interne\"}");
            } catch (IOException ignored) {}
        }
    }

    private static boolean uploadToServer(File file, String chequeId) throws IOException {
        String boundary = Long.toHexString(System.currentTimeMillis());
        URL url = new URL("http://localhost:8104/api/scan/upload");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
            OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true)
        ) {
            String CRLF = "\r\n";

            // Paramètre chequeId
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"chequeId\"").append(CRLF);
            writer.append(CRLF).append(chequeId).append(CRLF).flush();

            // Fichier image
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                .append(file.getName()).append("\"").append(CRLF);
            writer.append("Content-Type: image/jpeg").append(CRLF);
            writer.append(CRLF).flush();

            FileInputStream input = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) != -1) output.write(buffer, 0, len);
            output.flush();
            input.close();

            writer.append(CRLF).flush();
            writer.append("--").append(boundary).append("--").append(CRLF).flush();
        }

        return connection.getResponseCode() == 200;
    }

    private static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                map.put(
                    URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                );
            }
        }
        return map;
    }
}