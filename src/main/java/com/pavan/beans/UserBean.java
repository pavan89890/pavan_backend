package com.pavan.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBean {
	private Long id;
	private String name;
	private String mobile;
	private String oriDobStr;
	private String cerDobStr;
	private String oriAgeStr;
	private String cerAgeStr;
}
