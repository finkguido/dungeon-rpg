package com.tallerwebi.infraestructura.impl;

import com.tallerwebi.dominio.entidades.GameSession;
import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.dominio.entidades.SessionHero;
import com.tallerwebi.infraestructura.RepositorioHeroSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class RepositorioHeroSessionImpl implements RepositorioHeroSession {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioHeroSessionImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void add(GameSession session, Heroe hero, int i) {
        // 1) calcular orden
        Criteria countCrit = session()
                .createCriteria(SessionHero.class)
                .add(Restrictions.eq("session", session))
                .setProjection(Projections.rowCount());
        Long count = (Long) countCrit.uniqueResult();

        // 2) crear y guardar
        SessionHero sh = new SessionHero();
        sh.setSession(session);
        sh.setHero(hero);
        sh.setVidaActual(hero.getMaxVida());
        sh.setOrden(count.intValue() + 1);

        session().save(sh);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SessionHero> findBySession(GameSession session) {
        Criteria crit = session()
                .createCriteria(SessionHero.class)
                .add(Restrictions.eq("session", session))
                .addOrder(Order.asc("orden"));
        return crit.list();
    }

    @Override
    public void update(SessionHero sessionHero) {
        session().update(sessionHero);
    }

    @Override
    public void deleteBySession(GameSession session) {
        session()
                .createQuery("DELETE FROM SessionHero sh WHERE sh.session = :s")
                .setParameter("s", session)
                .executeUpdate();
    }
}
