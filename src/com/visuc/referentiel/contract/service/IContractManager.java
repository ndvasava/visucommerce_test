package com.visuc.referentiel.contract.service;

import java.util.List;

import com.visuc.referentiel.contract.entite.Contract;
import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;

public interface IContractManager {

	/**
	 * @param p
	 * @return
	 */
	public Contract save(Contract p) ;
	
	/**
	 * @param p
	 * @return
	 */
	public Contract update(Contract p) ;

	/**
	 * @param p
	 */
	public void deleteOne(Contract p) ;
	
// ***** Implementation service de gestion des criteres de recherche *******
	
	/**
	 * @param aId
	 * @return
	 */
	public Contract findById(Long aId);
	
	/**
	 * @param pTypeCommerce
	 * @param numPage
	 * @param pageSize
	 * @return
	 */
	public List<Contract> findContractListByCommerceType(TypeCommerce pTypeCommerce,int numPage,int pageSize) ;

	/**
	 * Find a single Contract for a commerce type
	 * @param pTypeCommerce
	 * @param pNumPage
	 * @param pPageSize
	 * @return
	 */
	public Contract findSingleContractByCommerceType(TypeCommerce pTypeCommerce, int pNumPage, int pPageSize);
	
// ******** Couche de service a rendre a l'action EditerContractAction
	
}
