package com.visuc.marchand.vendeur.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

import com.visuc.marchand.vendeur.entite.Vendeur;

public class VendeurDao implements IVendeurDao{

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
//	***** méthodes du CRUD from IVendeurDao *******
// *************************************************
	
	// Creation d'une entite Vendeur
	public Vendeur saveOne(final Vendeur v) {
		return (Vendeur)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Vendeur vendeur_tmp = null;
				
				// persistance for the photo before the persistance of salesperson himself, 
				// if the photo exists
				if((v.getPhoto() != null)&&(v.getPhoto().getId() == null)){
					em.persist(v.getPhoto());
				}

				em.persist(v);
				vendeur_tmp = v;
				
				System.out.println("--> VendeurDao.saveOne() " + vendeur_tmp);
				return vendeur_tmp;
			}
		});
	}
	
	// Mise a jour d'une entite Vendeur
	public Vendeur updateOne(final Vendeur v) {
		return (Vendeur)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Vendeur vendeur_tmp = null;
				vendeur_tmp = em.merge(v);
				
				System.out.println("    -DEBUG-   VendeurDao.updateOne, "+vendeur_tmp);
				return vendeur_tmp;
			}
		});
	}
	
	// Suppression a partir d'un id d'une entite vendeur
	public void deleteOne(final Long aId) {
		this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("delete from Vendeur v where v.id = :idVendeur");
				query.setParameter("idVendeur", aId);
				query.executeUpdate();
				// l'appel a getSingleREsult pour le delete ne fonctionne pas car il ne renvoit pas d'objet, on obtient une exception : Not supported for DML operations
				//query.getSingleResult();
				System.out.println("    -DEBUG-   VendeurDao.deleteOne ");
				return null;
			}
		});
	}
	
	
	// ***** Implementation dao pour recherche d'un Vendeur par son Id *******
	public Vendeur findById(final Long aId){
		return (Vendeur)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select v from Vendeur v where v.id = :idVendeur");
				query.setParameter("idVendeur", aId);
				Vendeur vendeur_temp = (Vendeur)query.getSingleResult();
				
				System.out.println("    -DEBUG DAO-   VendeurDao.findById "+vendeur_temp);
				
				return vendeur_temp;
			}
		});
	}
	
	
	// ***** Implementation dao de gestion des criteres de recherche *******
	@SuppressWarnings("unchecked")
	public List<Vendeur> findByName(final String nomVendeur,final int numPage,final int pageSize) {
		return (List<Vendeur>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select v from Vendeur v where lower(v.nom) like :nomVendeur");
				query.setParameter("nomVendeur", nomVendeur);
				query.setMaxResults(pageSize+1);
		        query.setFirstResult(numPage * pageSize);
		        
				List<Vendeur> list = query.getResultList();;
				
				System.out.println("    -DEBUG DAO-   VendeurDao.findByName "+nomVendeur);
				
				return list;
			}
		});
	}
	
}
