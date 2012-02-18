package com.visuc.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


@Entity
@Table(name = "IMAGE")
@SuppressWarnings("serial")
public class Image implements Serializable {
	
	@Id
	@Column(name = "ID_IMAGE")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Lob
	@Column(name = "CONTENT")
	private byte[] content;
	
	@Column(name = "NOM_IMAGE")
	private String name;
	
	@Column(name = "MIMETYPE")
	private String mimeType;

	@Column(name = "TAILLE_IMAGE")
	private long length;
	
	// ********** Getter & Setter *********
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getName(){
		return this.name;
	}
	public void setName(String aName){
		name = aName;
	    int extDot = aName.lastIndexOf('.');
	    if(extDot > 0){
	        String extension = aName.substring(extDot +1);
	        if("bmp".equals(extension)){
	        	mimeType="image/bmp";
	        } else if("jpg".equals(extension)){
	        	mimeType="image/jpeg";
	        } else if("gif".equals(extension)){
	        	mimeType="image/gif";
	        } else if("png".equals(extension)){
	        	mimeType="image/png";
	        } else {
	        	mimeType = "image/unknown";
	        }
	    }
	}
	
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public Long getLength(){
		return this.length;
	}
	public void setLength(Long aLength){
		this.length = aLength;
	}
}
