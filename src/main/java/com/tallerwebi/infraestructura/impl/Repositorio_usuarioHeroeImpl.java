package com.tallerwebi.infraestructura.impl;

import com.tallerwebi.dominio.entidades.*;
import com.tallerwebi.infraestructura.Repositorio_usuarioHeroe;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class Repositorio_usuarioHeroeImpl implements Repositorio_usuarioHeroe {

    private SessionFactory sessionFactory;

    @Autowired
    Repositorio_usuarioHeroeImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public UsuarioHeroe buscarRelacion(Usuario usuarioBuscado, Heroe heroeBuscado) {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(UsuarioHeroe.class)
                .add(Restrictions.eq("usuario", usuarioBuscado))
                .add(Restrictions.eq("heroe", heroeBuscado));

        return (UsuarioHeroe) criteria.uniqueResult();
    }

    @Override
    public void agregarRelacion(Usuario usuario, Heroe heroe) {

        Usuario usuarioBuscado = sessionFactory.getCurrentSession().get(Usuario.class, usuario.getId());

        Heroe heroeBuscado = sessionFactory.getCurrentSession().get(Heroe.class, heroe.getId());

        if (usuarioBuscado == null) throw new RuntimeException("No se encontro el usuario en BD");
        if(heroeBuscado == null) throw new RuntimeException("No se encontro el heroe en BD");

        UsuarioHeroe uh = buscarRelacion(usuarioBuscado, heroeBuscado);

        if(uh == null) {
            UsuarioHeroe UsuarioHeroe = new UsuarioHeroe(usuarioBuscado,heroeBuscado);
            sessionFactory.getCurrentSession().save(UsuarioHeroe);
        }else {
            throw new RuntimeException("La relacion entre usuario y heroe ya existe en BD");
        }

    }




//    @Override
//    public List<Heroe> getListaDeHeroes(Long idUsuario) {
//        return List.of();
//    }

    @Override
    public List<Heroe> getListaDeHeroes(Long idUsuario) {
        Session session = sessionFactory.getCurrentSession();
        //SELECT heroe.*
        //FROM usuario_heroe uh
        //JOIN heroe ON ch.heroe_id = heroe.id
        //WHERE ch.usuario_id = [idUsuario];

        // Crear criteria para la entidad de relación UsuarioHeroe
        Criteria criteria = session.createCriteria(UsuarioHeroe.class, "uh")
                .createAlias("uh.heroe", "heroe")// Añadir join con Heroe y seleccionar solo los héroes
                .setProjection(Projections.property("heroe"))//SELECT heroe.*
                .add(Restrictions.eq("uh.usuario.id", idUsuario));


        // Ejecutar la consulta
        @SuppressWarnings("unchecked")
        List<Heroe> heroes = criteria.list();

        if(heroes == null){
            heroes = new ArrayList<>();
        }

        return heroes;
    }


}
