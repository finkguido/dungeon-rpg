package com.tallerwebi.infraestructura.impl;

import com.tallerwebi.dominio.entidades.GameSession;
import com.tallerwebi.dominio.entidades.Monster;
import com.tallerwebi.dominio.entidades.SessionMonster;
import com.tallerwebi.infraestructura.RepositorioMonster;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class RepositorioMonsterImpl implements RepositorioMonster {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioMonsterImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Monster> obtenerTodosLosMonstruos() {
        return session()
                .createCriteria(Monster.class)
                .list();
    }
    @SuppressWarnings("unchecked")
    @Override
    public List<SessionMonster> findBySessionAndExpeditionNumber(GameSession session, int expeditionNumber) {
        return sessionFactory.getCurrentSession()
                .createCriteria(SessionMonster.class)
                .add(Restrictions.eq("session", session))
                .add(Restrictions.eq("expeditionNumber", expeditionNumber))
                .addOrder(Order.asc("orden"))
                .list();
    }

    @Override
    public Monster findById(Long monsterId) {
        return session().get(Monster.class, monsterId);
    }
}
