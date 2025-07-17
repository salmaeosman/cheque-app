
import com.sun.net.httpserver.HttpExchange;

import com.sun.net.httpserver.HttpServer;



import java.io.*;

import java.net.*;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;

import java.util.*;



public class LocalScanServer {



// Chemin vers l'exécutable NAPS2

private static final String NAPS2_CONSOLE_PATH =

"C:\\Program Files\\NAPS2\\naps2.console.exe";



// URL de ton endpoint Spring Boot qui reçoit le scan (port 8103)

private static final String UPLOAD_URL =

"http://localhost:8103/api/scan/upload";



public static void main(String[] args) throws IOException {

HttpServer server = HttpServer.create(new InetSocketAddress(8104), 0);

server.createContext("/scan/lancer-scan", LocalScanServer::handleScanRequest);

server.setExecutor(null);

System.out.println("➡ Serveur de scan lancé sur http://localhost:8104");

server.start();

}



private static void handleScanRequest(HttpExchange exchange) throws IOException {

try {

// 1) Méthode autorisée

if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {

sendJson(exchange, 405,

"{\"success\":false,\"message\":\"Method Not Allowed\"}");

return;

}



// 2) Récupération du chequeId

String query = exchange.getRequestURI().getQuery();

Map<String,String> params = queryToMap(query);

String chequeId = params.get("chequeId");

if (chequeId == null || chequeId.isBlank()) {

sendJson(exchange, 400,

"{\"success\":false,\"message\":\"ID du chèque manquant\"}");

return;

}



// 3) Préparation du fichier de sortie

File output = new File("scan_" + chequeId + ".jpg");



// 4) Vérification de l'exécutable NAPS2

File exe = new File(NAPS2_CONSOLE_PATH);

if (!exe.exists()) {

sendJson(exchange, 500,

"{\"success\":false,\"message\":"

+ "\"NAPS2 introuvable: " + NAPS2_CONSOLE_PATH + "\"}");

return;

}



// 5) Lancement du scan

ProcessBuilder pb = new ProcessBuilder(

NAPS2_CONSOLE_PATH,

"-o", output.getAbsolutePath(),

"-f", "jpg"

);

pb.redirectErrorStream(true);

System.out.println("🔧 Exécution de : " + String.join(" ", pb.command()));

Process p = pb.start();



// 6) Log du process

try (BufferedReader r = new BufferedReader(

new InputStreamReader(p.getInputStream()))) {

r.lines().forEach(l -> System.out.println("[NAPS2] " + l));

}

int exit = p.waitFor();

if (exit != 0 || !output.exists()) {

sendJson(exchange, 500,

"{\"success\":false,\"message\":"

+ "\"Echec du scan (code " + exit + ")\"}");

return;

}



// 7) Upload vers Spring Boot

if (!uploadToServer(output, chequeId)) {

sendJson(exchange, 500,

"{\"success\":false,\"message\":\"Echec de l'upload\"}");

return;

}



// 8) Réponse OK

sendJson(exchange, 200, "{\"success\":true}");

} catch (Exception e) {

e.printStackTrace();

sendJson(exchange, 500,

"{\"success\":false,\"message\":\"Erreur interne\"}");

}

}



private static boolean uploadToServer(File file, String chequeId) throws IOException {

String boundary = "----Boundary" + System.currentTimeMillis();

URL url = new URL(UPLOAD_URL);

HttpURLConnection conn = (HttpURLConnection) url.openConnection();

conn.setDoOutput(true);

conn.setRequestMethod("POST");

conn.setRequestProperty(

"Content-Type",

"multipart/form-data; boundary=" + boundary

);



try (

OutputStream out = conn.getOutputStream();

PrintWriter writer = new PrintWriter(

new OutputStreamWriter(out, StandardCharsets.UTF_8), true

)

) {

String CRLF = "\r\n";



// -- chequeId

writer.append("--").append(boundary).append(CRLF);

writer.append(

"Content-Disposition: form-data; name=\"chequeId\""

).append(CRLF).append(CRLF);

writer.append(chequeId).append(CRLF).flush();



// -- fichier

writer.append("--").append(boundary).append(CRLF);

writer.append(

"Content-Disposition: form-data; name=\"file\"; filename=\""

+ file.getName() + "\""

).append(CRLF);

writer.append("Content-Type: image/jpeg").append(CRLF).append(CRLF);

writer.flush();



// copie bytes

Files.copy(file.toPath(), out);

out.flush();

writer.append(CRLF).flush();



// -- fin de multipart

writer.append("--").append(boundary).append("--").append(CRLF).flush();

}



int code = conn.getResponseCode();

System.out.println("📤 Upload HTTP code: " + code);

return code >= 200 && code < 300;

}



private static void sendJson(

HttpExchange exchange, int status, String json

) throws IOException {

exchange.getResponseHeaders().set(

"Content-Type", "application/json; charset=UTF-8"

);

byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

exchange.sendResponseHeaders(status, bytes.length);

try (OutputStream os = exchange.getResponseBody()) {

os.write(bytes);

}

}



private static Map<String,String> queryToMap(String query) {

Map<String,String> map = new HashMap<>();

if (query == null || query.isBlank()) return map;

for (String part : query.split("&")) {

String[] kv = part.split("=", 2);

String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);

String val = kv.length>1

? URLDecoder.decode(kv[1], StandardCharsets.UTF_8)

: "";

map.put(key, val);

}

return map;

}

}