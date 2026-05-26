// 状态标签组件：将数字状态码渲染为彩色 Tag

import { Tag } from 'antd';

interface StatusMap {
  [key: number]: { color: string; text: string };
}

const defaultStatusMap: StatusMap = {
  1: { color: 'green', text: '启用' },
  0: { color: 'red', text: '禁用' },
};

interface StatusTagProps {
  /** 当前状态值 */
  status: number;
  /** 状态映射表，不传则使用默认映射（1启用/0禁用） */
  statusMap?: StatusMap;
}

function StatusTag({ status, statusMap = defaultStatusMap }: StatusTagProps) {
  const config = statusMap[status];
  if (!config) {
    return <Tag>未知</Tag>;
  }
  return <Tag color={config.color}>{config.text}</Tag>;
}

export default StatusTag;
