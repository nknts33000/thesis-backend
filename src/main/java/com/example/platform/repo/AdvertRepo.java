package com.example.platform.repo;

import com.example.platform.model.Advert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertRepo extends JpaRepository<Advert,Long> {
    //public List<Advert> getAdverts
}
