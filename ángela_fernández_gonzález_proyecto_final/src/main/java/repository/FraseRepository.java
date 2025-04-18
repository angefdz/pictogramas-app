package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import model.Frase;

public interface FraseRepository extends JpaRepository<Frase,Integer>{


    @Query("SELECT f FROM frases f WHERE f.usuario.id = :usuarioId")
    List<Frase> findByUsuario_id(@Param("usuarioId") Long usuarioId);
    


}
