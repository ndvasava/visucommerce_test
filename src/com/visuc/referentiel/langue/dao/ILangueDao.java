package com.visuc.referentiel.langue.dao;

import java.util.List;

import com.visuc.referentiel.langue.entite.Langue;

public interface ILangueDao {

	// ajouter/modifier un pays
	Langue saveOne(Langue l);
	
	// modifier un pays
	Langue updateOne(Langue l);
	
	// supprimer un vendeur(attention au objets li�s)
	void deleteOne(Long aId);
	
	//rechercher par identifiant format Long
	Langue findById(Long aId);
	
	// rechercher les langues par le nom, avec pagination du r�sultat  
	List<Langue> findByNameOrCode(String nomLangue,String codeIso,int numPage,int pageSize);
	
	// rechercher tous les pays li�s � une langue, ou non (recherche finalement exhaustive car une langue et un pays peuvent etre li�s � plusieurs entit�s (relation manyToMany)
	List<Langue> findAllNotlinkedLanguages_4Country(Long aId);
}
