package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Competitor;
import com.example.models.CompetitorDTO;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

@Path("/competitors")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitorService {

    private EntityManager em;

    @PostConstruct
    public void init() {
        try {
            em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("/get")
    public Response getAll() {
        Query query = em.createQuery("SELECT c FROM Competitor c ORDER BY c.surname ASC");
        List<Competitor> competitors = query.getResultList();

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .entity(competitors)
                .build();
    }

    @POST
    @Path("/add")
    public Response createCompetitor(CompetitorDTO dto) {
        JSONObject rta = new JSONObject();
        Competitor c = new Competitor(
                dto.getName(),
                dto.getSurname(),
                dto.getAge(),
                dto.getTelephone(),
                dto.getCellphone(),
                dto.getAddress(),
                dto.getCity(),
                dto.getCountry(),
                false
        );

        c.setEmail(dto.getEmail());
        c.setPassword(dto.getPassword());

        try {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
            em.refresh(c);
            rta.put("competitorId", c.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return Response.status(500).entity("Error al guardar competidor").build();
        }

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .entity(rta)
                .build();
    }
}
