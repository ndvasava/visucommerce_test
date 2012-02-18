package com.visuc.marchand.commerce.dao;

import java.util.List;

import com.visuc.marchand.commerce.entite.Commerce;
import com.visuc.marchand.commerce.entite.ContractToCommerce;

public interface ICommerceDao {

	// ajouter/modifier un pays
	Commerce saveOne(Commerce pCommerce);
	
	// modifier un pays
	Commerce updateOne(Commerce p);
	
	// supprimer un vendeur(attention au objets liés)
	void deleteOne(Long aId);
	
	//rechercher par identifiant format Long
	Commerce findById(Long aId);
	
	// rechercher les vendeurs par le nom, avec pagination du résultat  
	List<Commerce> findByName(String nom,int numPage,int pageSize);
	
	List<Commerce> findByNameAndType(Commerce pCommerce, int pNumPage, int pPageSize);
	
	List<Commerce> findByNameAndStatus(String pNom, int pStatus, final int pNumPage, final int pPageSize);
	
	//************** Contract to commerce Dao *******************
	
	ContractToCommerce saveOneContractToCommerce(ContractToCommerce pContract);
	
	ContractToCommerce updateOneContractToCommerce(ContractToCommerce pContract);
	
	void deleteOneContractToCommerce(Long pContractId);
	
	ContractToCommerce findByCommerceId(Long pCommerceId);

	boolean checkUserIdExists(String pNomCommerce);
	
}
