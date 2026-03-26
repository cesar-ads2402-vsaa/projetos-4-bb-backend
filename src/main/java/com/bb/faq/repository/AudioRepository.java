package com.bb.faq.repository;
import com.bb.faq.model.Audio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AudioRepository extends JpaRepository<Audio, Long> {

    List<Audio> findByTutorialIdOrderByVotosDesc(Long tutorialId);
}