package com.tallerwebi.infraestructura.impl;

import com.tallerwebi.dominio.entidades.Expedition;
import com.tallerwebi.dominio.entidades.GameSession;
import com.tallerwebi.infraestructura.RepositorioExpedition;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class RepositorioExpeditionImpl implements RepositorioExpedition {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioExpeditionImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Expedition save(Expedition expedition) {
        session().saveOrUpdate(expedition);
        return expedition;
    }

    @Override
    public Optional<Expedition> findBySessionAndCompletedFalse(GameSession session) {
        Criteria crit = session()
                .createCriteria(Expedition.class)
                .add(Restrictions.eq("session", session))
                .add(Restrictions.eq("completed", false))
                .setMaxResults(1);
        Expedition e = (Expedition) crit.uniqueResult();
        return Optional.ofNullable(e);
    }
}
