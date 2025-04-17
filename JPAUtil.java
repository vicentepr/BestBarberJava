package Util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Classe utilitária para obter o EntityManager a partir do persistence.xml
 */
public class JPAUtil {

    // Nome do persistence-unit definido no persistence.xml
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("barbeariaPU");

    /**
     * Retorna uma nova instância de EntityManager
     */
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
