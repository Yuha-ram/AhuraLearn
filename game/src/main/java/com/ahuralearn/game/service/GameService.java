package com.ahuralearn.game.service;

import com.ahuralearn.game.domain.vo.GameVO;

import java.util.List;
import java.util.Map;

/**
 * Game service interface.
 *
 * All game access should be checked by course enrollment in the implementation layer.
 */
public interface GameService {

    List<GameVO> getGames(Long courseId);

    Map<String, Object> getInstruction(Long courseId, String gameCode);

    Object getChallengeQuestions(Long courseId, String gameCode);

    Object getSyntaxItems(Long courseId);

    Object getKnowledgeQuestions(Long courseId);

    Object getKnowledgeConfig(Long courseId);
}