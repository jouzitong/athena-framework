package org.arthena.framework.common.constant;

/**
 * 参数异常业务子码。
 *
 * <p>编码规范：YY_XX_####
 * <ul>
 *     <li>YY: 大方向（41=参数域）</li>
 *     <li>XX: 小方向（01=必填缺失，02=取值非法，03=请求体结构非法，04=参数约束冲突）</li>
 *     <li>####: 具体业务编号</li>
 * </ul>
 */
public interface ParamBizCodeConstant {

    // 41_01_xxxx 必填参数缺失
    Integer REQUIRED_DTO = 41_01_0001;
    Integer REQUIRED_ID = 41_01_0002;
    Integer REQUIRED_ID_OR_DTO = 41_01_0003;
    Integer REQUIRED_QUERY_OR_DTO = 41_01_0004;
    Integer REQUIRED_MESSAGES = 41_01_0005;
    Integer REQUIRED_INPUTS = 41_01_0006;
    Integer REQUIRED_QUERY_OR_CANDIDATES = 41_01_0007;
    Integer REQUIRED_KB_ID_OR_DOCUMENTS = 41_01_0008;
    Integer REQUIRED_KB_ID_OR_DOCUMENT_IDS = 41_01_0009;
    Integer REQUIRED_KB_ID_OR_QUERY = 41_01_0010;

    // 41_02_xxxx 参数取值非法
    Integer INVALID_TOP_K = 41_02_0001;

    // 41_03_xxxx 请求体结构非法
    Integer INVALID_MESSAGES_ITEM = 41_03_0001;
    Integer INVALID_INPUTS_ITEM = 41_03_0002;
    Integer INVALID_CANDIDATES_ITEM = 41_03_0003;
    Integer INVALID_DOCUMENTS_ITEM = 41_03_0004;
    Integer INVALID_DOCUMENT_IDS_ITEM = 41_03_0005;

    // 41_04_xxxx 参数约束冲突
    Integer CONFLICT_ALLOW_MULTIPLE = 41_04_0001;
}
