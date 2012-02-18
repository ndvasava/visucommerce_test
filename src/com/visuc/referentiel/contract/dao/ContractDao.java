package com.visuc.referentiel.contract.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

import com.visuc.referentiel.contract.entite.Contract;
import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;

public class ContractDao implements IContractDao {

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
	public Contract saveOne(final Contract v) {
		return (Contract)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Contract contract_tmp = null;
				em.persist(v);
				contract_tmp = v;
				
				System.out.println("    -DEBUG-   VendeurDao.saveOne, " + contract_tmp);
				return contract_tmp;
			}
		});
	}
	
	// Mise a jour d'une entite Contract
	public Contract updateOne(final Contract v) {
		return (Contract)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Contract Contract_tmp = null;
				Contract_tmp = em.merge(v);
				
				System.out.println("    -DEBUG-   ContractDao.updateOne, "+Contract_tmp);
				return Contract_tmp;
			}
		});
	}
	
	// Suppression a partir d'un id d'une entite Contract
	public void deleteOne(final Long aId) {
		this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("delete from Contract v where v.id = :idContract");
				query.setParameter("idContract", aId);
				query.executeUpdate();
				// l'appel a getSingleREsult pour le delete ne fonctionne pas car il ne renvoit pas d'objet, on obtient une exception : Not supported for DML operations
				//query.getSingleResult();
				System.out.println("    -DEBUG-   ContractDao.deleteOne ");
				return null;
			}
		});
	}
	
	
	// ***** Implementation dao pour recherche d'un Contract par son Id *******
	public Contract findById(final Long aId){
		return (Contract)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select c from Contract c where c.id = :idContract");
				query.setParameter("idContract", aId);
				Contract Contract_temp = (Contract)query.getSingleResult();
				
				System.out.println("    -DEBUG DAO-   ContractDao.findById "+Contract_temp);
				
				return Contract_temp;
			}
		});
	}
	
	
	// ***** Implementation dao de gestion des criteres de recherche *******
	@SuppressWarnings("unchecked")
	public List<Contract> findByComissionType(final int pComissionType, final int numPage,final int pageSize) {
		return (List<Contract>)this.jpaTemplate.execute(new JpaCallback() {
			
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select c from Contract c where c.comissionType = :comissionType");
				query.setParameter("comissionType", pComissionType);
				query.setMaxResults(pageSize+1);
		        query.setFirstResult(numPage * pageSize);
		        
				List<Contract> list = query.getResultList();
				
				System.out.println("    -DEBUG DAO-   ContractDao.findByName " + list.size());
				
				return list;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Contract> findContractListByCommerceType(final TypeCommerce pCommerceType, final int pNumPage, final int pPageSize) {
		
		return (List<Contract>)this.jpaTemplate.execute(new JpaCallback() {
			
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select c from Contract c where c.commerceType.id = :idTypeCommerce");
				query.setParameter("idTypeCommerce", pCommerceType.getId());
				query.setMaxResults(pPageSize+1);
		        query.setFirstResult(pNumPage * pPageSize);
		        
				List<Contract> list = query.getResultList();
				
				System.out.println("    -DEBUG DAO-   ContractDao.findByCommerceType " + list.size());
				
				return list;
			}
		});
	}

	@Override
	public Contract findSingleContractByCommerceType(
			final TypeCommerce pCommerceType, final int pNumPage, final int pPageSize) {
		
		return (Contract)this.jpaTemplate.execute(new JpaCallback() {
					
				public Object doInJpa(EntityManager em) throws PersistenceException {
					Query query=em.createQuery("select c from Contract c where c.commerceType.id = :idTypeCommerce");
					query.setParameter("idTypeCommerce", pCommerceType.getId());
					query.setMaxResults(pPageSize+1);
			        query.setFirstResult(pNumPage * pPageSize);
			        
					Contract list = (Contract) query.getSingleResult();
					
					System.out.println("    -DEBUG DAO-   ContractDao.findSingleContractByCommerceType "
							+ list.getComissionType() + list.getComissionAmount());
					
					return list;
				}
		});
	}
	
}
