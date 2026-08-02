package com.tallerwebi.infraestructura.impl;

import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.infraestructura.RepositorioHeroe;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class RepositorioHeroeImpl implements RepositorioHeroe {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioHeroeImpl(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }


    @Override
    public Heroe buscarHeroePorId(Long id) {
        return sessionFactory.getCurrentSession().get(Heroe.class, id);
    }

    @Override
    public Heroe guardar(Heroe heroe) {
        sessionFactory.getCurrentSession().save(heroe);
        return heroe;
    }

    @Override
    public List<Heroe> getListaDeHeroes() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Heroe.class);
        return (List<Heroe>) criteria.list();
    }

    @Override
    public void modificar(Heroe heroe) {
        sessionFactory.getCurrentSession().update(heroe);
    }


}
