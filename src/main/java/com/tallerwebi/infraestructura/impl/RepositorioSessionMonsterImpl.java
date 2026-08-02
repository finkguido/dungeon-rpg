package com.tallerwebi.infraestructura.impl;

import com.tallerwebi.dominio.entidades.GameSession;
import com.tallerwebi.dominio.entidades.Monster;
import com.tallerwebi.dominio.entidades.SessionMonster;
import com.tallerwebi.infraestructura.RepositorioSessionMonster;
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
public class RepositorioSessionMonsterImpl
        implements RepositorioSessionMonster {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioSessionMonsterImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void add(GameSession session, Monster monster) {
        // calculo de orden con Criteria y Projections
        Criteria crit = session()
                .createCriteria(SessionMonster.class)
                .add(Restrictions.eq("session", session))
                .setProjection(Projections.rowCount());
        Long count = (Long) crit.uniqueResult();

        SessionMonster sm = new SessionMonster();
        sm.setSession(session);
        sm.setMonster(monster);
        sm.setVidaActual(monster.getVida());
        sm.setExpeditionNumber(1);
        sm.setDungeonNumber(1);
        sm.setOrden(count.intValue() + 1);
        session().save(sm);
    }

    @Override
    public void add(GameSession session, Monster monster, int dungeonNumber) {
        // 1) calcular el nuevo orden dentro de esta mazmorra
        Criteria countCrit = session()
                .createCriteria(SessionMonster.class)
                .add(Restrictions.eq("session", session))
                .add(Restrictions.eq("dungeonNumber", dungeonNumber))
                .setProjection(Projections.rowCount());
        Long count = (Long) countCrit.uniqueResult();

        // 2) crear y poblar la nueva entidad
        SessionMonster sm = new SessionMonster();
        sm.setSession(session);
        sm.setMonster(monster);
        sm.setVidaActual(monster.getVida());

        sm.setExpeditionNumber(dungeonNumber);
        sm.setDungeonNumber(dungeonNumber);
        sm.setOrden(count.intValue() + 1);

        session().save(sm);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SessionMonster> findBySession(GameSession session) {
        return session()
                .createCriteria(SessionMonster.class)
                .add(Restrictions.eq("session", session))
                .addOrder(Order.asc("orden"))
                .list();
    }

    @Override
    public void update(SessionMonster sm) {
        session().update(sm);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deleteBySession(GameSession session) {

        List<SessionMonster> lista = session()
                .createCriteria(SessionMonster.class)
                .add(Restrictions.eq("session", session))
                .list();

        for (SessionMonster sm : lista) {
            session().delete(sm);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deleteBySessionAndDungeonNumber(GameSession session, int dungeonNumber) {
        List<SessionMonster> lista = session()
                .createCriteria(SessionMonster.class)
                .add(Restrictions.eq("session", session))
                .add(Restrictions.eq("dungeonNumber", dungeonNumber))
                .list();

        for (SessionMonster sm : lista) {
            session().delete(sm);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SessionMonster> findBySessionAndExpeditionNumber(GameSession session, int expeditionNumber) {
        return session()
                .createCriteria(SessionMonster.class)
                .add(Restrictions.eq("session", session))
                .add(Restrictions.eq("expeditionNumber", expeditionNumber))
                .addOrder(Order.asc("orden"))
                .list();
    }

    @Override
    public List findBySessionAndDungeonNumber(GameSession session, int dungeonNumber) {
        return session()
                .createCriteria(SessionMonster.class)
                .add(Restrictions.eq("session", session))
                .add(Restrictions.eq("dungeonNumber", dungeonNumber))
                .addOrder(Order.asc("orden"))
                .list();
    }

    @Override
    public void deleteBySessionAndExpeditionNumber(GameSession gameSession, int expeditionNumber) {
        Session s = session();
        Criteria crit = s.createCriteria(SessionMonster.class)
                .add(Restrictions.eq("session", gameSession))
                .add(Restrictions.eq("expeditionNumber", expeditionNumber));

        @SuppressWarnings("unchecked")
        List<SessionMonster> monstersToDelete = crit.list();
        for (SessionMonster sm : monstersToDelete) {
            s.delete(sm);
        }
    }

}

