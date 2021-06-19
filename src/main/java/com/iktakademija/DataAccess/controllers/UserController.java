package com.iktakademija.DataAccess.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iktakademija.DataAccess.entities.AddressEntity;
import com.iktakademija.DataAccess.entities.UserEntity;
import com.iktakademija.DataAccess.repositories.AddressRepository;
import com.iktakademija.DataAccess.repositories.UserRepository;

import static net.andreinc.mockneat.unit.user.Names.names;
import static net.andreinc.mockneat.unit.user.Emails.emails;
import net.andreinc.mockneat.types.enums.NameType;
import net.andreinc.mockneat.unit.time.LocalDates;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AddressRepository addressRepository;
	
	@RequestMapping(method = RequestMethod.POST)
	public UserEntity saveNewUser(@RequestParam String name, @RequestParam String email)
	{
		UserEntity newUser = new UserEntity();
		newUser.setEmail(email);
		newUser.setName(name);		
		
		userRepository.save(newUser); 
		return newUser;
	}
	// popraviti za sve atribute
	
	@RequestMapping(method = RequestMethod.GET)
	public List<UserEntity> getAll()
	{
		return (List<UserEntity>)userRepository.findAll();
	}
	
	@RequestMapping(path = "/{id}/address", method = RequestMethod.PUT)
	public UserEntity addAdressToUser (@PathVariable Integer id, @RequestParam Integer address)
	{
		UserEntity userEntity = userRepository.findById(id).get();
		AddressEntity adressEntity = addressRepository.findById(address).get();		
		userEntity.setAddress(adressEntity);		
		return userRepository.save(userEntity);
	}
	
	//  Uraditi sledeće stavke:
	//  1.1 popuniti bazu podataka sa podacima o deset osoba
	@RequestMapping(method = RequestMethod.POST, path = "/add10Users")
	public void add10Users()
	{
		/*
		 *  Dodati u POM:
		 *  <dependency>
		 *    <groupId>net.andreinc</groupId>
		 *    <artifactId>mockneat</artifactId>
		 *    <version>0.4.7</version>
		 *  </dependency>
		 * 
		 *  Dodati 
		 *  import static net.andreinc.mockneat.unit.user.Names.*;
		 *  import net.andreinc.mockneat.types.enums.NameType;
		 */		
		Random rnd = new Random();
		UserEntity user;
		for (int i = 0; i < 10; i++) {
			user = new UserEntity(
							names().type(NameType.FIRST_NAME_MALE).get(),
							emails().domains("startup.io", "gmail.ac.rs").get());			
			user.setJMBG(String.format("%5d%d5%4d", rnd.nextInt(99999), rnd.nextInt(99999), rnd.nextInt(9999)));
			user.setBrTelefona(String.format("063/%6d", rnd.nextInt(9999999)));
			user.setBrLicneKarte(String.format("%8d", rnd.nextInt(999999999)));
			user.setDatumRodenja(LocalDates.localDates().between(LocalDate.of(1918, 1, 1), LocalDate.of(1982, 12, 31)).get());
			if (addressRepository != null && addressRepository.count()>0)
			{
				Integer j = new Random().nextInt((int)addressRepository.count()-1);
				if (addressRepository.existsById(j)) 
					user.setAddress(addressRepository.findById(j).get());
			}
			userRepository.save(user);
			
		}		
	}	
	// Dodati upis broja preko parametra
	
	
	//  1.2 u potpunosti omogućiti rad sa korisnicima
	//  vraćanje korisnika po ID
	//  ažuriranje korisnika
	//  brisanje korisnika
	@RequestMapping(method = RequestMethod.GET, path = "/{id}")
	public UserEntity getUserById(@PathVariable(name = "id") Integer id) {
		
		if (id == null)
			return null;
		return userRepository.findById(id).get();
	}
	
	@RequestMapping(method = RequestMethod.PUT, path = "/{id}")
	public UserEntity setUserById(@PathVariable(name = "id") Integer id, 
			@RequestParam(name = "Name") String name,
			@RequestParam(name = "eMail") String email, 
			@RequestParam(name = "Id") Integer addressId) {
		
		// Get user by id
		UserEntity user = userRepository.findById(id).orElse(null);
		if (user == null) return null;
		
		// Try to get address
		if (addressId == null) return null;
		AddressEntity address = addressRepository.findById(addressId).orElse(null);
		if (address == null) return null;
		
		// Check other inputs
		if (name == null) return null;
		if (email == null) return null;

		// Commit changes
		user.setEmail(email);
		user.setName(name);
		user.setAddress(address);
		return userRepository.save(user);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
	public UserEntity removeById(@PathVariable(name = "id") Integer id) {
		
		// Get user by id
		UserEntity user = userRepository.findById(id).orElse(null);
		if (user == null) return null;	
		
		// Remove user if exists
		userRepository.delete(user);
		return user;
	}	
	
	//  1.3 omogućiti pronalaženje korisnika po email adresi putanja /by-email
	@RequestMapping(method = RequestMethod.GET, path = "/by-email")
	public UserEntity getUserByEmailAddress(@RequestParam String email) {
		
		return userRepository.findByEmail(email);
	}
	
	//	1.4 omogućiti pronalaženje korisnika po imenu
	//	vraćanje korisnika sortiranih po rastućoj vrednosti emaila putanja /by-name
	@RequestMapping(method = RequestMethod.GET, path = "/by-name")
	public List<UserEntity> getAllUserByNameSorted(@RequestParam String name) {
		
		return userRepository.findAllByNameOrderByEmailAsc(name);
	}		
	
	//  2.2 omogućiti pronalaženje korisnika po datumu rođenja sortiranih u rastućem redosledu imena
	//  putanja /by-dob
	@RequestMapping(method = RequestMethod.GET, path = "/by-dob")
	public List<UserEntity> getAllUserByDateSorted(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data1,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data2) {
		
		 return userRepository.findAllBydatumRodenjaBetweenOrderByNameAsc(data1,data2);	
	}
		
	//  2.3* omogućiti pronalaženje različitih imena korisnika po prvom slovu imena
	//  putanja /by-name-first-letter	
	@RequestMapping(method = RequestMethod.GET, path = "/by-name-first-letter")
	public List<UserEntity> getByNameFirstLatter(@RequestParam(name = "a") String letter) {
		
		if (letter == null || letter.length() == 0) return null;
		return userRepository.findDistinctByNameStartingWith(letter.charAt(0));		
	}	
	// popraviti queri parametar name. vratiti listu imena
	
	//  zad 2.2 dodati REST entpoint u UserController koji omogućava
	//  uklanjanje adrese iz entiteta korisnika
	@RequestMapping(method = RequestMethod.PUT, path = "/remove-address/{id}")
	public UserEntity removeAddressById(@PathVariable(name = "id") Integer id ) {
		
		// Get user by id
		UserEntity user = userRepository.findById(id).orElse(null);
		if (user == null) return null;	
		
		user.setAddress(null);
		userRepository.save(user);
		return user;
	} // mozda pathvariable mesto query
		
}
