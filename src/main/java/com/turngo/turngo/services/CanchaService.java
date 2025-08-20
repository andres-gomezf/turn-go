package com.turngo.turngo.services;

import com.turngo.turngo.entities.Cancha;
import com.turngo.turngo.entities.Cliente;
import com.turngo.turngo.repositories.CanchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CanchaService {

    @Autowired
    private CanchaRepository canchaRepository;

    public List<Cancha> findAll() {
        return canchaRepository.findAll();
    }

    public Optional<Cancha> findById(Long id) {
        return canchaRepository.findById(id);
    }

    public Cancha save(Cancha cancha) {
        return canchaRepository.save(cancha);
    }

    public void delete(Long id) {
        canchaRepository.deleteById(id);
    }
}
