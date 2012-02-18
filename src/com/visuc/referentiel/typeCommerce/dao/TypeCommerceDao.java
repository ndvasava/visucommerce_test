package com.visuc.referentiel.typeCommerce.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;

public class TypeCommerceDao implements ITypeCommerceDao{

	private JpaTemplate jpaTemplate;

	@SuppressWarnings("unused")
	private JdbcTemplate jdbcTemplate;

	public void setEntityManagerFactory(EntityManagerFactory emf) {
		jpaTemplate = new JpaTemplate(emf);
	}

	public void setDataSource(DataSource ds) {
		jdbcTemplate = new JdbcTemplate(ds);
	}
	
// **************************************************
//	 ***** méthodes du CRUD from ITypeCommerceDao *******
// **************************************************
	
	// Creation d'une entite TypeCommerce
	public TypeCommerce saveOne(final TypeCommerce tc) {
		return (TypeCommerce)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				TypeCommerce typeCommerce_tmp = null;
				em.persist(tc);
				typeCommerce_tmp = tc;
				
				System.out.println("    -DEBUG-   TypeCommerceDao.saveOne, "+typeCommerce_tmp);
				return typeCommerce_tmp;
			}
		});
	}
	
	// Mise a jour d'une entite TypeCommerce
	public TypeCommerce updateOne(final TypeCommerce tc) {
		return (TypeCommerce)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				TypeCommerce typeCommerce_tmp = null;
				// Sauvegarde du typeCommerce nouvelle version
				typeCommerce_tmp = em.merge(tc);
				
				System.out.println("    -DEBUG-   TypeCommerceDao.updateOne, "+typeCommerce_tmp);
				return typeCommerce_tmp;
			}
		});
	}
	
	// Suppression a partir d'un id d'une entite typeCommerce
	public void deleteOne(final Long aId) {
		this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("delete from TypeCommerce tc where tc.id = :idTypeCommerce");
				query.setParameter("idTypeCommerce", aId);
				query.executeUpdate();
				
				System.out.println("    -DEBUG-   TypeCommerceDao.deleteOne ");
				return null;
			}
		});
	}
	
	
	// ***** Implementation dao pour recherche d'un TypeCommerce par son Id *******
	public TypeCommerce findById(final Long aId){
		return (TypeCommerce)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select tc from TypeCommerce tc where tc.id = :idTypeCommerce");
				query.setParameter("idTypeCommerce", aId);
				TypeCommerce typeCommerce_temp = (TypeCommerce)query.getSingleResult();
				
				System.out.println("    -DEBUG DAO-   TypeCommerceDao.findById "+typeCommerce_temp);
				return typeCommerce_temp;
			}
		});
	}
	
	
	// ***** Implementation dao de gestion des criteres de recherche *******
	@SuppressWarnings("unchecked")
	public List<TypeCommerce> findByName(final String nom,final int numPage,final int pageSize) {
		return (List<TypeCommerce>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select tc from TypeCommerce tc where lower(tc.typeCommerceName) like :typcom");
				query.setParameter("typcom", nom);
				query.setMaxResults(pageSize+1);
		        query.setFirstResult(numPage * pageSize);
		        
				List<TypeCommerce> list = query.getResultList();
				
				System.out.println("    -DEBUG DAO-   TypeCommerceDao.findByName "+nom);
				return list;
			}
		});
	}
	
}
