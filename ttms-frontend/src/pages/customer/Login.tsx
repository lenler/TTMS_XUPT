// 观众端登录页

import { Button, Card, Form, Input, message } from 'antd';
import { Link, useNavigate } from 'react-router-dom';
import { customerLogin } from '@/services/customer/auth';

function CustomerLoginPage() {
  const navigate = useNavigate();

  const onFinish = async (values: { username: string; password: string }) => {
    try {
      await customerLogin(values);
      message.success('登录成功');
      navigate('/');
    } catch (err) {
      message.error(err instanceof Error ? err.message : '登录失败');
    }
  };

  return (
    <Card title="观众登录" style={{ maxWidth: 420, margin: '40px auto' }}>
      <Form layout="vertical" onFinish={onFinish}>
        <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="password" label="密码" rules={[{ required: true }]}>
          <Input.Password />
        </Form.Item>
        <Button type="primary" htmlType="submit" block>登录</Button>
      </Form>
      <div style={{ marginTop: 12 }}>没有账号？<Link to="/register">立即注册</Link></div>
    </Card>
  );
}

export default CustomerLoginPage;
