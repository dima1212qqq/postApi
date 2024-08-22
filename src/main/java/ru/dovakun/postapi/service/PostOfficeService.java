package ru.dovakun.postapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.dovakun.postapi.model.PostOffice;
import ru.dovakun.postapi.repo.PostOfficeRepo;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostOfficeService{

    private final PostOfficeRepo postOfficeRepo;


    public List<PostOffice> getPostOffices(){
        return postOfficeRepo.findAll();
    }

    public PostOffice create(PostOffice postOffice) {
        return postOfficeRepo.save(postOffice);
    }


    public PostOffice delete(String id) {
        PostOffice postOffice = postOfficeRepo.findById(id).orElse(null);
        if (postOffice != null) {
            postOfficeRepo.delete(postOffice);
        }
        return postOffice;
    }

}
