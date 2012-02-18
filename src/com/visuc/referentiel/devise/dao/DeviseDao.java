package com.visuc.referentiel.devise.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

import com.visuc.referentiel.devise.entite.Devise;

public class DeviseDao implements IDeviseDao{

	private JpaTemplate jpaTemplate;

	@SuppressWarnings("unused")
	private JdbcTemplate jdbcTemplate;

	public void setEntityManagerFactory(EntityManagerFactory emf) {
		jpaTemplate = new JpaTemplate(emf);
	}

	public void setDataSource(DataSource ds) {
		jdbcTemplate = new JdbcTemplate(ds);
	}
	
// *************************************************
//	***** méthodes du CRUD from IDeviseDao *******
// *************************************************
	
	// Creation d'une entite Devise
	public Devise saveOne(final Devise d) {
		return (Devise)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Devise devise_tmp = null;
				em.persist(d);
				devise_tmp = d;
				
				System.out.println("    -DEBUG-   DeviseDao.saveOne, "+devise_tmp);
				return devise_tmp;
			}
		});
	}
	
	// Mise a jour d'une entite Devise
	public Devise updateOne(final Devise d) {
		return (Devise)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Devise devise_tmp = null;
				devise_tmp = em.merge(d);
				
				System.out.println("    -DEBUG-   DeviseDao.updateOne, "+devise_tmp);
				return devise_tmp;
			}
		});
	}
	
	// Suppression a partir d'un id d'une entite devise
	public void deleteOne(final Long aId) {
		this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("delete from Devise v where v.id = :idDevise");
				query.setParameter("idDevise", aId);
				query.executeUpdate();
				// l'appel a getSingleREsult pour le delete ne fonctionne pas car il ne renvoit pas d'objet, on obtient une exception : Not supported for DML operations
				//query.getSingleResult();
				System.out.println("    -DEBUG-   DeviseDao.deleteOne ");
				return null;
			}
		});
	}
	
	
	// ***** Implementation dao pour recherche d'un Devise par son Id *******
	public Devise findById(final Long aId){
		return (Devise)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select v from Devise v where v.id = :idDevise");
				query.setParameter("idDevise", aId);
				Devise devise_temp = (Devise)query.getSingleResult();
				
				System.out.println("    -DEBUG DAO-   DeviseDao.findById "+devise_temp);
				
				return devise_temp;
			}
		});
	}
	
	
	// ***** Implementation dao de gestion des criteres de recherche *******
	@SuppressWarnings("unchecked")
	public List<Devise> findByName(final String nomDevise,final int numPage,final int pageSize) {
		return (List<Devise>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select v from Devise v where lower(v.nom) like :nomDevise");
				query.setParameter("nomDevise", nomDevise);
				query.setMaxResults(pageSize+1);
		        query.setFirstResult(numPage * pageSize);
		        
				List<Devise> list = query.getResultList();
				
				System.out.println("    -DEBUG DAO-   DeviseDao.findByName "+nomDevise);
				
				return list;
			}
		});
	}
	
	
}
