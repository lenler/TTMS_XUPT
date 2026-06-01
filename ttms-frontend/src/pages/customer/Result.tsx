// 支付结果页

import { Button, Result } from 'antd';
import { Link, useParams } from 'react-router-dom';

function ResultPage() {
  const { orderId } = useParams();
  return (
    <Result
      status="success"
      title="支付成功"
      subTitle={`订单号：${orderId}`}
      extra={[
        <Button type="primary" key="orders"><Link to="/orders">查看订单</Link></Button>,
        <Button key="schedule"><Link to="/schedule">继续购票</Link></Button>,
      ]}
    />
  );
}

export default ResultPage;
