package service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import model.Pictograma;
import repository.PictogramaRepository;

@Service
public class PictogramaService {

    @Autowired
    private PictogramaRepository pictogramaRepository;

    public Pictograma crearPictograma(Pictograma pictograma) {
        return pictogramaRepository.save(pictograma);
    }

    public List<Pictograma> obtenerPictogramasGenerales() {
        return pictogramaRepository.findAllGenerales();
    }

    public List<Pictograma> obtenerPictogramasPorUsuario(long usuarioId) {
        return pictogramaRepository.findAllPersonalizados(usuarioId);
    }

    public Optional<Pictograma> obtenerPorId(int id) {
        return pictogramaRepository.findById(id);
    }

    public Optional<Pictograma> actualizarPictograma(int id, Pictograma pictogramaActualizado) {
        return pictogramaRepository.findById(id).map(p -> {
            p.setNombre(pictogramaActualizado.getNombre());
            p.setImagen(pictogramaActualizado.getImagen());
            p.setUsuario(pictogramaActualizado.getUsuario());
            return pictogramaRepository.save(p);
        });
    }

    public boolean eliminarPictograma(int id) {
        return pictogramaRepository.findById(id).map(p -> {
            pictogramaRepository.delete(p);
            return true;
        }).orElse(false);
    }
}
