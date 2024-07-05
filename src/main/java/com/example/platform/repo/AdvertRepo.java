package com.example.platform.repo;

import com.example.platform.model.Advert;
import com.example.platform.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdvertRepo extends JpaRepository<Advert,Long> {
    //public List<Advert> getAdverts
    public Advert findAdvertByAdvertId(long advertId);



}
