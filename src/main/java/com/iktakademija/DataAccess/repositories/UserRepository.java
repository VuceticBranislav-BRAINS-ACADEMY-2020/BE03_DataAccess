package com.iktakademija.DataAccess.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.DataAccess.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {

	UserEntity findByEmail(String email);	
	List<UserEntity> findAllByNameOrderByEmailAsc(String name);
	
	
	//List<UserEntity> findAllBydatumRodenjaOrderByNameAsc(LocalDate data);
	List<UserEntity> findAllBydatumRodenjaBetweenOrderByNameAsc(LocalDate data1, LocalDate date2);	
	
	Optional<UserEntity> findById(Integer id);
	
	//List<UserEntity> findByNameStartingWith(String data);
	List<UserEntity> findDistinctByNameStartingWith(Character letter);
	
	
	//@Query("select u from UserEntity as u where datumRodenja between ?1 and ?2")
	//@Query(value = "select * from User_Entity as u where u.dat_rod between ?1 and ?2", nativeQuery = true)
	//@Query("select u from UserEntity as u where datumRodenja between :status and ?2")	
	//List<UserEntity> test2(@Param("status")LocalDate data, LocalDate data2);
}
