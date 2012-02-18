package com.visuc.common.dao;

import com.visuc.common.entity.Address;


public interface IAddressDao {

	Address saveOne(Address pAddress);
	Address updateOne(Address p);
	void deleteOne(Long aId);
	Address findById(Long aId);

}
