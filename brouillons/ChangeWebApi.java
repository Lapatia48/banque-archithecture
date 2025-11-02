import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import change.ChangeRemote;

public class ChangeWebApi {
    
    private ChangeRemote getChangeEjb() throws Exception {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
        props.put(Context.SECURITY_PRINCIPAL, "admin");
        props.put(Context.SECURITY_CREDENTIALS, "admin123");
        props.put("jboss.naming.client.ejb.context", true);
        props.put("org.jboss.ejb.client.scoped.context", true);
        
        Context context = new InitialContext(props);
        return (ChangeRemote) context.lookup("ejb:/change-ejb-1.0.0/ChangeBean!change.ChangeRemote");
    }

    public static void main(String[] args) throws IOException {
        // Création du serveur HTTP sur le port 8082
        HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
        
        // Endpoint /devises
        server.createContext("/devises", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("GET".equals(exchange.getRequestMethod())) {
                    try {
                        ChangeWebApi api = new ChangeWebApi();
                        ChangeRemote ejb = api.getChangeEjb();
                        List<String> devises = ejb.getDevisesDisponibles();
                        
                        String response = devises.toString();
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                        
                        System.out.println("✅ GET /devises -> " + response);
                    } catch (Exception e) {
                        String error = "❌ Erreur: " + e.getMessage();
                        exchange.sendResponseHeaders(500, error.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(error.getBytes());
                        os.close();
                    }
                }
            }
        });
        
        // Endpoint /convertir
        server.createContext("/convertir", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("GET".equals(exchange.getRequestMethod())) {
                    try {
                        String query = exchange.getRequestURI().getQuery();
                        String devise = getParam(query, "devise");
                        String montantStr = getParam(query, "montant");
                        
                        if (devise == null || montantStr == null) {
                            String error = "Paramètres manquants: devise et montant requis";
                            exchange.sendResponseHeaders(400, error.getBytes().length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(error.getBytes());
                            os.close();
                            return;
                        }
                        
                        Double montant = Double.parseDouble(montantStr);
                        ChangeWebApi api = new ChangeWebApi();
                        ChangeRemote ejb = api.getChangeEjb();
                        Double result = ejb.convertirVersAriary(devise, montant);
                        
                        String response = result.toString();
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                        
                        System.out.println("✅ GET /convertir?devise=" + devise + "&montant=" + montant + " -> " + response);
                    } catch (Exception e) {
                        String error = "❌ Erreur: " + e.getMessage();
                        exchange.sendResponseHeaders(500, error.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(error.getBytes());
                        os.close();
                    }
                }
            }
        });
        
        // Endpoint /test
        server.createContext("/test", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("GET".equals(exchange.getRequestMethod())) {
                    try {
                        ChangeWebApi api = new ChangeWebApi();
                        ChangeRemote ejb = api.getChangeEjb();
                        List<String> devises = ejb.getDevisesDisponibles();
                        
                        String response = "✅ API Change Docker FONCTIONNE! Devises: " + devises;
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                        
                        System.out.println("✅ GET /test -> " + response);
                    } catch (Exception e) {
                        String error = "❌ Erreur: " + e.getMessage();
                        exchange.sendResponseHeaders(500, error.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(error.getBytes());
                        os.close();
                    }
                }
            }
        });
        
        server.setExecutor(null);
        server.start();
    }
    
    private static String getParam(String query, String paramName) {
        if (query == null) return null;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                return keyValue[1];
            }
        }
        return null;
    }
}