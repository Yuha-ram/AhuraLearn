-- 创建课程模块表
CREATE TABLE course_module (
                               id VARCHAR(50) PRIMARY KEY COMMENT '课程ID',
                               name VARCHAR(100) NOT NULL COMMENT '课程名称'
) COMMENT='课程表';

-- 插入两条测试数据
INSERT INTO course_module (id, name) VALUES ('c_001', 'Java Core Programming');
INSERT INTO course_module (id, name) VALUES ('c_002', 'Data Structures & Algorithms');

CREATE TABLE question_bank (
                               id VARCHAR(50) PRIMARY KEY COMMENT '题目ID',
                               module_id VARCHAR(50) NOT NULL COMMENT '归属课程ID',
                               question_text TEXT NOT NULL COMMENT '题干内容',
                               options_json JSON NOT NULL COMMENT '选项(JSON数组)',
                               correct_answer VARCHAR(10) NOT NULL COMMENT '正确答案'
) COMMENT='题库表';


-- 1. 创建考试总成绩记录表
CREATE TABLE user_assessment_record (
                                        id VARCHAR(50) PRIMARY KEY COMMENT '考试记录流水号',
                                        user_id VARCHAR(50) NOT NULL COMMENT '考生ID',
                                        module_id VARCHAR(50) NOT NULL COMMENT '课程模块ID',
                                        score INT NOT NULL COMMENT '最终得分(0-100)',
                                        time_taken INT NOT NULL COMMENT '考试耗时(秒)',
                                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '交卷时间'
) COMMENT='考试成绩主表';

-- 2. 创建每道题的答题细节表
CREATE TABLE assessment_answer_detail (
                                          id VARCHAR(50) PRIMARY KEY COMMENT '细节流水号',
                                          record_id VARCHAR(50) NOT NULL COMMENT '关联的考试记录流水号',
                                          question_id VARCHAR(50) NOT NULL COMMENT '题目ID',
                                          user_answer VARCHAR(100) COMMENT '用户提交的答案',
                                          is_correct TINYINT(1) NOT NULL COMMENT '是否正确：1对，0错'
) COMMENT='答题细节明细表';