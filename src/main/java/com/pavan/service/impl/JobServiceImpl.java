package com.pavan.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pavan.beans.ApiResponse;
import com.pavan.beans.JobBean;
import com.pavan.modal.Job;
import com.pavan.modal.User;
import com.pavan.repository.JobRespository;
import com.pavan.repository.UserRespository;
import com.pavan.service.JobService;
import com.pavan.util.DateUtil;
import com.pavan.util.Utility;

@Service
public class JobServiceImpl implements JobService {

	@Autowired
	JobRespository jobsRepository;

	@Autowired
	UserRespository userRepository;

	private String message = "";

	@Override
	public void saveJob(User currentUser, JobBean jobBean) throws Exception {

		if (!validData(jobBean)) {
			throw new Exception(message);
		}

		Job job = new Job();
		if (jobBean.getId() != null) {
			job.setId(jobBean.getId());
		}
		job.setUser(currentUser);
		job.setCompany(jobBean.getCompany());
		job.setDesignation(jobBean.getDesignation());
		job.setCurrent(jobBean.getCurrent() == null ? false : jobBean.getCurrent());
		try {
			job.setDoj(jobBean.getDoj());
			job.setDol(jobBean.getDol());

		} catch (Exception e) {
			message = e.getMessage();
			throw new Exception(message);
		}

		jobsRepository.save(job);

	}

	private boolean validData(JobBean bean) {

		if (Utility.isEmpty(bean.getCompany())) {
			message = "Please Enter Company Name";
			return false;
		}

		if (Utility.isEmpty(bean.getDesignation())) {
			message = "Please Enter Designation";
			return false;
		}

		if (Utility.isEmpty(bean.getDoj())) {
			message = "Please Select DOJ";
			return false;
		}

		return true;
	}

	@Override
	public ApiResponse getJobs(User currentUser) {

		Map<String, Object> data = new LinkedHashMap<>();

		List<Job> jobs = jobsRepository.findByUserOrderByDojDesc(currentUser);

		if (Utility.isEmpty(jobs)) {
			return new ApiResponse(HttpStatus.NOT_FOUND, "No data found", null);
		}

		List<JobBean> jobBeans = new ArrayList<>();

		for (Job job : jobs) {
			JobBean jobBean = toBean(job);
			jobBeans.add(jobBean);
		}

		String totalExperience = "";

		if (!Utility.isEmpty(jobs)) {
			LocalDate firstDoj = jobs.get(jobs.size() - 1).getDoj().toInstant().atZone(ZoneId.systemDefault())
					.toLocalDate();
			totalExperience = DateUtil.getDateDifference(firstDoj, LocalDate.now());
		}

		data.put("jobs", jobBeans);
		data.put("totalExperience", totalExperience);

		return new ApiResponse(HttpStatus.OK, null, data);
	}

	private JobBean toBean(Job job) {
		LocalDate now = LocalDate.now();

		JobBean jobBean = new JobBean();

		jobBean.setId(job.getId());
		jobBean.setCompany(job.getCompany());
		jobBean.setDesignation(job.getDesignation());
		jobBean.setCurrent(job.getCurrent() == null ? false : job.getCurrent());
		jobBean.setDoj(job.getDoj());
		jobBean.setDol(job.getDol());

		LocalDate date1 = job.getDoj().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		if (job.getDol() != null) {
			now = job.getDol().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}

		jobBean.setExperience(DateUtil.getDateDifference(date1, now));
		return jobBean;
	}

	@Override
	public ApiResponse getJob(Long id) {
		if (id == null || id == 0) {
			return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "No data found", null);
		} else {

			Optional<Job> jobOp = jobsRepository.findById(id);
			if (jobOp.isPresent()) {
				JobBean jobBean = toBean(jobOp.get());
				return new ApiResponse(HttpStatus.OK, null, jobBean);
			} else {
				return new ApiResponse(HttpStatus.NO_CONTENT, "No data found", null);
			}
		}
	}

	@Override
	public ApiResponse deleteJob(Long id) {
		if (getJob(id).getData() == null) {
			return new ApiResponse(HttpStatus.NO_CONTENT, "No data found", null);
		} else {
			jobsRepository.deleteById(id);
			message = "Job deleted successfully";
			return new ApiResponse(HttpStatus.OK, message, null);
		}
	}

	@Override
	public ApiResponse deleteJobs(User currentUser) {
		if (currentUser != null) {
			jobsRepository.deleteByUser(currentUser);
			message = "Hi " + currentUser.getName() + ", Jobs deleted successfully";
		} else {
			jobsRepository.deleteAll();
			message = "Jobs deleted successfully";
		}
		return new ApiResponse(HttpStatus.OK, message, null);
	}

	@Override
	public void bulkUpload(List<List<Object>> data) {
		List<Job> jobs = new ArrayList<>();

		for (List<Object> rowData : data.subList(1, data.size())) {
			Job job = new Job();

			job.setUser(userRepository.getOne(Double.valueOf(rowData.get(1) + "").longValue()));
			job.setCompany((String) rowData.get(2));
			job.setDesignation((String) rowData.get(3));
			job.setDoj(DateUtil.objToDate(rowData.get(4)));
			job.setDol(DateUtil.objToDate(rowData.get(5)));
			
			if(rowData.get(6)!=null && (rowData.get(6) instanceof Boolean)) {
				job.setCurrent((Boolean) rowData.get(6));
			}else {
				job.setCurrent(false);
			}
			
			jobs.add(job);
		}

		jobsRepository.deleteAll();
		jobsRepository.saveAll(jobs);
	}

}
