package service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import model.Frase;
import repository.FraseRepository;

public class FraseService {
	
	@Autowired
	private FraseRepository fraseRepository;
	
	public Frase crearFrase(Frase frase) {
		return fraseRepository.save(frase);
	}
	
	public List<Frase> obtenerFrasesByUsuario(Long usuarioId){
		return fraseRepository.findByUsuario_id(usuarioId);
	}

}

