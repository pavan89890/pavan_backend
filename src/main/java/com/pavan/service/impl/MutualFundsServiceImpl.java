package com.pavan.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pavan.beans.ApiResponse;
import com.pavan.beans.MutualFundBean;
import com.pavan.modal.MutualFund;
import com.pavan.modal.User;
import com.pavan.repository.MfRespository;
import com.pavan.service.MutualFundService;
import com.pavan.util.DateUtil;
import com.pavan.util.Utility;

@Service
public class MutualFundsServiceImpl implements MutualFundService {

	@Autowired
	MfRespository mfRepository;

	private String message = "";

	@Override
	public void saveMutualFund(MutualFundBean mfBean, User currentUser) throws Exception {

		if (!validData(mfBean)) {
			throw new Exception(message);
		}

		MutualFund mf = new MutualFund();
		if (mfBean.getId() != null) {
			mf.setId(mfBean.getId());
		}
		mf.setName(mfBean.getName());
		mf.setDesc(mfBean.getDesc());
		mf.setInvestedAmount(mfBean.getInvestedAmount());
		mf.setCurrentAmount(mfBean.getCurrentAmount());
		mf.setUser(currentUser);

		Date depositedOn = null;

		if (!Utility.isEmpty(mfBean.getDepositedOnStr())) {
			try {
				depositedOn = DateUtil.yyyy_MM_dd.parse(mfBean.getDepositedOnStr());
			} catch (ParseException e) {
				message = e.getMessage();
				throw new Exception(message);
			}
		}

		mf.setDepositedOn(depositedOn);

		mfRepository.save(mf);

	}

	private boolean validData(MutualFundBean bean) {

		if (Utility.isEmpty(bean.getName())) {
			message = "Please Enter Name";
			return false;
		}

		if (Utility.isEmpty(bean.getDesc())) {
			message = "Please Enter Description";
			return false;
		}

		if (Utility.isEmpty(bean.getInvestedAmount())) {
			message = "Please Enter Invested Amount";
			return false;
		}

		if (Utility.isEmpty(bean.getCurrentAmount())) {
			message = "Please Enter Current Amount";
			return false;
		}

		if (Utility.isEmpty(bean.getCurrentAmount())) {
			message = "Please Enter Current Amount";
			return false;
		}

		return true;
	}

	@Override
	public ApiResponse getMutualFunds(User currentUser) {

		Map<String, Object> data = new LinkedHashMap<>();

		List<MutualFund> mfs = null;
		if (currentUser != null) {
			mfs = mfRepository.findByUserOrderByName(currentUser);
		}

		List<MutualFundBean> mfBeans = new ArrayList<>();

		if (Utility.isEmpty(mfs)) {
			return new ApiResponse(HttpStatus.NOT_FOUND, "No data found", null);
		}

		for (MutualFund mf : mfs) {
			MutualFundBean mfBean = toBean(mf);
			mfBeans.add(mfBean);
		}

		Float totalInvested = mfRepository.getTotalInvested(currentUser);

		Float totalCurrent = mfRepository.getTotalCurrent(currentUser);

		Float totalProfitprofitOrLossAmount = totalCurrent - totalInvested;
		
		Float totalProfitprofitOrLossPerc = (totalProfitprofitOrLossAmount/totalInvested)*100;

		data.put("mfs", mfBeans);
		
		data.put("totalInvested", totalInvested);
		data.put("totalCurrent", totalCurrent);
		
		data.put("totalProfitprofitOrLossAmount", totalProfitprofitOrLossAmount);
		data.put("totalProfitprofitOrLossPerc", totalProfitprofitOrLossPerc);

		return new ApiResponse(HttpStatus.OK, null, data);
	}

	private MutualFundBean toBean(MutualFund mf) {
		MutualFundBean mfBean = new MutualFundBean();

		mfBean.setId(mf.getId());
		mfBean.setName(mf.getName());
		mfBean.setDesc(mf.getDesc());
		mfBean.setInvestedAmount(mf.getInvestedAmount());
		mfBean.setCurrentAmount(mf.getCurrentAmount());

		if (mf.getDepositedOn() != null) {
			mfBean.setDepositedOnStr(DateUtil.yyyy_MM_dd.format(mf.getDepositedOn()));
		}

		mfBean.setProfitOrLossAmount(mfBean.getCurrentAmount() - mfBean.getInvestedAmount());
		mfBean.setProfitOrLossPerc((mfBean.getProfitOrLossAmount() / mfBean.getInvestedAmount()) * 100);
		return mfBean;
	}

	@Override
	public ApiResponse getMutualFund(Long id) {
		if (id == null || id == 0) {
			return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "No data found", null);
		} else {

			Optional<MutualFund> mfOp = mfRepository.findById(id);
			if (mfOp.isPresent()) {
				MutualFundBean fd = toBean(mfOp.get());
				return new ApiResponse(HttpStatus.OK, null, fd);
			} else {
				return new ApiResponse(HttpStatus.NO_CONTENT, "No data found", null);
			}
		}
	}

	@Override
	public ApiResponse deleteMutualFund(Long id) {
		if (getMutualFund(id).getData() == null) {
			return new ApiResponse(HttpStatus.NO_CONTENT, "No data found", null);
		} else {
			mfRepository.deleteById(id);
			message = "Mutual Fund deleted successfully";
			return new ApiResponse(HttpStatus.OK, message, null);
		}
	}

	@Override
	public ApiResponse deleteMutualFunds(User currentUser) {
		if (currentUser != null) {
			mfRepository.deleteByUser(currentUser);
			message = "Hi " + currentUser.getName() + ", Mutual Funds deleted successfully";
		} else {
			mfRepository.deleteAll();
			message = "Mutual Funds deleted successfully";
		}
		return new ApiResponse(HttpStatus.OK, message, null);
	}

}