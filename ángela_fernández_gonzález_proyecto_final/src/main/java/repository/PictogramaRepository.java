package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import model.Pictograma;

public interface PictogramaRepository extends JpaRepository<Pictograma, Integer> {

    // Pictogramas generales (los que no tienen usuario asociado)
    @Query("SELECT p FROM Pictograma p WHERE p.usuario IS NULL")
    List<Pictograma> findAllGenerales();

    // Pictogramas personalizados de un usuario concreto
    @Query("SELECT p FROM Pictograma p WHERE p.usuario.id = :usuarioId")
    List<Pictograma> findAllPersonalizados(@Param("usuarioId") Long usuarioId);
}
