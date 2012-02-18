package com.visuc.common.service;

import com.visuc.common.entity.Address;

public interface IAddressManager {

	public Address saveAddress(Address pAddress);
	public Address updateAddress(Address pAddress) ;
	public void deleteOneAddress(Address pAddress) ;
	

	// ***** data search methods *******
	
	public Address findAddressById(Long pId);

}
