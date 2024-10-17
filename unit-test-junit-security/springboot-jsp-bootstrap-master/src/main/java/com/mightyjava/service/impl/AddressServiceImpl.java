package com.mightyjava.service.impl;

import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mightyjava.model.Address;
import com.mightyjava.model.Users;
import com.mightyjava.repository.AddressRepository;
import com.mightyjava.repository.UserRepository;
import com.mightyjava.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {
	
	private final AddressRepository addressRepository;
	private final UserRepository userRepository;

	@Autowired
	public AddressServiceImpl(AddressRepository addressRepository,
							  UserRepository userRepository) {
		this.addressRepository = addressRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<Address> addressList() {
		return addressRepository.findAll();
	}

	@Override
	public Address findOne(Long id) {
		return addressRepository.findOne(id);
	}

	@Override
	public String addAddress(Address address) {
		String message;
		JSONObject jsonObject = new JSONObject();
		try {
			if (address.getId() == null) {
				message = "Added";
			} else {
				message = "Updated";
			}

			// Verifique se o usuário está definido no objeto Address
			Users user = address.getUser();
			if (user != null) {
				// Opcional: Verifique se o usuário existe no banco de dados
				Users existingUser = userRepository.findOne(user.getId());
				if (existingUser != null) {
					address.setUser(existingUser);
				} else {
					jsonObject.put("status", "error");
					jsonObject.put("message", "User not found.");
					return jsonObject.toString();
				}
			} else {
				jsonObject.put("status", "error");
				jsonObject.put("message", "User information is missing.");
				return jsonObject.toString();
			}

			addressRepository.save(address);
			jsonObject.put("status", "success");
			jsonObject.put("title", message + " Confirmation");
			jsonObject.put("message", "Address for " + user.getFullName() + " " + message + " successfully.");
		} catch (JSONException e) {
			System.err.println(e.getMessage());
			try {
				jsonObject.put("status", "error");
				jsonObject.put("message", "An error occurred while processing the request.");
			} catch (JSONException jsonException) {
				System.err.println(jsonException.getMessage());
			}
		}
		return jsonObject.toString();
	}


	@Override
	public String deleteAddress(Long id) {
		JSONObject jsonObject = new JSONObject();
		try {
			addressRepository.delete(id);
			jsonObject.put("message", "Address Deleted Successfully");
		} catch (JSONException e) {
			System.err.println(e.getMessage());
		}
		return jsonObject.toString();
	}
}
