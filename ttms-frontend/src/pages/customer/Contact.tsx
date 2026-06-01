// 联系我们页

import { Card, Typography, Descriptions, Divider } from 'antd';
import { EnvironmentOutlined, PhoneOutlined, MailOutlined, ClockCircleOutlined } from '@ant-design/icons';

const { Title, Paragraph } = Typography;

/** 联系我们页 */
function ContactPage() {
  return (
    <div style={{ maxWidth: 800, margin: '0 auto' }}>
      <Title level={3} style={{ marginBottom: 24 }}>联系我们</Title>

      <Card style={{ marginBottom: 24 }}>
        <Title level={4}>汉唐剧院</Title>
        <Descriptions column={1} size="large">
          <Descriptions.Item label={<span><EnvironmentOutlined /> 地址</span>}>
            陕西省西安市长安区西长安街618号
          </Descriptions.Item>
          <Descriptions.Item label={<span><PhoneOutlined /> 票务热线</span>}>
            029-8816 0000
          </Descriptions.Item>
          <Descriptions.Item label={<span><MailOutlined /> 电子邮箱</span>}>
            service@hantang-theatre.com
          </Descriptions.Item>
          <Descriptions.Item label={<span><ClockCircleOutlined /> 营业时间</span>}>
            周一至周日 09:00 - 22:00
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card>
        <Title level={4}>交通路线</Title>
        <Divider />
        <Paragraph><strong>地铁：</strong>乘坐地铁2号线至航天城站，B出口向西步行约800米</Paragraph>
        <Paragraph><strong>公交：</strong>乘坐215路、229路、600路至西长安街站下车</Paragraph>
        <Paragraph><strong>自驾：</strong>剧院配有机动车停车场，凭当日演出票可免费停车3小时</Paragraph>
      </Card>
    </div>
  );
}

export default ContactPage;
