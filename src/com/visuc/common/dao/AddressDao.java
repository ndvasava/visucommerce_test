package com.visuc.common.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

import com.visuc.common.entity.Address;

public class AddressDao implements IAddressDao {

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
	public Address saveOne(final Address pAddress) {

		return (Address) this.jpaTemplate.execute(new JpaCallback() {
			
			@Override
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Address persistedAddress = null;
				em.persist(pAddress);
				persistedAddress = pAddress;
				
				System.out.println("--> AddressDao.saveOne() " + pAddress);
				return persistedAddress; 
			}
		});
	}

	@Override
	public Address updateOne(final Address pAddress) {
		return (Address) this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Address persistedAddress = null;
				em.merge(pAddress);
				persistedAddress = pAddress;
				
				System.out.println("--> AddressDao.updateOne() " + persistedAddress);
				return persistedAddress;
			}
		});
	}

	@Override
	public void deleteOne(final Long aId) {
		this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("delete from Address c where c.id = :idAddress");
				query.setParameter("idAddress", aId);
				query.executeUpdate();
				System.out.println("--> AddressDao.deleteOne() ");
				return null;
			}
		});

	}

	@Override
	public Address findById(final Long pAddressId) {
		return (Address)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select v from Address v where v.id = :idAddress");
				query.setParameter("idAddress", pAddressId);
				Address address = (Address)query.getSingleResult();
				
				System.out.println("--> AddressDao.findById() :" + address.getId() + ", " + address.getAddress1() + ", " + address.getCity().getNom());
				return address;
			}
		});
	}


}
