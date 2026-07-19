package com.ahuralearn.game.controller;

import com.ahuralearn.common.domain.Result;
import com.ahuralearn.game.domain.vo.GameVO;
import com.ahuralearn.game.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//通过courseId以及获取到的user Id 作为查询参数去lesson表查询 若有记录则表明注册了该课
@RestController
@RequestMapping("/api/v1/courses/{courseId}/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public Result<List<GameVO>> getGames(@PathVariable Long courseId) {
        return Result.success(gameService.getGames(courseId));
    }

    @GetMapping("/{gameCode}/instruction")
    public Result<Map<String, Object>> getInstruction(@PathVariable Long courseId,
                                                      @PathVariable String gameCode) {
        return Result.success(gameService.getInstruction(courseId, gameCode));
    }

    // 查询指定课程、指定游戏的挑战题目
    @GetMapping("/{gameCode}/challenge-questions")
    public Result<Object> getChallengeQuestions(@PathVariable Long courseId,
                                                @PathVariable String gameCode) {
        return Result.success(gameService.getChallengeQuestions(courseId, gameCode));
    }

    @GetMapping("/code-firewall/syntax-items")
    public Result<Object> getSyntaxItems(@PathVariable Long courseId) {
        return Result.success(gameService.getSyntaxItems(courseId));
    }

    @GetMapping("/concept-sorter/questions")
    public Result<Object> getKnowledgeQuestions(@PathVariable Long courseId) {
        return Result.success(gameService.getKnowledgeQuestions(courseId));
    }

    @GetMapping("/concept-sorter/config")
    public Result<Object> getKnowledgeConfig(@PathVariable Long courseId) {
        return Result.success(gameService.getKnowledgeConfig(courseId));
    }

    @PostMapping("/result")
    public Result<Map<String, Object>> submitGameResult(@PathVariable Long courseId,
                                                        @RequestBody Map<String, Object> gameResult) {
        gameResult.put("courseId", courseId);
        return Result.success(gameResult);
    }
}