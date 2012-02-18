package com.visuc.referentiel.contract.service;

import java.util.List;

import com.visuc.referentiel.contract.dao.ContractDao;
import com.visuc.referentiel.contract.entite.Contract;
import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;

public class ContractManager implements IContractManager {

	ContractDao contractDao;

	public void setContractDao(ContractDao pContractDao) {
		this.contractDao = pContractDao;
	}

// ***** Implementation CRUD couche service *******	
	
	@Override
	public Contract save(Contract d) {
		System.out.println("    -DEBUG-   ContractManager.save "+d);
		return contractDao.saveOne(d);
	}

	@Override
	public Contract update(Contract pContract) {
		System.out.println("    -DEBUG-   ContractManager.update "+pContract);
		return contractDao.updateOne(pContract);
	}

	@Override
	public void deleteOne(Contract pContract) {
		System.out.println("    -DEBUG-   ContractManager.deleteOne " + pContract.getIdContract());
		contractDao.deleteOne(pContract.getIdContract());
	}
	
// ***** Implementation service de gestion des criteres de recherche *******
	
	@Override
	public Contract findById(Long pId){
		System.out.println("    -DEBUG service-   ContractManager.findById "+pId);
		return contractDao.findById(pId);
	}
	
	@Override
	public List<Contract> findContractListByCommerceType(TypeCommerce pTypeCommerce, int pNumPage, int pPageSize) {
		System.out.println("-->   ContractManager.findContractListByCommerceType() " + pTypeCommerce.getTypeCommerceName());
		List<Contract> list = contractDao.findContractListByCommerceType(pTypeCommerce, pNumPage, pPageSize);
		System.out.println("<-- ContractManager.findContractListByCommerceType() " + list.size());
		return list;
	}

	@Override
	public Contract findSingleContractByCommerceType(TypeCommerce pTypeCommerce, int pNumPage, int pPageSize) {
		System.out.println("    -DEBUG service-   ContractManager.findByName " + pTypeCommerce.getTypeCommerceName());
		return contractDao.findSingleContractByCommerceType(pTypeCommerce, pNumPage, pPageSize);
	}


// ******** Couche de service a rendre a l'action EditerContractAction

	
}