package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import model.PictogramaOculto;

public interface PictogramaOcultoRepository extends JpaRepository<PictogramaOculto, Long> {

    List<PictogramaOculto> findByUsuario_Id(Long usuarioId);

    boolean existsByUsuario_IdAndPictograma_Id(Long usuarioId, Integer pictogramaId);

    void deleteByUsuario_IdAndPictograma_Id(Long usuarioId, Integer pictogramaId);
}
