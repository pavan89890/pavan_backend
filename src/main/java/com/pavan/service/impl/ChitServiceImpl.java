package com.pavan.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pavan.beans.ApiResponse;
import com.pavan.modal.Chit;
import com.pavan.repository.ChitRespository;
import com.pavan.service.ChitService;

@Service
public class ChitServiceImpl implements ChitService {

	@Autowired
	ChitRespository chitRepository;

	private String message = "";

	@Override
	public ApiResponse getChits() {
		if (chitRepository.findAll() == null) {
			return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "No data found", null);
		}
		return new ApiResponse(HttpStatus.OK, null, chitRepository.findAll());
	}

	@Override
	public ApiResponse saveChit(Chit chit) {
		Chit c = chitRepository.findByMonthAndYear(chit.getMonth(),chit.getYear());

		if (c != null) {
			if ((chit.getId() == null) || (chit.getId() != c.getId())) {
				return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Chit For Month and Year combination Already Exists", null);
			}
		}

		if (chit.getId() == null || chit.getId() == 0) {
			message = "Chit saved successfully";
		} else {
			message = "Chit updated successfully";
		}
		chitRepository.save(chit);

		return new ApiResponse(HttpStatus.OK, message, null);
	}

	@Override
	public ApiResponse getChit(Long id) {
		if (id == null || id == 0) {
			return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "No data found", null);
		} else {

			Optional<Chit> chitOp = chitRepository.findById(id);
			if (chitOp.isPresent()) {
				Chit chit = chitOp.get();
				return new ApiResponse(HttpStatus.OK, null, chit);
			} else {
				return new ApiResponse(HttpStatus.NO_CONTENT, "No data found", null);
			}
		}
	}

	@Override
	public ApiResponse deleteChit(Long id) {
		if (getChit(id).getData() == null) {
			return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "No data found", null);
		} else {
			chitRepository.delete((Chit) getChit(id).getData());
			message = "Chit deleted successfully";
			return new ApiResponse(HttpStatus.OK, message, null);
		}
	}

}