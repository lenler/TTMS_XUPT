// 骨架屏加载组件 —— 页面或卡片加载时的占位效果

import { Card, Skeleton, Row, Col } from 'antd';

interface SkeletonLoadingProps {
  /** 行数 */
  rows?: number;
  /** 是否显示卡片样式 */
  card?: boolean;
  /** 卡片数量（卡片模式下有效） */
  cardCount?: number;
}

/**
 * 骨架屏加载组件
 * 用于页面数据加载时展示占位效果，提升用户体验
 *
 * @example
 * <SkeletonLoading rows={4} />
 * <SkeletonLoading card cardCount={6} />
 */
function SkeletonLoading({ rows = 3, card = false, cardCount = 4 }: SkeletonLoadingProps) {
  if (card) {
    return (
      <Row gutter={[16, 16]}>
        {Array.from({ length: cardCount }).map((_, i) => (
          <Col xs={24} sm={12} lg={6} key={i}>
            <Card>
              <Skeleton active paragraph={{ rows: 2 }} />
            </Card>
          </Col>
        ))}
      </Row>
    );
  }

  return (
    <Card>
      <Skeleton active paragraph={{ rows }} />
    </Card>
  );
}

export default SkeletonLoading;
