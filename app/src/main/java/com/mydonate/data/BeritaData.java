package com.mydonate.data;

public class BeritaData {
  String id_pengurus, created_at, title, detail, image;

  public BeritaData(String id_pengurus, String created_at, String title, String detail, String image) {
    this.id_pengurus = id_pengurus;
    this.created_at = created_at;
    this.title = title;
    this.detail = detail;
    this.image = image;
  }

  public String getId_pengurus() {
    return id_pengurus;
  }

  public void setId_pengurus(String id_pengurus) {
    this.id_pengurus = id_pengurus;
  }

  public String getCreated_at() {
    return created_at;
  }

  public void setCreated_at(String created_at) {
    this.created_at = created_at;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }
}
