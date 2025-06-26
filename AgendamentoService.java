package com.bestbarber.service;

import com.bestbarber.model.Agendamento;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class AgendamentoService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("barbeariaPU");

    public List<Agendamento> buscarTodos() {
        EntityManager em = emf.createEntityManager();
        List<Agendamento> lista = em.createQuery("FROM Agendamento", Agendamento.class).getResultList();
        em.close();
        return lista;
    }

    public List<Agendamento> buscarPorData(String data) {
        EntityManager em = emf.createEntityManager();
        List<Agendamento> lista = em.createQuery(
            "SELECT a FROM Agendamento a WHERE a.data = :data", Agendamento.class)
            .setParameter("data", data)
            .getResultList();
        em.close();
        return lista;
    }

    public boolean salvar(Agendamento agendamento) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(agendamento);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean atualizar(Agendamento agendamento) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(agendamento);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean cancelar(Long id) {
        EntityManager em = emf.createEntityManager();
        Agendamento agendamento = em.find(Agendamento.class, id);
        if (agendamento != null) {
            em.getTransaction().begin();
            agendamento.setStatus("cancelado");
            em.merge(agendamento);
            em.getTransaction().commit();
            em.close();
            return true;
        }
        em.close();
        return false;
    }

    public Agendamento buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        Agendamento agendamento = em.find(Agendamento.class, id);
        em.close();
        return agendamento;
    }
}
