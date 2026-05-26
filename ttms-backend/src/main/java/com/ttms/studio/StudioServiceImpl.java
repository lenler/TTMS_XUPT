package com.ttms.studio;

import com.ttms.common.BusinessException;
import com.ttms.common.ErrorCode;
import com.ttms.common.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 演出厅业务服务实现。
 */
@Service
public class StudioServiceImpl implements StudioService {

    private static final int ACTIVE_STATUS = 1;
    private final StudioRepository studioRepository;

    /**
     * 创建演出厅业务服务。
     *
     * @param studioRepository 演出厅数据访问接口
     */
    public StudioServiceImpl(StudioRepository studioRepository) {
        this.studioRepository = studioRepository;
    }

    /**
     * 分页查询启用演出厅。
     *
     * @param keyword 模糊查询关键字
     * @param page 当前页码
     * @param pageSize 每页数量
     * @return 演出厅分页数据
     */
    @Override
    public PageResult<Studio> list(String keyword, int page, int pageSize) {
        String normalizedKeyword = normalizeKeyword(keyword);
        int offset = (page - 1) * pageSize;
        long total = studioRepository.countActive(normalizedKeyword);
        List<Studio> studios = studioRepository.selectActivePage(normalizedKeyword, offset, pageSize);
        return new PageResult<>(studios, total, page, pageSize);
    }

    /**
     * 根据 ID 查询启用演出厅。
     *
     * @param id 演出厅 ID
     * @return 演出厅实体
     */
    @Override
    public Studio getById(Long id) {
        Studio studio = requireActiveStudio(id);
        return studio;
    }

    /**
     * 新增演出厅。
     *
     * @param request 演出厅请求数据
     * @return 新增演出厅 ID
     */
    @Override
    public Long create(StudioRequest request) {
        Studio studio = toStudio(request);
        studio.setStatus(ACTIVE_STATUS);
        studioRepository.insert(studio);
        return studio.getId();
    }

    /**
     * 修改演出厅基础信息。
     *
     * @param id 演出厅 ID
     * @param request 演出厅请求数据
     */
    @Override
    public void update(Long id, StudioRequest request) {
        requireActiveStudio(id);
        Studio studio = toStudio(request);
        studio.setId(id);
        int updated = studioRepository.update(studio);
        if (updated == 0) {
            throw notFound(id);
        }
    }

    /**
     * 停用演出厅。
     *
     * @param id 演出厅 ID
     */
    @Override
    public void disable(Long id) {
        requireActiveStudio(id);
        int updated = studioRepository.disableById(id);
        if (updated == 0) {
            throw notFound(id);
        }
    }

    /**
     * 将请求数据转换为演出厅实体。
     *
     * @param request 演出厅请求数据
     * @return 演出厅实体
     */
    private Studio toStudio(StudioRequest request) {
        Studio studio = new Studio();
        studio.setName(request.getName().trim());
        studio.setRowCount(request.getRowCount());
        studio.setColCount(request.getColCount());
        studio.setIntroduction(normalizeText(request.getIntroduction()));
        return studio;
    }

    /**
     * 查询并校验演出厅处于启用状态。
     *
     * @param id 演出厅 ID
     * @return 启用状态的演出厅
     */
    private Studio requireActiveStudio(Long id) {
        Studio studio = studioRepository.selectById(id);
        if (studio == null || !Integer.valueOf(ACTIVE_STATUS).equals(studio.getStatus())) {
            throw notFound(id);
        }
        return studio;
    }

    /**
     * 构造数据不存在异常。
     *
     * @param id 演出厅 ID
     * @return 数据不存在异常
     */
    private BusinessException notFound(Long id) {
        return new BusinessException(ErrorCode.NOT_FOUND, "演出厅不存在：" + id);
    }

    /**
     * 规范化模糊查询关键字。
     *
     * @param keyword 原始关键字
     * @return 处理后的关键字
     */
    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }

    /**
     * 规范化可选文本字段。
     *
     * @param text 原始文本
     * @return 处理后的文本
     */
    private String normalizeText(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return text.trim();
    }
}
