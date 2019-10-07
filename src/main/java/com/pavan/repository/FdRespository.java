package com.pavan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pavan.modal.Fd;

@Repository
public interface FdRespository extends JpaRepository<Fd, Long> {

	@Query(value = "select sum(depAmount) from Fd")
	Float getTotalDeposited();
	
	@Query(value = "select sum(maturedAmount) from Fd")
	Float getTotalMatured();
	
	List<Fd> findAllByOrderByMaturedOnDesc();
}