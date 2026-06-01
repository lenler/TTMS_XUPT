// 票房榜单页

import { useEffect, useState } from 'react';
import { Card, List, Tag } from 'antd';
import { getBoxOfficeBoard } from '@/services/customer/board';

function BoardPage() {
  const [items, setItems] = useState<Awaited<ReturnType<typeof getBoxOfficeBoard>>>([]);

  useEffect(() => {
    getBoxOfficeBoard().then(setItems).catch(() => {});
  }, []);

  return (
    <Card title="票房榜单">
      <List
        dataSource={items}
        renderItem={(item, index) => (
          <List.Item actions={[<Tag key="rank" color={index < 3 ? 'gold' : 'default'}>#{index + 1}</Tag>]}>
            <List.Item.Meta title={item.playName} description={`${item.studioName} / ${item.showTime} / ¥${Number(item.ticketPrice).toFixed(2)}`} />
          </List.Item>
        )}
      />
    </Card>
  );
}

export default BoardPage;
