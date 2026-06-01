// 观众端登录页

import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Card, Form, Input, Button, Typography, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { customerLogin } from '@/services/customer/auth';

const { Title } = Typography;

/** 观众端登录页 */
function CustomerLoginPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onFinish = async (values: { username: string; password: string }) => {
    setLoading(true);
    try {
      const res = await customerLogin(values);
      localStorage.setItem('customerToken', res.data.token);
      localStorage.setItem('customerInfo', JSON.stringify(res.data.customer));
      message.success('登录成功');
      navigate('/', { replace: true });
    } catch {
      // 错误已在拦截器中提示
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: '80px auto' }}>
      <Card>
        <Title level={3} style={{ textAlign: 'center', marginBottom: 24 }}>观众登录</Title>
        <Form onFinish={onFinish} size="large">
          <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input prefix={<UserOutlined />} placeholder="用户名" />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password prefix={<LockOutlined />} placeholder="密码" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>
              登录
            </Button>
          </Form.Item>
          <div style={{ textAlign: 'center' }}>
            还没有账号？<Link to="/register">立即注册</Link>
          </div>
        </Form>
      </Card>
    </div>
  );
}

export default CustomerLoginPage;
