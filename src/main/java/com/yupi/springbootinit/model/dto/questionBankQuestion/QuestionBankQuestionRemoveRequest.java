package com.yupi.springbootinit.model.dto.questionBankQuestion;


import lombok.Data;

import java.io.Serializable;

/**
 * 移除题目题库关系请求
 */
@Data
public class QuestionBankQuestionRemoveRequest implements Serializable {
    private Long questionBankId;

    private Long questionId;

    private static final long serialVersionUID = 1L;
}
