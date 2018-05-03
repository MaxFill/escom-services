/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maxfill.escom.bpm.model;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 *
 * @author Maxim
 */
@Stateless
public class LicenseFacade extends AbstractFacade<License> {
    @PersistenceContext(unitName = "EscomServicesPU")
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public LicenseFacade() {
        super(License.class);
    }
    
     /**
     * Ищет лицензию в базе по её номеру
     * @param number
     * @return
     */
    public List<License> findByNumber(String number){
        getEntityManager().getEntityManagerFactory().getCache().evict(License.class);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<License> cq = builder.createQuery(License.class);
        Root<License> c = cq.from(License.class);
        Predicate crit = builder.equal(c.get("number"), number);
        cq.select(c).where(builder.and(crit));
        TypedQuery<License> q = entityManager.createQuery(cq);
        return q.getResultList();
    }
}
