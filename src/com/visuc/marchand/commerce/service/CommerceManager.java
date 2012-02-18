package com.visuc.marchand.commerce.service;

import java.util.List;

import com.visuc.marchand.commerce.dao.CommerceDao;
import com.visuc.marchand.commerce.entite.Commerce;
import com.visuc.marchand.commerce.entite.ContractToCommerce;

public class CommerceManager implements ICommerceManager {

	CommerceDao commerceDao;
	
	public void setCommerceDao(CommerceDao commerceDao) {
		this.commerceDao = commerceDao;
	}

	@Override
	public void deleteOneCommerce(Commerce pCommerce) {
		System.out.println("    -DEBUG-   CommerceManager.deleteOne " + pCommerce.getId());
		this.commerceDao.deleteOne(pCommerce.getId());

	}

	@Override
	public Commerce findCommerceById(Long pId) {
		System.out.println("    -DEBUG-   CommerceManager.findCommerceById " + pId);
		return this.commerceDao.findById(pId);
	}

	@Override
	public boolean userIdExists(String pUserId) {
		System.out.println("    -DEBUG-   CommerceManager.userIdExists " + pUserId);
		return this.commerceDao.checkUserIdExists(pUserId);
	}


	@Override
	public List<Commerce> findCommerceByNameAndStatus(String pNom, int pStatus, int pNumPage, int pPageSize) {
		System.out.println("    -DEBUG-   CommerceManager.findCommerceByNameAndStatus "
				+ pNom + ", " + pStatus);
		return this.commerceDao.findByNameAndStatus(pNom, pStatus, pNumPage, pPageSize);
	}

	@Override
	public List<Commerce> findCommerceByName(String pNom, int pNumPage, int pPageSize) {
		System.out.println("    -DEBUG-   CommerceManager.findByName " + pNom);
		return this.commerceDao.findByName(pNom, pNumPage, pPageSize);
	}
	
	@Override
	public List<Commerce> findCommerceByNameAndType(Commerce pCommerce, int pNumPage, int pPageSize) {
		System.out.println("    -DEBUG-   CommerceManager.findByNameAndType ");
		return this.commerceDao.findByNameAndType(pCommerce, pNumPage, pPageSize);
	}

	@Override
	public Commerce saveCommerce(Commerce pCommerce) {
		System.out.println("    -DEBUG-   CommerceManager.save " + pCommerce);
		return commerceDao.saveOne(pCommerce);
	}

	@Override
	public Commerce updateCommerce(Commerce pCommerce) {
		System.out.println("    -DEBUG-   CommerceManager.update " + pCommerce);
		return commerceDao.updateOne(pCommerce);
	}

	
	//----------------- contract to commerce ------------------------
	
	@Override
	public void deleteOneContractToCommerce(
			ContractToCommerce pContract) {
		System.out.println("    -DEBUG-   CommerceManager.deleteOneContractToCommerce " 
				+ pContract.getIdContract() + ", " + pContract.getComissionType() 
				+ ", " + pContract.getComissionAmount());
		this.commerceDao.deleteOneContractToCommerce(pContract.getIdContract());
	}

	@Override
	public ContractToCommerce saveContractToCommerce(
			ContractToCommerce pContract) {
		System.out.println("    -DEBUG-   CommerceManager.saveContractToCommerce "
				+ pContract.getIdContract() + ", " + pContract.getComissionType() 
				+ ", " + pContract.getComissionAmount());
		return this.commerceDao.saveOneContractToCommerce(pContract);
	}

	@Override
	public ContractToCommerce updateContract(ContractToCommerce pContract) {
		System.out.println("    -DEBUG-   CommerceManager.updateContract "
				+ pContract.getIdContract() + ", " + pContract.getComissionType() 
				+ ", " + pContract.getComissionAmount());
		return this.commerceDao.updateOneContractToCommerce(pContract);
	}
	
}
