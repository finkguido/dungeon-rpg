package com.tallerwebi.infraestructura.impl;

import com.tallerwebi.dominio.entidades.Carruaje;
import com.tallerwebi.dominio.entidades.CarruajeHeroe;
import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.infraestructura.Repositorio_carruajeHeroe;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class Repositorio_carruajeHeroeImpl implements Repositorio_carruajeHeroe {

    private SessionFactory sessionFactory;
    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Autowired
    Repositorio_carruajeHeroeImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void agregarRelacion(Carruaje carruaje, Heroe heroe) {
        Carruaje carruajeBuscado = sessionFactory.getCurrentSession().get(Carruaje.class, carruaje.getId());
        Heroe heroeBuscado = sessionFactory.getCurrentSession().get(Heroe.class, heroe.getId());
        if (carruajeBuscado == null) throw new RuntimeException("No se encontro el carruaje en BD");
        if(heroeBuscado == null) throw new RuntimeException("No se encontro el heroe en BD");

        CarruajeHeroe ch = buscarRelacion(carruajeBuscado, heroeBuscado);

        if(ch == null) {
            CarruajeHeroe carruajeHeroe = new CarruajeHeroe(carruajeBuscado,heroeBuscado);
            sessionFactory.getCurrentSession().save(carruajeHeroe);
        }else {
            throw new RuntimeException("La relacion entre carruaje y heroe ya existe en BD");
        }


    }

    @Override
    public CarruajeHeroe buscarRelacion(Carruaje carruajeBuscado, Heroe heroeBuscado) {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(CarruajeHeroe.class)
                .add(Restrictions.eq("carruaje", carruajeBuscado))
                .add(Restrictions.eq("heroe", heroeBuscado));

        return (CarruajeHeroe) criteria.uniqueResult();
    }

    @Override
    public List<Heroe> getListaDeHeroes(Long idCarruaje) {
        Session session = sessionFactory.getCurrentSession();
        //SELECT heroe.*
        //FROM carruaje_heroe ch
        //JOIN heroe ON ch.heroe_id = heroe.id
        //WHERE ch.carruaje_id = [idCarruaje];

        // Crear criteria para la entidad de relación CarruajeHeroe
        Criteria criteria = session.createCriteria(CarruajeHeroe.class, "ch")
                        .createAlias("ch.heroe", "heroe")// Añadir join con Heroe y seleccionar solo los héroes
                        .setProjection(Projections.property("heroe"))//SELECT heroe.*
                        .add(Restrictions.eq("ch.carruaje.id", idCarruaje));//WHERE ch.carruaje_id = [idCarruaje];


        // Ejecutar la consulta
        @SuppressWarnings("unchecked")
        List<Heroe> heroes = criteria.list();
        if (heroes == null) throw new RuntimeException("No se encontro lista de heroes en el carruaje en BD");

        return heroes;
    }

    @Override
    public void removerRelacion(CarruajeHeroe carruajeHeroeBuscado) {
        Session session = sessionFactory.getCurrentSession();

        CarruajeHeroe relacionExistente = (CarruajeHeroe) session.createCriteria(CarruajeHeroe.class)
                .add(Restrictions.eq("id",carruajeHeroeBuscado.getId()))
                .uniqueResult();

        if (relacionExistente != null) {
            try {
                session.delete(relacionExistente);
            } catch (Exception e) {
                throw new RuntimeException("No se pudo eliminar la relación", e);
            }
        } else {
            throw new RuntimeException("La relación no existe en la base de datos");
        }
    }

    @Override
    public void quitarHeroeDeCarruaje(Carruaje carruajeBuscado, Heroe heroeBuscado) {

    }
    @Override
    public void add(Carruaje c, Heroe h) {
        // primero chequea duplicados si quieres
        Carruaje HeroeExisting = (Carruaje) session()
                .createCriteria(CarruajeHeroe.class)
                .add(Restrictions.eq("carruaje", c))
                .add(Restrictions.eq("heroe", h))
                .uniqueResult();

        if (HeroeExisting != null) {
            throw new RuntimeException("Ya está seleccionado ese héroe");
        }

        CarruajeHeroe ch = new CarruajeHeroe();
        ch.setCarruaje(c);
        ch.setHeroe(h);
        session().save(ch);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CarruajeHeroe> findByCarruaje(Carruaje c) {
        return session().createCriteria(CarruajeHeroe.class)
                .add(Restrictions.eq("carruaje", c))
                .list();
    }

    @Override
    public void delete(CarruajeHeroe relacion) {
        session().delete(relacion);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Heroe> findHeroesByCarruaje(Carruaje c) {
        return session()
                .createCriteria(CarruajeHeroe.class, "ch")
                .createAlias("ch.heroe", "h")
                .add(Restrictions.eq("ch.carruaje", c))
                .setProjection(Projections.property("h"))
                .list();
    }




}
