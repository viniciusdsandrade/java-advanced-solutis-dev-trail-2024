package com.mightyjava.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.mightyjava.model.Address;
import com.mightyjava.service.AddressService;
import com.mightyjava.service.UserService;
import com.mightyjava.utils.ErrorUtils;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("/address")
public class AddressController {

	private final AddressService addressService;
	private final UserService userService;

	@Autowired
	public AddressController(AddressService addressService, UserService userService) {
		this.addressService = addressService;
		this.userService = userService;
	}

	// ---- Endpoints de Visão (MVC) ----

	/**
	 * Exibe o formulário para adicionar um novo endereço.
	 */
	@GetMapping("/form")
	public String addressForm(Model model) {
		model.addAttribute("isNew", true);
		model.addAttribute("addressForm", new Address());
		model.addAttribute("users", userService.userList());
		return "address/form";
	}

	/**
	 * Exibe o formulário para editar um endereço existente.
	 */
	@GetMapping("/edit/{id}")
	public String addressEdit(@PathVariable Long id, Model model) {
		Address address = addressService.findOne(id);
		if (address != null) {
			model.addAttribute("isNew", false);
			model.addAttribute("addressForm", address);
			model.addAttribute("users", userService.userList());
			return "address/form";
		} else {
			// Trate o caso onde o endereço não é encontrado
			model.addAttribute("message", "Address not found.");
			return "error/404"; // Supondo que exista uma visão 404
		}
	}

	/**
	 * Exibe a lista de todos os endereços.
	 */
	@GetMapping("/list")
	public String addressList(Model model) {
		model.addAttribute("addresses", addressService.addressList());
		return "address/list";
	}

	/**
	 * Obtém um endereço específico por ID.
	 * Retorna o endereço como JSON.
	 */
	@GetMapping("/get/{id}")
	@ResponseBody
	public Address findOne(@PathVariable Long id) {
		return addressService.findOne(id);
	}

	// ---- Endpoints REST (JSON) ----

	/**
	 * Deleta um endereço específico por ID.
	 * Requer a autorização de ROLE_ADMIN.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/delete/{id}", produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public String addressDelete(@PathVariable Long id) {
		return addressService.deleteAddress(id);
	}

	/**
	 * Adiciona ou atualiza um endereço.
	 * Retorna a resposta como JSON.
	 */
	@PostMapping(value = "/add", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public String addressAdd(@Valid @RequestBody Address address, BindingResult result) {
		if(result.hasErrors()) {
			return ErrorUtils.customErrors(result.getAllErrors());
		} else {
			return addressService.addAddress(address);
		}
	}

}
