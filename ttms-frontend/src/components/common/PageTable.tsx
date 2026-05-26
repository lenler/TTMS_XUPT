// 通用分页表格组件

import { Table, Button, Popconfirm, Space, Input } from "antd";
import { PlusOutlined, SearchOutlined } from "@ant-design/icons";
import type { ColumnsType, TablePaginationConfig } from "antd/es/table";

interface PageTableProps<T> {
  /** 表格列配置 */
  columns: ColumnsType<T>;
  /** 数据源 */
  dataSource: T[];
  /** 是否加载中 */
  loading: boolean;
  /** 分页信息 */
  pagination: { page: number; pageSize: number; total: number };
  /** 搜索关键词 */
  keyword: string;
  /** 搜索回调 */
  onSearch: (keyword: string) => void;
  /** 新增回调 */
  onAdd?: () => void;
  /** 编辑回调 */
  onEdit?: (record: T) => void;
  /** 删除回调 */
  onDelete?: (id: number) => void;
  /** 行唯一 key */
  rowKey?: string;
  /** 分页变化 */
  onPageChange: (page: number, pageSize: number) => void;
  /** 新增按钮文字 */
  addText?: string;
}

function PageTable<T extends { id: number }>({
  columns,
  dataSource,
  loading,
  pagination,
  keyword,
  onSearch,
  onAdd,
  onEdit,
  onDelete,
  rowKey = "id",
  onPageChange,
  addText = "新增",
}: PageTableProps<T>) {
  /** 构建完整表格列（含操作列） */
  const fullColumns: ColumnsType<T> = [
    ...columns,
    ...(onEdit || onDelete
      ? [
          {
            title: "操作",
            key: "action",
            width: 160,
            render: (_: unknown, record: T) => (
              <Space>
                {onEdit && (
                  <Button
                    type="link"
                    size="small"
                    onClick={() => onEdit(record)}
                  >
                    编辑
                  </Button>
                )}
                {onDelete && (
                  <Popconfirm
                    title="确定删除？"
                    onConfirm={() => onDelete(record.id)}
                  >
                    <Button type="link" size="small" danger>
                      删除
                    </Button>
                  </Popconfirm>
                )}
              </Space>
            ),
          },
        ]
      : []),
  ];

  const tablePagination: TablePaginationConfig = {
    current: pagination.page,
    pageSize: pagination.pageSize,
    total: pagination.total,
    showSizeChanger: true,
    showQuickJumper: true,
    showTotal: (total) => `共 ${total} 条`,
    onChange: onPageChange,
  };

  return (
    <div>
      {/* 顶栏：搜索 + 新增 */}
      <div
        style={{
          marginBottom: 16,
          display: "flex",
          justifyContent: "space-between",
        }}
      >
        <Input
          placeholder="输入关键字搜索"
          prefix={<SearchOutlined />}
          value={keyword}
          onChange={(e) => onSearch(e.target.value)}
          style={{ width: 300 }}
          allowClear
        />
        {onAdd && (
          <Button type="primary" icon={<PlusOutlined />} onClick={onAdd}>
            {addText}
          </Button>
        )}
      </div>

      {/* 表格 */}
      <Table<T>
        columns={fullColumns}
        dataSource={dataSource}
        rowKey={rowKey}
        loading={loading}
        pagination={tablePagination}
        scroll={{ x: 800 }}
      />
    </div>
  );
}

export default PageTable;
