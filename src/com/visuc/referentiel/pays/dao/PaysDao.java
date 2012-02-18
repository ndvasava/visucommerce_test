package com.visuc.referentiel.pays.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

import com.visuc.referentiel.pays.entite.Pays;

public class PaysDao implements IPaysDao{

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
//	***** méthodes du CRUD from IPaysDao *******
// *************************************************
	
	// Creation d'une entite Pays
	public Pays saveOne(final Pays p) {
		return (Pays)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Pays pays_tmp = null;
				em.persist(p);
				pays_tmp = p;
				
				System.out.println("    -DEBUG-   PaysDao.saveOne, "+pays_tmp);
				return pays_tmp;
			}
		});
	}
	
	// Mise a jour d'une entite Pays
	public Pays updateOne(final Pays p) {
		return (Pays)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Pays pays_tmp = null;
				System.out.println(" PaysDao.updateOne-TESTJPA - --  - - - -  "+em.contains(p));
				
				pays_tmp = em.merge(p);
				
				System.out.println("    -DEBUG-   PaysDao.updateOne, "+pays_tmp);
				System.out.println(" PaysDao.updateOne-TESTJPA - --  - - - -  "+em.contains(p));
				
				return pays_tmp;
			}
		});
	}
	
	// Suppression a partir d'un id d'une entite pays
	public void deleteOne(final Long aId) {
		this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("delete from Pays p where p.id = :idPays");
				query.setParameter("idPays", aId);
				query.executeUpdate();
				// l'appel a getSingleREsult pour le delete ne fonctionne pas car il ne renvoit pas d'objet, on obtient une exception : Not supported for DML operations
				//query.getSingleResult();
				System.out.println("    -DEBUG-   PaysDao.deleteOne ");
				return null;
			}
		});
	}
	
	
	// ***** Implementation dao pour recherche d'un Pays par son Id *******
	public Pays findById(final Long aId){
		return (Pays)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select p from Pays p where p.id = :idPays");
				query.setParameter("idPays", aId);
				Pays pays_temp = (Pays)query.getSingleResult();
				
				System.out.println("    -DEBUG DAO-   PaysDao.findById "+pays_temp);
				
				return pays_temp;
			}
		});
	}
	
	
	// ***** Implementation dao de gestion des criteres de recherche *******
	@SuppressWarnings("unchecked")
	public List<Pays> findByName(final String nomPays,final int numPage,final int pageSize) {
		return (List<Pays>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select p from Pays p where lower(p.nom) like :nomPays");
				query.setParameter("nomPays", nomPays);
				query.setMaxResults(pageSize+1);
		        query.setFirstResult(numPage * pageSize);
		        
				List<Pays> list = query.getResultList();;
				
				System.out.println("    -DEBUG DAO-   PaysDao.findByName "+nomPays);
				
				return list;
			}
		});
	}
	
	
	// Calcul de tous les pays existants, en supprimant ceux déjà liés à la devise courante.
	@SuppressWarnings("unchecked")
	public List<Pays> findAllNotlinkedCountries_WithoutDevise(){
		return (List<Pays>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select distinct p from Pays p where p.devise is null");
				List<Pays> list = query.getResultList();
				
				return list;
			}
		});
	}
	
	
	// Calcul de tous les pays existants, en supprimant ceux déjà liés à la devise courante.
	@SuppressWarnings("unchecked")
	public List<Pays> findAllNotlinkedCountries_4Devise(final Long aId){
		return (List<Pays>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select distinct p from Pays p, Devise d where p.devise = d AND d.id=:idDevise OR p.devise is null");
				query.setParameter("idDevise", aId);
				List<Pays> list = query.getResultList();
				
				return list;
			}
		});
	}
	
	// Calcul de tous les pays existants, en supprimant ceux déjà liés à la devise courante.
	@SuppressWarnings("unchecked")
	public List<Pays> findAllNotlinkedCountries_4Langue(final Long aId){
		return (List<Pays>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select distinct p from Pays p");
				//query.setParameter("idLangue", aId);
				List<Pays> list = query.getResultList();
				
				return list;
			}
		});
	}
	
}
