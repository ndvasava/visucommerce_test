package com.visuc.common.service;

import com.visuc.common.dao.AddressDao;
import com.visuc.common.entity.Address;

public class AddressManager implements IAddressManager {

	AddressDao addressDao;
	
	public void setAddressDao(AddressDao adressDao) {
		this.addressDao = adressDao;
	}

	@Override
	public void deleteOneAddress(Address pAddress) {
		this.addressDao.deleteOne(pAddress.getId());
	}

	@Override
	public Address findAddressById(Long pId) {
		return this.addressDao.findById(pId);
	}

	@Override
	public Address saveAddress(Address pAddress) {
		return this.addressDao.saveOne(pAddress);
	}

	@Override
	public Address updateAddress(Address pAddress) {
		return this.addressDao.updateOne(pAddress);
	}

}
