package com.visuc.referentiel.contract.dao;

import java.util.List;

import com.visuc.referentiel.contract.entite.Contract;
import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;

public interface IContractDao {

	// ajouter/modifier un pays
	/**
	 * Save a single contract reference entity.
	 * @param pContract A contract to be saved.
	 * @return saved instance of contract.
	 */
	Contract saveOne(Contract pContract);
	
	// modifier un pays
	/**
	 * Update a single contract reference entity.
	 * @param pContract A contract reference entity to be updated.
	 * @return updated instance of contract reference entity.
	 */
	Contract updateOne(Contract pContract);
	
	// supprimer un vendeur(attention au objets liés)
	/**
	 * Delete a contract reference entity.
	 * @param pId An ID of the contract reference entity to be deleted.
	 */
	void deleteOne(Long pId);
	
	//rechercher par identifiant format Long
	/**
	 * Find the contract reference entity by its ID.
	 * @param pId An ID of the contract reference entity to be searched.
	 * @return A contract reference entity referenced by pId.
	 */
	Contract findById(Long pId);
	
	
	/**
	 * Search all contract reference entities for comission type noted by <pComissionType>.
	 * Result is returned by page. Desired page number and page size (number of results per page) are 
	 * passed in parameter. 
	 * @param pCommerceType commerce type
	 * @param numPage a number of the resultset page to be fetched
	 * @param pageSize number of desired results per page. 
	 * @return A list of Contract objects each corresponds to the contract reference entity in DB.
	 */
	List<Contract> findByComissionType(final int pComissionType, final int numPage,final int pageSize);
	
	// rechercher les vendeurs par le nom, avec pagination du résultat  
	/**
	 * Search all contract reference entities for a commerce type noted by <pCommerceType>.
	 * Result is returned by page. Desired page number and page size (number of results per page) are 
	 * passed in parameter. 
	 * @param pCommerceType commerce type
	 * @param numPage a number of the resultset page to be fetched
	 * @param pageSize number of desired results per page. 
	 * @return A list of Contract objects each corresponds to the contract reference entity in DB.
	 */
	List<Contract> findContractListByCommerceType(TypeCommerce pCommerceType,int numPage,int pageSize);

	Contract findSingleContractByCommerceType(TypeCommerce pCommerceType,int numPage,int pageSize);
}
