package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.ResultatQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResultatQuizRepository extends JpaRepository<ResultatQuiz, Integer> {
    List<ResultatQuiz> findByEleveIdOrderByDatePassageDesc(Integer eleveId);
    List<ResultatQuiz> findByQuizIdOrderByDatePassageDesc(Integer quizId);
}
