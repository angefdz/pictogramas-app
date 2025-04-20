package service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import model.Categoria;
import repository.CategoriaRepository;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Categoria crearCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> obtenerCategoriasGenerales() {
        return categoriaRepository.findAllGenerales();
    }

    public List<Categoria> obtenerCategoriasPersonalizadas(long usuarioId) {
        return categoriaRepository.findAllPersonalizados(usuarioId);
    }

    public Optional<Categoria> obtenerPorId(int id) {
        return categoriaRepository.findById(id);
    }

    public Optional<Categoria> actualizarCategoria(int id, Categoria categoriaActualizada) {
        return categoriaRepository.findById(id).map(c -> {
            // Aquí puedes hacer una actualización parcial si lo necesitas
            c.setNombre(categoriaActualizada.getNombre());
            c.setUsuario(categoriaActualizada.getUsuario());
            return categoriaRepository.save(c);
        });
    }

    public boolean eliminarCategoria(int id) {
        return categoriaRepository.findById(id).map(c -> {
            categoriaRepository.delete(c);
            return true;
        }).orElse(false);
    }
}
