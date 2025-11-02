package change;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

@ApplicationScoped
public class ChangeService {
    
    private static final String FICHIER_CONFIG = "/data.json";
    private JSONArray devisesConfig;
    
    public ChangeService() {
        chargerConfiguration();
    }
    
    private void chargerConfiguration() {
        try (InputStream is = getClass().getResourceAsStream(FICHIER_CONFIG);
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            
            Scanner scanner = new Scanner(reader).useDelimiter("\\A");
            String jsonContent = scanner.hasNext() ? scanner.next() : "[]";
            
            devisesConfig = new JSONArray(jsonContent);
            System.out.println("✅ Configuration devises chargée: " + devisesConfig.length() + " devises");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement configuration: " + e.getMessage());
            devisesConfig = new JSONArray(); // Tableau vide en cas d'erreur
        }
    }
    
    public Double convertirVersAriary(String devise, Double montant) throws Exception {
        if (!estDeviseValide(devise)) {
            throw new Exception("Devise non valide ou période expirée: " + devise);
        }
        
        Double taux = getTauxChange(devise);
        return montant * taux;
    }
    
    public List<String> getDevisesDisponibles() {
        List<String> devises = new ArrayList<>();
        
        for (int i = 0; i < devisesConfig.length(); i++) {
            JSONObject deviseObj = devisesConfig.getJSONObject(i);
            if (estDeviseValide(deviseObj)) {
                devises.add(deviseObj.getString("code"));
            }
        }
        
        return devises;
    }
    
    public Double getTauxChange(String devise) throws Exception {
        for (int i = 0; i < devisesConfig.length(); i++) {
            JSONObject deviseObj = devisesConfig.getJSONObject(i);
            if (deviseObj.getString("code").equalsIgnoreCase(devise) && estDeviseValide(deviseObj)) {
                return deviseObj.getDouble("taux");
            }
        }
        
        throw new Exception("Taux non trouvé pour la devise: " + devise);
    }
    
    public boolean estDeviseValide(String devise) {
        for (int i = 0; i < devisesConfig.length(); i++) {
            JSONObject deviseObj = devisesConfig.getJSONObject(i);
            if (deviseObj.getString("code").equalsIgnoreCase(devise)) {
                return estDeviseValide(deviseObj);
            }
        }
        return false;
    }
    
    private boolean estDeviseValide(JSONObject deviseObj) {
        try {
            LocalDate aujourdhui = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            String dateDebutStr = deviseObj.getString("dateDebut");
            String dateFinStr = deviseObj.getString("dateFin");
            
            LocalDate dateDebut = LocalDate.parse(dateDebutStr, formatter);
            LocalDate dateFin = LocalDate.parse(dateFinStr, formatter);
            
            return !aujourdhui.isBefore(dateDebut) && !aujourdhui.isAfter(dateFin);
            
        } catch (Exception e) {
            System.err.println("Erreur validation période devise: " + e.getMessage());
            return false;
        }
    }
    
    public Map<String, Map<String, Object>> getInfoDevises() {
        Map<String, Map<String, Object>> infos = new HashMap<>();
        
        for (int i = 0; i < devisesConfig.length(); i++) {
            JSONObject deviseObj = devisesConfig.getJSONObject(i);
            String code = deviseObj.getString("code");
            
            Map<String, Object> info = new HashMap<>();
            info.put("taux", deviseObj.getDouble("taux"));
            info.put("nom", deviseObj.getString("nom"));
            info.put("dateDebut", deviseObj.getString("dateDebut"));
            info.put("dateFin", deviseObj.getString("dateFin"));
            info.put("valide", estDeviseValide(deviseObj));
            
            infos.put(code, info);
        }
        
        return infos;
    }
}