package change;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/change")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChangeResource {
    
    @Inject
    private ChangeService changeService;
    
    @GET
    @Path("/devises")
    public Response getDevisesDisponibles() {
        try {
            List<String> devises = changeService.getDevisesDisponibles();
            return Response.ok(devises).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erreur", e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/taux/{devise}")
    public Response getTauxChange(@PathParam("devise") String devise) {
        try {
            Double taux = changeService.getTauxChange(devise);
            return Response.ok(Map.of("devise", devise, "taux", taux)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("erreur", e.getMessage()))
                    .build();
        }
    }
    
    @POST
    @Path("/convertir")
    public Response convertirVersAriary(ConversionRequest request) {
        try {
            if (request.getDevise() == null || request.getMontant() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Devise et montant sont requis")
                        .build();
            }
            
            Double resultat = changeService.convertirVersAriary(request.getDevise(), request.getMontant());
            
            // Retourne directement le montant
            return Response.ok(resultat).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    @GET
    @Path("/devises/infos")
    public Response getInfoDevises() {
        try {
            Map<String, Map<String, Object>> infos = changeService.getInfoDevises();
            return Response.ok(infos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erreur", e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/devises/{devise}/valide")
    public Response estDeviseValide(@PathParam("devise") String devise) {
        try {
            boolean valide = changeService.estDeviseValide(devise);
            return Response.ok(Map.of("devise", devise, "valide", valide)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erreur", e.getMessage()))
                    .build();
        }
    }
}