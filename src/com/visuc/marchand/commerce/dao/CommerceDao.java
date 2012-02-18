package com.visuc.marchand.commerce.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

import com.visuc.marchand.commerce.entite.Commerce;
import com.visuc.marchand.commerce.entite.ContractToCommerce;

public class CommerceDao implements ICommerceDao {

	private JpaTemplate jpaTemplate;
	
	@SuppressWarnings("unused")
	private JdbcTemplate jdbcTemplate;

	protected int pPageSize;
	
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		jpaTemplate = new JpaTemplate(emf);
	}

	public void setDataSource(DataSource ds) {
		jdbcTemplate = new JdbcTemplate(ds);
	}
	
	@Override
	public Commerce saveOne(final Commerce pCommerce) {

		return (Commerce) this.jpaTemplate.execute(new JpaCallback() {
			
			@Override
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Commerce persistedCommerce = null;
				
				// gestion du drapeau à ajouter en préambule a l'ajout de la langue elle meme si le drapeau est non null
				if((pCommerce.getAddress() != null)&&(pCommerce.getAddress().getId() == null)){
					em.persist(pCommerce.getAddress());
				}

				em.persist(pCommerce);
				persistedCommerce = pCommerce;
				
				System.out.println("--> CommerceDao.saveOne() " + pCommerce);
				return persistedCommerce; 
			}
		});
	}

	@Override
	public Commerce updateOne(final Commerce pCommerce) {
		return (Commerce) this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Commerce persistedCommerce = null;
				// gestion du drapeau à ajouter en préambule a l'ajout de la langue elle meme si le drapeau est non null
				if((pCommerce.getShopInfo() != null)
						&&(pCommerce.getShopInfo().getId() == null)){
					System.out.println("> " + pCommerce.getShopInfo().getShopAddress().getAddress1());
					System.out.println("> " + pCommerce.getShopInfo().getShopAddress().getCity().getNom());
					em.persist(pCommerce.getShopInfo());
					System.out.println("> persist address = OK");
				}
				em.merge(pCommerce);
				System.out.println("> update commerce = OK");
				persistedCommerce = pCommerce;
				
				System.out.println("--> CommerceDao.updateOne() " + persistedCommerce);
				return persistedCommerce;
			}
		});
	}

	@Override
	public void deleteOne(final Long aId) {
		this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("delete from Commerce c where c.id = :idCommerce");
				query.setParameter("idCommerce", aId);
				query.executeUpdate();
				System.out.println("--> CommerceDao.deleteOne() ");
				return null;
			}
		});

	}

	@Override
	public Commerce findById(final Long pCommerceId) {
		return (Commerce)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select v from Commerce v where v.id = :idCommerce");
				query.setParameter("idCommerce", pCommerceId);
				Commerce commerce = (Commerce)query.getSingleResult();
				
				System.out.println("--> CommerceDao.findById() :" + commerce.getId() + ", " + commerce.getNomCommerce());
				return commerce;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Commerce> findByName(final String pNomCommerce, final int pNumPage, final int pPageSize) {
		return (List<Commerce>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select c from Commerce c where lower(c.nomCommerce) like :nomCommerce");
				query.setParameter("nomCommerce", pNomCommerce);
				query.setMaxResults(pPageSize+1);
		        query.setFirstResult(pNumPage * pPageSize);
		        
				List<Commerce> list = query.getResultList();
				
				System.out.println("--> CommerceDao.findByName(" + pNomCommerce + ") : " + list.size());
				
				return list;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkUserIdExists(final String pUserId) {
		List<Commerce> commerceList = (List<Commerce>)this.jpaTemplate.execute(new JpaCallback() {
			
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select c from Commerce c where lower(c.userId) like :userId");
				query.setParameter("userId", pUserId);
				List<Commerce> list = query.getResultList();
				
				System.out.println("--> CommerceDao.findByName(" + pUserId + ") : " + list.size());
				return list;
			}
		});
		
		return commerceList.size() > 0 ? true : false;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Commerce> findByNameAndType(final Commerce pCommerce, final int pNumPage, final int pPageSize) {
		return (List<Commerce>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select c from Commerce c where lower(c.nomCommerce) like :commerceName "
						+ "and c.type = :idTypeCommerce");
				query.setParameter("nomCommerce", pCommerce.getNomCommerce());
				query.setParameter("idTypeCommerce", pCommerce.getType().getId());
				query.setMaxResults(pPageSize+1);
		        query.setFirstResult(pNumPage * pPageSize);
		        
				List<Commerce> list = query.getResultList();
				System.out.println("--> CommerceDao.findByNameAndType(" + ") " + list.size());
				return list;
			}
		});
	}


	@SuppressWarnings("unchecked")
	public List<Commerce> findByNameAndStatus(final String pName, final int pStatus, final int pNumPage, final int pPageSize) {
		return (List<Commerce>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				//Query query=em.createQuery("select c from Commerce c where c.nomCommerce like :commerceName "); 
						//+ "and c.contract.status = :commerceStatus");
				Query query = em.createQuery("select c from Commerce c where c.accStatus = :contractStatus and c.nomCommerce like :commerceName");
				query.setParameter("commerceName", pName);
				query.setParameter("contractStatus", pStatus);
				query.setMaxResults(pPageSize+1);
		        query.setFirstResult(pNumPage * pPageSize);
		        
				List<Commerce> list = query.getResultList();
				System.out.println("--> CommerceDao.findByNameAndStatus(" + ") " + list.size());
				return list;
			}
		});
	}

	// ************* Contract To Commerce DAO methods ***********************
	
	@Override
	public void deleteOneContractToCommerce(final Long pContractId) {

		this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("delete from Contract c where c.id = :idContract");
				query.setParameter("idContract", pContractId);
				query.executeUpdate();
				System.out.println("--> CommerceDao.deleteOneContract() ");
				return null;
			}
		});
		
	}

	@Override
	public ContractToCommerce findByCommerceId(final Long pCommerceId) {

		return (ContractToCommerce)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select c from ContractToCommerce c where c.commerce.id like :idCommerce");
				query.setParameter("idCommerce", pCommerceId);
				
				ContractToCommerce contract = (ContractToCommerce) query.getSingleResult();
				System.out.println("--> CommerceDao.findByName() " + pCommerceId);
				
				return contract;
			}
		});
		
	}

	@Override
	public ContractToCommerce saveOneContractToCommerce(
			final ContractToCommerce pContract) {

		return (ContractToCommerce) this.jpaTemplate.execute(new JpaCallback() {
			
			@Override
			public Object doInJpa(EntityManager em) throws PersistenceException {
				ContractToCommerce persistedContract = null;
				em.persist(pContract);
				persistedContract = pContract;
				
				System.out.println("--> CommerceDao.saveOneContractToCommerce() " + pContract);
				return persistedContract; 
			}
		});
	}

	@Override
	public ContractToCommerce updateOneContractToCommerce(
			final ContractToCommerce pContract) {

		return (ContractToCommerce) this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				
				ContractToCommerce persistedCommerce = null;
				em.merge(pContract);
				persistedCommerce = pContract;
				
				System.out.println("--> CommerceDao.updateOne() " + persistedCommerce);
				return persistedCommerce;
			}
		});
	}

}
