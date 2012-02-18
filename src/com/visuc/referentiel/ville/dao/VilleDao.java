package com.visuc.referentiel.ville.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

import com.visuc.referentiel.ville.entite.Ville;

public class VilleDao implements IVilleDao{

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
//	***** méthodes du CRUD from IVilleDao *******
// *************************************************
	
	// Creation d'une entite Ville
	public Ville saveOne(final Ville v) {
		return (Ville)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Ville ville_tmp = null;
				em.persist(v);
				ville_tmp = v;
				
				System.out.println("    -DEBUG-   VilleDao.saveOne, "+ville_tmp);
				return ville_tmp;
			}
		});
	}
	
	// Mise a jour d'une entite Ville
	public Ville updateOne(final Ville v) {
		return (Ville)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Ville ville_tmp = null;
				ville_tmp = em.merge(v);
				
				System.out.println("    -DEBUG-   VilleDao.updateOne, "+ville_tmp);
				return ville_tmp;
			}
		});
	}
	
	// Suppression a partir d'un id d'une entite ville
	public void deleteOne(final Long aId) {
		this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("delete from Ville v where v.id = :idVille");
				query.setParameter("idVille", aId);
				query.executeUpdate();
				// l'appel a getSingleREsult pour le delete ne fonctionne pas car il ne renvoit pas d'objet, on obtient une exception : Not supported for DML operations
				//query.getSingleResult();
				System.out.println("    -DEBUG-   VilleDao.deleteOne ");
				return null;
			}
		});
	}
	
	
	// ***** Implementation dao pour recherche d'un Ville par son Id *******
	public Ville findById(final Long aId){
		return (Ville)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select v from Ville v where v.id = :idVille");
				query.setParameter("idVille", aId);
				Ville ville_temp = (Ville)query.getSingleResult();
				
				System.out.println("    -DEBUG DAO-   VilleDao.findById "+ville_temp);
				
				return ville_temp;
			}
		});
	}
	
	
	// ***** Implementation dao de gestion des criteres de recherche *******
	@SuppressWarnings("unchecked")
	public List<Ville> findByName(final String nomVille,final int numPage,final int pageSize) {
		return (List<Ville>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select v from Ville v where lower(v.nom) like :nomVille");
				query.setParameter("nomVille", nomVille);
				query.setMaxResults(pageSize+1);
		        query.setFirstResult(numPage * pageSize);
		        
				List<Ville> list = query.getResultList();;
				
				System.out.println("    -DEBUG DAO-   VilleDao.findByName "+nomVille);
				
				return list;
			}
		});
	}
	
	
	// Calcul de toutes les villes existantes, en supprimant ceux déjà liés au pays courant.
	@SuppressWarnings("unchecked")
	public List<Ville> findAllNotlinkedVilles(final Long aId){
		return (List<Ville>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select distinct v from Ville v, Pays p where v.pays = p AND p.id=:idPays OR v.pays is null");
				query.setParameter("idPays", aId);
				List<Ville> list = query.getResultList();
				
				return list;
			}
		});
	}
	
	// Calcul de toutes les villes existantes, en supprimant ceux déjà liés au pays courant.
	@SuppressWarnings("unchecked")
	public List<Ville> listAllVillesPays(final Long aId){
		return (List<Ville>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select distinct v from Ville v, Pays p where v.pays = p AND p.id=:idPays");
				query.setParameter("idPays", aId);
				List<Ville> list = query.getResultList();
				
				return list;
			}
		});
	}
}
