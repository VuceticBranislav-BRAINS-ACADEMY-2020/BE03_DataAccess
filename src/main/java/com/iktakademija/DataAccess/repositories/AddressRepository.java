package com.iktakademija.DataAccess.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.DataAccess.entities.AddressEntity;

public interface AddressRepository extends CrudRepository<AddressEntity, Integer> { // Long je bolji od integera  u ovom slucaju

	AddressEntity findByCity(String city);
	List<AddressEntity> findAllByStreetOrderByCountryAsc(String address);

}
