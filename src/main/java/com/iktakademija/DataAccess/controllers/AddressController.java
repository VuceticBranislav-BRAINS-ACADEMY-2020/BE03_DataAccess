package com.iktakademija.DataAccess.controllers;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iktakademija.DataAccess.entities.AddressEntity;
import com.iktakademija.DataAccess.entities.UserEntity;
import com.iktakademija.DataAccess.repositories.AddressRepository;
import com.iktakademija.DataAccess.repositories.UserRepository;
import com.iktakademija.DataAccess.services.AddressDAO;

import static net.andreinc.mockneat.unit.user.Names.names;
import static net.andreinc.mockneat.unit.address.Cities.cities; 
import static net.andreinc.mockneat.unit.address.Countries.countries; 


@RestController
@RequestMapping(path = "/api/v1/address")
public class AddressController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private AddressDAO addressDAO;
	
	@RequestMapping(method = RequestMethod.GET)
	public List<AddressEntity> getAll() {
		return (List<AddressEntity>) addressRepository.findAll();
	}

	@RequestMapping(method = RequestMethod.POST)
	public AddressEntity saveNewAdress(@RequestParam String street, @RequestParam String city,
			@RequestParam String country) {

		AddressEntity adressEntity = new AddressEntity();
		adressEntity.setStreet(street);
		adressEntity.setCity(city);
		adressEntity.setCountry(country);
		return addressRepository.save(adressEntity);
	}

	// Uraditi sledeće stavke:
	// zad 1.1 popuniti bazu podataka sa podacima o deset adresa
	@RequestMapping(method = RequestMethod.POST, path = "/add10Address")
	public void add10Address() {
		/*
		 *  Dodati u POM:
		 *  <dependency>
		 *    <groupId>net.andreinc</groupId>
		 *    <artifactId>mockneat</artifactId>
		 *    <version>0.4.7</version>
		 *  </dependency>
		 * 
		 *  Dodati 
		 *  import static net.andreinc.mockneat.unit.user.Names.names;
		 *  import static net.andreinc.mockneat.unit.address.Cities.cities; 
		 *  import static net.andreinc.mockneat.unit.address.Countries.countries;
		 */	
		Random rnd = new Random();
		for (int i = 0; i < 10; i++) {
			addressRepository.save(
					new AddressEntity(
							names().get() + " Street " + rnd.nextInt(100),
							cities().capitalsEurope().get(),
							countries().get()));
		}
	}
	
	//  zad 1.2 u potpunosti omogućiti rad sa adresama
	//  vraćanje adrese po ID
	//  ažuriranje adrese
	//  brisanje adrese
	@RequestMapping(method = RequestMethod.GET, path = "/{id}")
	public AddressEntity getAddressById(@PathVariable(name = "id") Integer id) {
		
		if (id == null) return null;
		return addressRepository.findById(id).get();
	}
	
	@RequestMapping(method = RequestMethod.PUT, path = "/{id}")
	public AddressEntity setAddressById(@PathVariable(name = "id") Integer id, 
			@RequestParam(name = "Street") String street,
			@RequestParam(name = "City") String city, 
			@RequestParam(name = "Country") String country) {
		
		// Get address by id
		AddressEntity address = addressRepository.findById(id).orElse(null);
		if (address == null) return null;
		
		// Check inputs
		if (street == null || city == null || country == null) return null;

		// Commit changes
		address.setStreet(street);
		address.setCity(city);
		address.setCountry(country);
		return addressRepository.save(address);
	}
	// dodati i setovanje korisnika
	
	@RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
	public AddressEntity removeById(@PathVariable(name = "id") Integer id) {
		
		// Get address by id
		AddressEntity address = addressRepository.findById(id).orElse(null);
		if (address == null) return null;	
		
		// Remove address if exists
		addressRepository.delete(address);
		return address;
	}	
	
	// zad 1.3 omogućiti pronalaženje adrese po gradu putanja /by-city
	@RequestMapping(method = RequestMethod.GET, path = "/by-city")
	public AddressEntity getAddressByCity(@RequestParam(name = "by-city") String city) {
		
		return addressRepository.findByCity(city);
	}
	// unikatna adresa ?
	
	//	zad 1.4 omogućiti pronalaženje adrese po državi vraćanje adresa sortiranih 
	//  po rastućoj vrednosti države putanja /by-country
	@RequestMapping(method = RequestMethod.GET, path = "/by-country")
	public List<AddressEntity> getAllAddressByCountrySorted(@RequestParam String address) {
		
		return addressRepository.findAllByStreetOrderByCountryAsc(address);
	}	
	
	//  zad 2.2 u AddressController dodati REST entpoint-e za dodavanje i
	//  brisanje korisnika u adresama
	@RequestMapping(method = RequestMethod.DELETE, path = "/remove-address/{id}")
	public AddressEntity removeUsersFromAdress(@PathVariable(name = "id") Integer id) {
		
		// Get address by id
		AddressEntity address = addressRepository.findById(id).orElse(null);
		if (address == null) return null;	
		
		// Remove address if exists
		address.getUsers().clear();
		addressRepository.save(address);
		return address;
	}	
	// prvo razvezati
	
	@RequestMapping(method = RequestMethod.POST, path = "/add-address/{id}")
	public AddressEntity addUserToAddress(@PathVariable(name = "id") Integer id, 
			@RequestParam(name = "Name") String name,
			@RequestParam(name = "eMail") String email, 
			@RequestParam(name = "Id") Integer addressId) {
		
		// Get address by id
		AddressEntity address = addressRepository.findById(id).orElse(null);
		if (address == null) return null;	
				
		// Check other inputs
		if (name == null) return null;
		if (email == null) return null;

		// Commit changes
		UserEntity user = new UserEntity();
		user.setEmail(email);
		user.setName(name);
		//user.setAddress(address);
		address.getUsers().add(user);
		return addressRepository.save(address);
	}
	
	//  2.3* dodati REST entpoint u UserController koji omogućava prosleđivanje parametara za kreiranje korisnika i adrese
	//  kreira korisnika
	//  proverava postojanje adrese
	//  ukoliko adresa postoji u bazi podataka dodaje je korisniku
	//  ukoliko adresa ne postoji, kreira adresu i dodaje je korisniku
	@RequestMapping(method = RequestMethod.POST, path = "/create-all")
	public UserEntity addUserAndAddress( 
			@RequestParam(name = "Name") String name,
			@RequestParam(name = "eMail") String email, 
			@RequestParam(name = "Id") Integer addressId,
			@RequestParam(name = "Street") String street,
			@RequestParam(name = "City") String city, 
			@RequestParam(name = "Country") String country) {
		// bolje je proslediti preko tela zahteva
		// Create user
		UserEntity user = new UserEntity();
		user.setEmail(email);
		user.setName(name);
		
		
		// Get address by id
		AddressEntity address = addressRepository.findById(addressId).orElse(null);
		if (address == null) 
			{
			// Commit changes
			address = new AddressEntity();
			address.setStreet(street);
			address.setCity(city);
			address.setCountry(country);
			user.setAddress(address);
			addressRepository.save(address);
			}
		else
			{
			user.setAddress(address);
			}		
		
		address.getUsers().add(user);		

		return userRepository.save(user);
	}	

	@RequestMapping(method = RequestMethod.GET, path = "/user/{name}")
	public List<AddressEntity> findAddressByUserName(@PathVariable String name) {
		return addressDAO.findAddressesByUserName(name);
	}
}
