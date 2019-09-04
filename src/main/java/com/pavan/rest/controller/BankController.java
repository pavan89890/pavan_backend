package com.pavan.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pavan.beans.ApiResponse;
import com.pavan.beans.BankBean;
import com.pavan.modal.Bank;
import com.pavan.service.BankService;

@RestController
@RequestMapping(path = "/banks/")
@CrossOrigin("*")
public class BankController {

	@Autowired
	private BankService bankService;

	@PostMapping
	public ApiResponse saveBank(@RequestBody(required = true) BankBean bankBean) {

		Bank bank = new Bank();
		if (bankBean.getId() != null) {
			bank.setId(bankBean.getId());
		}
		bank.setName(bankBean.getName());
		bank.setBalance(bankBean.getBalance());

		return bankService.saveBank(bank);

	}

	@GetMapping
	public ApiResponse banks() {
		return bankService.getBanks();
	}

	@GetMapping("/{id}")
	public ApiResponse getBank(@PathVariable(value = "id") Long id) {
		return bankService.getBank(id);
	}

	@DeleteMapping("/{id}")
	public ApiResponse deleteBank(@PathVariable(value = "id") Long id) {
		return bankService.deleteBank(id);
	}
}