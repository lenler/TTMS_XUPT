// 观众注册页

import { Button, Card, Form, Input, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { customerRegister } from '@/services/customer/auth';

function RegisterPage() {
  const navigate = useNavigate();

  const onFinish = async (values: { username: string; password: string; name: string; phone?: string; email?: string }) => {
    try {
      await customerRegister(values);
      message.success('注册成功，请登录');
      navigate('/login');
    } catch (err) {
      message.error(err instanceof Error ? err.message : '注册失败');
    }
  };

  return (
    <Card title="观众注册" style={{ maxWidth: 480, margin: '40px auto' }}>
      <Form layout="vertical" onFinish={onFinish}>
        <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="password" label="密码" rules={[{ required: true }]}>
          <Input.Password />
        </Form.Item>
        <Form.Item name="name" label="姓名" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="phone" label="手机号">
          <Input />
        </Form.Item>
        <Form.Item name="email" label="邮箱">
          <Input />
        </Form.Item>
        <Button type="primary" htmlType="submit" block>注册</Button>
      </Form>
    </Card>
  );
}

export default RegisterPage;
