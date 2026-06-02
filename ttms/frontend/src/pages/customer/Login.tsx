// 观众端登录页 —— 水墨留白 · 东方极简
// 居中卡片 + 衬线标题 + 线性输入框

import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Form, Input, Button, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { customerLogin } from '@/services/customer/auth';

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
    <div className="max-w-sm mx-auto pt-20 pb-20">
      <div className="border border-warm bg-cream rounded-sm p-8">
        {/* 品牌标题 */}
        <h1 className="font-serif text-2xl text-ink tracking-widest text-center mb-8">
          汉唐剧院
        </h1>

        <Form onFinish={onFinish} size="large">
          <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input
              prefix={<UserOutlined className="text-light-ink" />}
              placeholder="用户名"
            />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password
              prefix={<LockOutlined className="text-light-ink" />}
              placeholder="密码"
            />
          </Form.Item>
          <Form.Item className="!mb-4">
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
              className="!bg-ink !border-ink hover:!bg-gold hover:!border-gold !rounded-sm !h-10"
            >
              登录
            </Button>
          </Form.Item>
          <div className="text-center text-sm">
            <span className="text-stone">还没有账号？</span>
            <Link to="/register" className="text-gold hover:text-[#B8944F] ml-1 transition-soft">
              立即注册
            </Link>
          </div>
        </Form>
      </div>
    </div>
  );
}

export default CustomerLoginPage;
