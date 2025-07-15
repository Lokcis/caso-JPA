/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Competitor;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import javax.annotation.PostConstruct;

/**
 *
 * @author lokci
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthService {

    private EntityManager em;

    @PostConstruct
    public void init() {
        try {
            em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @POST
    @Path("/login")
    public Response login(Credentials creds) {
        try {
            if (em == null) {
                em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
            }

            Query q = em.createQuery("SELECT c FROM Competitor c WHERE c.email = :email AND c.password = :password");
            q.setParameter("email", creds.getEmail());
            q.setParameter("password", creds.getPassword());

            Competitor c = (Competitor) q.getSingleResult();

            return Response.status(200)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(c)
                    .build();

        } catch (NoResultException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciales inválidas")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(500).entity("Error interno en el servidor").build();
        }
    }

    // Clase auxiliar para recibir email y password
    public static class Credentials {

        private String email;
        private String password;

        public Credentials() {
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
