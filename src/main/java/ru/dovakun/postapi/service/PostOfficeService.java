package ru.dovakun.postapi.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.dovakun.postapi.model.PostOffice;
import ru.dovakun.postapi.repo.PostOfficeRepo;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostOfficeService{

    private final PostOfficeRepo postOfficeRepo;


    public List<PostOffice> getPostOffices(){
        return postOfficeRepo.findAll();
    }

    public PostOffice save(PostOffice postOffice) {
        if (postOffice == null) {
            throw new IllegalArgumentException("PostOffice must not be null");
        }
        return postOfficeRepo.save(postOffice);
    }
    public PostOffice findByIndex(String index) {
        Optional<PostOffice> optionalPostOffice = postOfficeRepo.findById(index);
        return optionalPostOffice.orElse(null);
    }

    public PostOffice delete(String id) {
        PostOffice postOffice = postOfficeRepo.findById(id).orElse(null);
        if (postOffice != null) {
            postOfficeRepo.delete(postOffice);
        }
        return postOffice;
    }

}
