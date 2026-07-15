package com.ahuralearn.game.service.impl;

import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.game.domain.po.GamePO;
import com.ahuralearn.game.domain.po.GameQuestionPO;
import com.ahuralearn.game.domain.po.LearningLessonPO;
import com.ahuralearn.game.domain.vo.GameVO;
import com.ahuralearn.game.mapper.GameLearningLessonMapper;
import com.ahuralearn.game.mapper.GameMapper;
import com.ahuralearn.game.mapper.GameQuestionMapper;
import com.ahuralearn.game.service.GameService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameServiceImpl implements GameService {

    private final GameMapper gameMapper;
    private final GameLearningLessonMapper gameLearningLessonMapper;
    private final GameQuestionMapper gameQuestionMapper;
    private final ObjectMapper objectMapper;

    public GameServiceImpl(GameMapper gameMapper,
                           GameLearningLessonMapper gameLearningLessonMapper,
                           GameQuestionMapper gameQuestionMapper,
                           ObjectMapper objectMapper) {
        this.gameMapper = gameMapper;
        this.gameLearningLessonMapper = gameLearningLessonMapper;
        this.gameQuestionMapper = gameQuestionMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取平台中的全部游戏。
     *
     * courseId 只用于检查当前用户是否学习了该课程，
     * 不再用于查询 game 表。
     */
    @Override
    public List<GameVO> getGames(Long courseId) {
        checkEnrollment(courseId);

        List<GamePO> gamePOList = gameMapper.selectList(
                new LambdaQueryWrapper<GamePO>()
                        .orderByAsc(GamePO::getId)
        );

        return gamePOList.stream()
                .map(gamePO -> {
                    GameVO gameVO = new GameVO();
                    BeanUtils.copyProperties(gamePO, gameVO);
                    return gameVO;
                })
                .toList();
    }

    /**
     * 获取指定游戏的操作说明。
     */
    @Override
    public Map<String, Object> getInstruction(Long courseId,
                                              String gameCode) {
        checkEnrollment(courseId);

        GamePO gamePO = getGameByCode(gameCode);

        Map<String, Object> instruction = new HashMap<>();
        instruction.put("controls", gamePO.getControls());
        instruction.put("goal", gamePO.getGoal());
        instruction.put("tips", gamePO.getTips());

        return instruction;
    }

    /**
     * 获取挑战题目。
     */
    @Override
    public Object getChallengeQuestions(Long courseId,
                                        String gameCode) {
        checkEnrollment(courseId);
        getGameByCode(gameCode);

        return getQuestionData(
                courseId,
                gameCode,
                "CHALLENGE"
        );
    }

    /**
     * 获取 Code Firewall 的语法题目。
     */
    @Override
    public Object getSyntaxItems(Long courseId) {
        checkEnrollment(courseId);
        getGameByCode("code-firewall");

        return getQuestionData(
                courseId,
                "code-firewall",
                "SYNTAX_ITEM"
        );
    }

    /**
     * 获取 Knowledge Defense 的知识题目。
     */
    @Override
    public Object getKnowledgeQuestions(Long courseId) {
        checkEnrollment(courseId);
        getGameByCode("concept-sorter");

        return getQuestionData(
                courseId,
                "concept-sorter",
                "KNOWLEDGE"
        );
    }

    /**
     * 获取 Knowledge Defense 的配置。
     */
    @Override
    public Object getKnowledgeConfig(Long courseId) {
        checkEnrollment(courseId);
        getGameByCode("concept-sorter");

        LambdaQueryWrapper<GameQuestionPO> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(GameQuestionPO::getGameCode, "concept-sorter")
                .eq(GameQuestionPO::getQuestionType, "CONFIG")
                .orderByAsc(GameQuestionPO::getSortOrder)
                .last("limit 1");

        GameQuestionPO config =
                gameQuestionMapper.selectOne(wrapper);

        if (config == null) {
            throw new BusinessException(
                    "Knowledge defense configuration not found."
            );
        }

        return parseJson(config.getQuestionData());
    }

    /**
     * 根据课程、游戏代码和题目类型查询题目。
     *
     * game_question 仍然保留 courseId，
     * 因为每门课程可以使用不同题目。
     */
    private Object getQuestionData(Long courseId,
                                   String gameCode,
                                   String questionType) {
        LambdaQueryWrapper<GameQuestionPO> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(GameQuestionPO::getCourseId, courseId)
                .eq(GameQuestionPO::getGameCode, gameCode)
                .eq(GameQuestionPO::getQuestionType, questionType)
                .orderByAsc(GameQuestionPO::getSortOrder);

        List<GameQuestionPO> questionList =
                gameQuestionMapper.selectList(wrapper);

        return questionList.stream()
                .map(item -> parseJson(item.getQuestionData()))
                .toList();
    }

    /**
     * 根据 gameCode 查询通用游戏信息。
     *
     * 不再使用 courseId 查询 game 表。
     */
    private GamePO getGameByCode(String gameCode) {
        if (gameCode == null || gameCode.isBlank()) {
            throw new BusinessException(
                    "Game code cannot be empty."
            );
        }

        LambdaQueryWrapper<GamePO> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(GamePO::getGameCode, gameCode)
                .last("limit 1");

        GamePO gamePO = gameMapper.selectOne(wrapper);

        if (gamePO == null) {
            throw new BusinessException(
                    "Game not found."
            );
        }

        return gamePO;
    }

    /**
     * 校验当前登录用户是否正在学习该课程。
     */
    private void checkEnrollment(Long courseId) {

        Long userId = UserContext.getUser();

        System.out.println("=================================");
        System.out.println("Game Enrollment Check");
        System.out.println("courseId = " + courseId);
        System.out.println("userId   = " + userId);
        System.out.println("=================================");

        LambdaQueryWrapper<LearningLessonPO> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(LearningLessonPO::getUserId, userId)
                .eq(LearningLessonPO::getCourseId, courseId);

        Long count = gameLearningLessonMapper.selectCount(wrapper);

        System.out.println("count = " + count);

        if (count == null || count == 0) {
            throw new BusinessException(
                    "User has not enrolled in this course."
            );
        }
    }

    /**
     * 将数据库中的 JSON 字符串转成 Java 对象。
     */
    private Object parseJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(
                    json,
                    Object.class
            );
        } catch (JsonProcessingException e) {
            throw new BusinessException(
                    "Game question JSON format is invalid."
            );
        }
    }
}