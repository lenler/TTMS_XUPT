// 管理端登录页

import { useState } from 'react';
import { useNavigate, Navigate } from 'react-router-dom';
import { Form, Input, Button, Card, Typography, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { adminLogin } from '@/services/admin/auth';
import { useAuthStore } from '@/stores/authStore';

const { Title } = Typography;

interface LoginForm {
  username: string;
  password: string;
}

function AdminLoginPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);

  // 已登录则直接跳转工作台
  if (localStorage.getItem('token')) {
    return <Navigate to="/admin/dashboard" replace />;
  }

  const onFinish = async (values: LoginForm) => {
    setLoading(true);
    try {
      const res = await adminLogin(values);
      const { token, employee } = res.data as unknown as { token: string; employee: { id: number; name: string; positionName: string; roles: string[] } };
      login(token, {
        id: employee.id,
        name: employee.name,
        positionName: employee.positionName,
        roles: employee.roles,
      });
      message.success('登录成功');
      navigate('/admin/dashboard', { replace: true });
    } catch {
      // 错误已在 request 拦截器中统一处理
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        background: '#f0f2f5',
      }}
    >
      <Card style={{ width: 400, boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
        <Title level={3} style={{ textAlign: 'center', marginBottom: 32 }}>
          汉唐剧院票务管理系统
        </Title>
        <Form
          name="adminLogin"
          onFinish={onFinish}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入工号' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="工号" />
          </Form.Item>
          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="密码" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block loading={loading}>
              登录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}

export default AdminLoginPage;
