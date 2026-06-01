// 放映安排列表页

import { useEffect, useState } from 'react';
import { Button, Card, Table } from 'antd';
import { Link } from 'react-router-dom';
import { getPublicSchedules } from '@/services/customer/schedule';
import type { Schedule } from '@/types/models';

function SchedulePage() {
  const [list, setList] = useState<Schedule[]>([]);

  useEffect(() => {
    getPublicSchedules().then(setList).catch(() => {});
  }, []);

  return (
    <Card title="放映安排">
      <Table
        rowKey="id"
        dataSource={list}
        columns={[
          { title: '剧目', dataIndex: 'playName' },
          { title: '演出厅', dataIndex: 'studioName' },
          { title: '演出时间', dataIndex: 'showTime' },
          { title: '票价', dataIndex: 'ticketPrice', render: (v: number) => `¥${Number(v).toFixed(2)}` },
          { title: '操作', render: (_, record) => <Button type="primary"><Link to={`/seats/${record.id}`}>选座订票</Link></Button> },
        ]}
      />
    </Card>
  );
}

export default SchedulePage;
