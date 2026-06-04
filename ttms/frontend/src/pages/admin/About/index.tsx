// 关于页面 —— 展示系统版本、技术栈、开发团队信息

import { useEffect, useState } from 'react';
import {
  Card, Descriptions, Tag, Typography, Spin, Divider, Row, Col, Statistic, Space,
} from 'antd';
import {
  InfoCircleOutlined, CodeOutlined, TeamOutlined,
  GithubOutlined, CheckCircleOutlined,
} from '@ant-design/icons';
import { getSystemInfo, type SystemInfo } from '@/services/admin/system';

const { Title, Text, Paragraph } = Typography;

/** 技术栈信息 */
const techStack = [
  { category: '前端框架', items: ['React 18', 'TypeScript', 'Vite', 'Ant Design 5'] },
  { category: '后端框架', items: ['Java 17', 'Spring Boot 3.3', 'MyBatis', 'H2/MySQL'] },
  { category: '构建工具', items: ['Maven', 'pnpm', 'ESLint'] },
  { category: '其他', items: ['Axios', 'Zustand', 'MSW', 'React Router 6'] },
];

function AboutPage() {
  const [loading, setLoading] = useState(true);
  const [info, setInfo] = useState<SystemInfo | null>(null);

  useEffect(() => {
    getSystemInfo()
      .then((res) => setInfo(res.data))
      .catch(() => { /* ignore */ })
      .finally(() => setLoading(false));
  }, []);

  return (
    <Spin spinning={loading}>
      <div style={{ maxWidth: 900, margin: '0 auto' }}>
        {/* 页头 */}
        <div style={{ marginBottom: 24 }}>
          <Title level={3}>
            <InfoCircleOutlined /> 关于系统
          </Title>
          <Text type="secondary">
            票枢Core 剧目票务管理系统 &mdash; 项目概述与技术说明
          </Text>
        </div>

        {/* 项目基本信息 */}
        <Card style={{ marginBottom: 24 }}>
          <Descriptions title="项目信息" column={2} bordered size="small">
            <Descriptions.Item label="项目名称" span={2}>
              <Text strong>{info?.name || '票枢Core · 剧目票务管理系统'}</Text>
            </Descriptions.Item>
            <Descriptions.Item label="英文名称" span={2}>
              {info?.nameEn || 'Ticket Core — Troupe Ticket Management System'}
            </Descriptions.Item>
            <Descriptions.Item label="简称">
              <Tag color="blue">{info?.abbr || 'TTMS'}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="版本号">
              <Tag color="green">v{info?.version || '0.1.0'}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="开发团队">
              <Space>
                <TeamOutlined /> {info?.team || 'HanTang Studio'}
              </Space>
            </Descriptions.Item>
            <Descriptions.Item label="开发者">
              <Space>
                <GithubOutlined /> {info?.developer || 'lyd60417'}
              </Space>
            </Descriptions.Item>
            <Descriptions.Item label="项目描述" span={2}>
              {info?.description || '面向中小型剧院的剧目票务全流程管理平台'}
            </Descriptions.Item>
          </Descriptions>
        </Card>

        {/* 技术栈 */}
        <Card
          title={<><CodeOutlined /> 技术栈</>}
          style={{ marginBottom: 24 }}
        >
          <Row gutter={[16, 16]}>
            {techStack.map((group) => (
              <Col xs={24} sm={12} key={group.category}>
                <Card size="small" title={group.category} variant="outlined">
                  <Space direction="vertical">
                    {group.items.map((item) => (
                      <Tag key={item} color="default">{item}</Tag>
                    ))}
                  </Space>
                </Card>
              </Col>
            ))}
          </Row>
        </Card>

        {/* 运行状态 */}
        <Card
          title={<><CheckCircleOutlined /> 运行状态</>}
          style={{ marginBottom: 24 }}
        >
          <Row gutter={24}>
            <Col span={12}>
              <Statistic
                title="Java 版本"
                value={info?.javaVersion || SystemInfoFallback()}
                valueStyle={{ fontSize: 18 }}
              />
            </Col>
            <Col span={12}>
              <Statistic
                title="操作系统"
                value={info?.osInfo || 'unknown'}
                valueStyle={{ fontSize: 18 }}
              />
            </Col>
          </Row>
          {info && (
            <Divider />
          )}
          {info && (
            <Text type="secondary" style={{ fontSize: 12 }}>
              系统启动时间：{info.startupTime} | 数据查询时间：{info.timestamp}
            </Text>
          )}
        </Card>

        {/* 版权信息 */}
        <div style={{ textAlign: 'center', padding: '12px 0' }}>
          <Paragraph type="secondary" style={{ fontSize: 12 }}>
            TTMS &copy; {new Date().getFullYear()} HanTang Studio &mdash;
            软件工程课程设计项目 &mdash; 贡献者：lyd60417
          </Paragraph>
        </div>
      </div>
    </Spin>
  );
}

/** 获取 Java 版本的本地兜底（API 不可用时的 fallback） */
function SystemInfoFallback(): string {
  return '17+';
}

export default AboutPage;
