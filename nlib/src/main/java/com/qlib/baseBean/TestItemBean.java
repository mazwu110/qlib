package com.qlib.baseBean;

import java.io.Serializable;

public class TestItemBean implements Serializable {
	@Override
	public String toString() {
		return "AddItemBean [title=" + title + ", images_id=" + images_id
				+ ", open_type=" + open_type + ", open_content=" + open_content
				+ ", images_url=" + images_url + ", api_url=" + api_url
				+ ", type=" + type + ", api_params=" + api_params + "]";
	}

	public TestItemBean() {

	}

	public TestItemBean(String title, String images_url) {
		this.title = title;
		this.images_url = images_url;
	}

	private static final long serialVersionUID = 1L;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImages_id() {
		return images_id;
	}

	public void setImages_id(String images_id) {
		this.images_id = images_id;
	}

	public String getOpen_type() {
		return open_type;
	}

	public void setOpen_type(String open_type) {
		this.open_type = open_type;
	}

	public String getOpen_content() {
		return open_content;
	}

	public void setOpen_content(String open_content) {
		this.open_content = open_content;
	}

	public String getImages_url() {
		return images_url;
	}

	public void setImages_url(String images_url) {
		this.images_url = images_url;
	}

	public String getApi_url() {
		return api_url;
	}

	public void setApi_url(String api_url) {
		this.api_url = api_url;
	}

	public String getApi_params() {
		return api_params;
	}

	public void setApi_params(String api_params) {
		this.api_params = api_params;
	}

	public String getShare_image() {
		return share_image;
	}

	public void setShare_image(String share_image) {
		this.share_image = share_image;
	}

	private String title;
	private String type;
	private String images_id;
	private String open_type;
	private String open_content;
	private String images_url;
	private String api_url;
	private String api_params;
	private String share_image;
}
