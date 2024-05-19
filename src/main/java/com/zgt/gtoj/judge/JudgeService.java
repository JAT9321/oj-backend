package com.zgt.gtoj.judge;

import com.zgt.gtoj.model.entity.QuestionSubmit;

/**
 * 判题服务
 */
public interface JudgeService {
    /**
     * 这里只需要传个questionSubmitId即可，在进行判题前，我们已经把提交保存到数据库了
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
