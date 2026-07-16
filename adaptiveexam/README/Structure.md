# Structure

src
 └── main
      ├── java
      │    └── com
      │         └── ahuralearn
      │              └── adaptiveexam (或者直接叫 exam)
      │                   ├── constants                  // 常量和枚举包
      │                   │    ├── ExamErrorInfo.java    // 接口：统一定义报错信息和错误码
      │                   │    └── QuestionType.java     // 枚举：题型枚举 (单选、多选、判断)
      │                   │
      │                   ├── controller                 // 表现层：专门写 @RestController 接前端请求
      │                   │    ├── AssessmentController.java // 处理交卷、看报告、看Dashboard的接口
      │                   │    ├── CourseController.java     // 处理获取课程/模块列表的接口
      │                   │    ├── QuestionController.java   // 处理题库后台增删改查的接口
      │                   │    └── AIChatController.java     // 处理 Feedback 页面 AI 聊天的接口
      │                   │
      │                   ├── domain                     // 领域模型层：所有的“数据包装盒”都在这里
      │                   │    ├── dto                   // Data Transfer Object：接收前端发来的数据
      │                   │    │    ├── SubmitExamDTO.java   // 前端交卷发来的答案和时间载荷
      │                   │    │    ├── QuestionFormDTO.java // 老师后台新建/修改题目发来的载荷
      │                   │    │    └── ChatRequestDTO.java  // 用户发给 AI 的聊天内容
      │                   │    │
      │                   │    ├── po                    // Persistent Object：直接和数据库表字段一一对应的类
      │                   │    │    ├── UserAssessmentRecord.java // 对应：用户考试记录表
      │                   │    │    ├── AssessmentAnswerDetail.java // 对应：用户每道题答题细节表
      │                   │    │    ├── CourseModule.java         // 对应：课程模块表
      │                   │    │    └── QuestionBank.java         // 对应：题库表
      │                   │    │
      │                   │    ├── query                 // Query Object：接收前端的 GET 搜索/分页查询条件
      │                   │    │    ├── QuestionPageQuery.java // 后台查题库时的分页条件(页码、关键词)
      │                   │    │    └── RecordPageQuery.java   // 查历史考试记录的分页条件
      │                   │    │
      │                   │    └── vo                    // View Object：后端组装好，返回给前端展示的数据
      │                   │         ├── DashboardVO.java       // 给仪表盘返回的分数、雷达图数据
      │                   │         ├── ExamReportVO.java      // 给反馈页返回的最终成绩单和红绿格子
      │                   │         ├── CourseListVO.java      // 给选课弹窗返回的课程列表
      │                   │         └── ChatResponseVO.java    // AI 回复的文本封装
      │                   │
      │                   ├── mapper                     // MyBatis 接口层：定义操作数据库的方法
      │                   │    ├── AssessmentMapper.java
      │                   │    ├── CourseMapper.java
      │                   │    └── QuestionMapper.java
      │                   │
      │                   ├── service                    // 业务逻辑层：核心大脑 (先写接口，再写实现类)
      │                   │    ├── IAssessmentService.java
      │                   │    ├── ICourseService.java
      │                   │    ├── IQuestionService.java
      │                   │    ├── IAIChatService.java
      │                   │    │
      │                   │    └── impl                  // Service 接口的具体实现代码
      │                   │         ├── AssessmentServiceImpl.java // 核心算分逻辑写在这里！
      │                   │         ├── CourseServiceImpl.java
      │                   │         ├── QuestionServiceImpl.java
      │                   │         └── AIChatServiceImpl.java
      │                   │
      │                   └── AdaptiveExamApplication.java // Spring Boot 的启动类
      │
      └── resources
           ├── application.yml                           // 配置文件：配 MySQL 账号密码、端口号等
           └── mapper                                    // MyBatis XML 映射文件：写手写 SQL 的地方
                ├── AssessmentMapper.xml
                ├── CourseMapper.xml
                └── QuestionMapper.xml