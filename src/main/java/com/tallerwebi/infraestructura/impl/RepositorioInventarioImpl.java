package com.tallerwebi.infraestructura.impl;


import com.tallerwebi.dominio.entidades.Inventario;
import com.tallerwebi.infraestructura.RepositorioInventario;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public class RepositorioInventarioImpl implements RepositorioInventario {

    @Autowired
    private SessionFactory sessionFactory;

    public void guardar(Inventario inventario) {
       sessionFactory.getCurrentSession().save(inventario);
    }

    @Override
    public Inventario buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Inventario.class, id);
    }
}
