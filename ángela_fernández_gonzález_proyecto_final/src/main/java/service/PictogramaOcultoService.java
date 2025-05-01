package service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import model.Pictograma;
import model.PictogramaOculto;
import model.Usuario;
import repository.PictogramaOcultoRepository;

@Service
public class PictogramaOcultoService {

    @Autowired
    private PictogramaOcultoRepository repository;

    public boolean ocultarPictograma(Pictograma pictograma, Usuario usuario) {
        if (repository.existsByUsuario_IdAndPictograma_Id(pictograma.getIdPictograma(), usuario.getIdUsuario())) return false;
        repository.save(new PictogramaOculto(usuario, pictograma));
        return true;
    }

    public boolean desocultarPictograma(Pictograma pictograma, Usuario usuario) {
        Optional<PictogramaOculto> oculto = repository.findByPictogramaIdAndUsuarioId(
            pictograma.getIdPictograma(), usuario.getIdUsuario());
        if (oculto.isPresent()) {
            repository.delete(oculto.get());
            return true;
        }
        return false;
    }
}
