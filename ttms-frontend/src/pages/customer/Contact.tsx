// 联系我们页

import { Card, Descriptions } from 'antd';

function ContactPage() {
  return (
    <Card title="联系我们">
      <Descriptions column={1} bordered>
        <Descriptions.Item label="剧院名称">汉唐剧院</Descriptions.Item>
        <Descriptions.Item label="地址">西安市长安区西长安街</Descriptions.Item>
        <Descriptions.Item label="服务热线">029-88888888</Descriptions.Item>
        <Descriptions.Item label="营业时间">09:00 - 22:00</Descriptions.Item>
      </Descriptions>
    </Card>
  );
}

export default ContactPage;
