package com.iktakademija.DataAccess.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import com.iktakademija.DataAccess.entities.AddressEntity;

@Service
public class AddressDAOImpl implements AddressDAO {

	@PersistenceContext()
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AddressEntity> findAddressesByUserName(String name) {
		
		// create SQL to invoke
		// radi se sa objektima i pisu se nazivi polja iz klasa		
		String sql = "SELECT a FROM AddressEntity AS a LEFT JOIN FETCH a.users AS u WHERE u.name = :name";
		// mesto ON postoji FETCH koji je lista veza
		// parametrizovani se koriste proti injectiona i 
		
		// SELECT * 
		// FROM AddressEntity AS a INNER JOIN UserEntyty AS u ON a.id=u.addressID 
		// WHERE u.name = :name
		
		// invoke SQL
		Query query = em.createQuery(sql);		
		query.setParameter("name", name);
		
		List<AddressEntity> retVal = new ArrayList<AddressEntity>();
		retVal = query.getResultList();
		
		// handle the return value of the SQL statement
		
		
		return retVal;
	}

}
