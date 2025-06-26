package com.bestbarber.service;

import com.bestbarber.model.Cliente;
import javax.persistence.*;

import java.util.List;

public class ClienteService {

    private EntityManager getEntityManager() {
        return Persistence.createEntityManagerFactory("barbeariaPU").createEntityManager();
    }

    public Cliente autenticar(String email, String senha) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery(
                "SELECT c FROM Cliente c WHERE c.email = :email AND c.senha = :senha", Cliente.class);
            query.setParameter("email", email);
            query.setParameter("senha", senha);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void salvar(Cliente cliente) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        if (cliente.getId() == null) {
            em.persist(cliente);
        } else {
            em.merge(cliente);
        }
        em.getTransaction().commit();
        em.close();
    }

    public List<Cliente> listar() {
        EntityManager em = getEntityManager();
        return em.createQuery("FROM Cliente", Cliente.class).getResultList();
    }
    public Cliente buscarPorEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cliente c WHERE c.email = :email", Cliente.class)
                     .setParameter("email", email)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}