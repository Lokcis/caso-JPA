/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.models.Competitor;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *
 * @author lokci
 */
@Path("/auth")
public class AuthService {

    @PersistenceUnit(unitName = "competitorPU")
    private EntityManagerFactory emf;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Competitor login(@FormParam("email") String email, @FormParam("password") String password) {

        EntityManager em = emf.createEntityManager();

        try {
            // Buscar al competidor por su email
            Query query = em.createQuery("SELECT c FROM Competitor c WHERE c.email = :email");
            query.setParameter("email", email);

            List<Competitor> results = query.getResultList();

            if (results.isEmpty()) {
                // No existe un usuario con ese email
                throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("Contraseña incorrecta").build());

            }

            Competitor competitor = results.get(0);

            if (!competitor.getPassword().equals(password)) {
                // Contraseña incorrecta
                throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("Contraseña incorrecta").build());

            }

            // Todo correcto, se devuelve el competidor
            return competitor;

        } finally {
            em.close();
        }
    }
}
