package ru.dovakun.postapi.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dovakun.postapi.model.PostOffice;

public interface PostOfficeRepo extends JpaRepository<PostOffice,String> {
}
