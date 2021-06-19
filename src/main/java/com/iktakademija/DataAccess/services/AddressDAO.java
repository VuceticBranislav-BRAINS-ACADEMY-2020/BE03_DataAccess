package com.iktakademija.DataAccess.services;

import java.util.List;

import com.iktakademija.DataAccess.entities.AddressEntity;

public interface AddressDAO {

	public List<AddressEntity> findAddressesByUserName(String name);
}
