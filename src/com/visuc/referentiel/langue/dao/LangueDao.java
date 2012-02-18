package com.visuc.referentiel.langue.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

import com.visuc.referentiel.langue.entite.Langue;

public class LangueDao implements ILangueDao{

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
//	 ***** méthodes du CRUD from ILangueDao *******
// **************************************************
	
	// Creation d'une entite Langue
	public Langue saveOne(final Langue l) {
		return (Langue)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Langue langue_tmp = null;
				
				// gestion du drapeau à ajouter en préambule a l'ajout de la langue elle meme si le drapeau est non null
				if((l.getDrapeau() != null)&&(l.getDrapeau().getId() == null)){
					em.persist(l.getDrapeau());
				}
				
				em.persist(l);
				langue_tmp = l;
				
				System.out.println("    -DEBUG-   LangueDao.saveOne, "+langue_tmp);
				return langue_tmp;
			}
		});
	}
	
	// Mise a jour d'une entite Langue
	public Langue updateOne(final Langue l) {
		return (Langue)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Langue langue_tmp = null;
				//recuperation en refresh de l'ancienne valeur en base
				langue_tmp = l;
				
				// gestion d'un ajout d'image en modification, l'image est alors transient, non rattaché au contexte de persistence
				if((l.getDrapeau() != null)&&(l.getDrapeau().getId() == null)){
					em.persist(l.getDrapeau());
				}
				// gestion d'une suppression de drapeau, via un refresh avec comparaison de la langue à updater
				if((l.getDrapeau() != null)&&(l.getDrapeau().getContent() == null)){
					em.remove(l.getDrapeau());
					langue_tmp.setDrapeau(null);
				}else {
					em.merge(l.getDrapeau());
				}
				
				// TODO, gestion d'un remplacement avec un drapeau déjà en base (identifié par le nom)
				// Alors on replace l'id dans fk_image et on ne cré par de nouvelle image dans la table
				
				// Sauvegarde finale de la langue
				langue_tmp = em.merge(l);
				
				System.out.println("    -DEBUG-   LangueDao.updateOne, "+langue_tmp);
				return langue_tmp;
			}
		});
	}
	
	// Suppression a partir d'un id d'une entite langue
	public void deleteOne(final Long aId) {
		this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("delete from Langue v where v.id = :idLangue");
				query.setParameter("idLangue", aId);
				query.executeUpdate();
				
				System.out.println("    -DEBUG-   LangueDao.deleteOne ");
				return null;
			}
		});
	}
	
	
	// ***** Implementation dao pour recherche d'un Langue par son Id *******
	public Langue findById(final Long aId){
		return (Langue)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select v from Langue v where v.id = :idLangue");
				query.setParameter("idLangue", aId);
				Langue langue_temp = (Langue)query.getSingleResult();
				
				System.out.println("    -DEBUG DAO-   LangueDao.findById "+langue_temp);
				return langue_temp;
			}
		});
	}
	
	
	// ***** Implementation dao de gestion des criteres de recherche *******
	@SuppressWarnings("unchecked")
	public List<Langue> findByNameOrCode(final String nomLangue,final String codeIso,final int numPage,final int pageSize) {
		return (List<Langue>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select v from Langue v where lower(v.nom) like :nomLangue OR lower(v.codeIso) like :codeIso");
				query.setParameter("nomLangue", nomLangue);
				query.setParameter("codeIso", codeIso);
				query.setMaxResults(pageSize+1);
		        query.setFirstResult(numPage * pageSize);
		        
				List<Langue> list = query.getResultList();
				
				System.out.println("    -DEBUG DAO-   LangueDao.findByName "+nomLangue+", "+codeIso);
				return list;
			}
		});
	}
	
	
	// Calcul de tous les pays existants, en supprimant ceux déjà liés à la devise courante.
	@SuppressWarnings("unchecked")
	public List<Langue> findAllNotlinkedLanguages_4Country(final Long aId){
		return (List<Langue>)this.jpaTemplate.execute(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				Query query=em.createQuery("select distinct l from Langue l");
				List<Langue> list = query.getResultList();
				
				return list;
			}
		});
	}
}
