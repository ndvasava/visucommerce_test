package com.visuc.marchand.commerce.service;

import java.util.List;

import com.visuc.marchand.commerce.entite.Commerce;
import com.visuc.marchand.commerce.entite.ContractToCommerce;

public interface ICommerceManager {
	
	
	// ***** data manipulation methods *****
	
	public Commerce saveCommerce(Commerce pCommerce) ;
	
	public Commerce updateCommerce(Commerce pCommerce) ;

	public void deleteOneCommerce(Commerce pCommerce) ;
	

	// ***** data search methods *******
	
	public Commerce findCommerceById(Long pId);
	
	public List<Commerce> findCommerceByName(String pNom, int pNumPage, int pPageSize) ;

	public List<Commerce> findCommerceByNameAndType(Commerce pCommerce, int pNumPage, int pPageSize);
	
	public List<Commerce> findCommerceByNameAndStatus(String pNom, int pStatus, int pNumPage, int pPageSize);
	
	
	// ******* contract to commerce methods **********************
	public ContractToCommerce saveContractToCommerce(ContractToCommerce pContract);
	
	public ContractToCommerce updateContract(ContractToCommerce pContract);
	
	public void deleteOneContractToCommerce(ContractToCommerce pContract);

	public boolean userIdExists(String pUserId);
}
