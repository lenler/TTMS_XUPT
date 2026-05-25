// 通用分页表格组件

import type { ReactNode } from 'react';
import { Button, Card, Input, Space, Table } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined, ReloadOutlined, SearchOutlined } from '@ant-design/icons';

/** 通用分页表格组件属性 */
interface PageTableProps<T extends object> {
  title: string;
  rowKey: TableProps<T>['rowKey'];
  columns: TableProps<T>['columns'];
  dataSource: T[];
  loading: boolean;
  total: number;
  page: number;
  pageSize: number;
  keyword: string;
  extra?: ReactNode;
  addText?: string;
  onSearch: (keyword: string) => void;
  onRefresh: () => void;
  onAdd?: () => void;
  onPageChange: (page: number, pageSize: number) => void;
}

/** 渲染带搜索栏、工具栏和分页能力的业务表格 */
function PageTable<T extends object>({
  title,
  rowKey,
  columns,
  dataSource,
  loading,
  total,
  page,
  pageSize,
  keyword,
  extra,
  addText = '新增',
  onSearch,
  onRefresh,
  onAdd,
  onPageChange,
}: PageTableProps<T>) {
  return (
    <Card
      title={title}
      extra={
        <Space>
          <Input.Search
            allowClear
            enterButton={<SearchOutlined />}
            placeholder="请输入关键字"
            defaultValue={keyword}
            style={{ width: 260 }}
            onSearch={onSearch}
          />
          <Button icon={<ReloadOutlined />} onClick={onRefresh}>
            刷新
          </Button>
          {extra}
          {onAdd && (
            <Button type="primary" icon={<PlusOutlined />} onClick={onAdd}>
              {addText}
            </Button>
          )}
        </Space>
      }
    >
      <Table<T>
        rowKey={rowKey}
        columns={columns}
        dataSource={dataSource}
        loading={loading}
        pagination={{
          current: page,
          pageSize,
          total,
          showSizeChanger: true,
          showTotal: (count) => `共 ${count} 条`,
          onChange: onPageChange,
        }}
      />
    </Card>
  );
}

export default PageTable;
