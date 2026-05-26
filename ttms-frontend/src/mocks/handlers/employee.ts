// 员工 Mock Handler：完整 CRUD
import { http, HttpResponse } from 'msw';
import employeesData from '../data/employees.json';

interface Employee {
  id: number;
  employeeNo: string;
  name: string;
  gender: number;
  phone: string;
  email: string;
  positionId: number;
  positionName: string;
  password: string;
  status: number;
}

let employees: Employee[] = JSON.parse(JSON.stringify(employeesData));

/** 岗位字典映射 */
const positionNames: Record<number, string> = {
  1: '售票员',
  2: '运营经理',
  3: '系统管理员',
  4: '会计',
  5: '财务经理',
  6: '场务员',
  7: '设备运维',
};

export const employeeHandlers = [
  /** 查询员工列表 */
  http.get('/admin/api/employees', ({ request }) => {
    const url = new URL(request.url);
    const keyword = url.searchParams.get('keyword') || '';
    const role = url.searchParams.get('role') || '';
    const page = Number(url.searchParams.get('page')) || 1;
    const pageSize = Number(url.searchParams.get('pageSize')) || 10;

    let filtered = employees;
    if (keyword) {
      filtered = filtered.filter(
        (e) => e.employeeNo.includes(keyword) || e.name.includes(keyword) || e.phone.includes(keyword)
      );
    }
    if (role) {
      filtered = filtered.filter((e) => e.positionId === Number(role));
    }

    const start = (page - 1) * pageSize;
    const list = filtered.slice(start, start + pageSize);

    // 返回列表不包含密码字段
    const safeList = list.map(({ password, ...rest }) => rest);

    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: { list: safeList, total: filtered.length, page, pageSize },
    });
  }),

  /** 查询单个员工 */
  http.get('/admin/api/employees/:id', ({ params }) => {
    const employee = employees.find((e) => e.id === Number(params.id));
    if (!employee) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }
    const { password, ...rest } = employee;
    return HttpResponse.json({ resCode: '10000', resMsg: '请求成功', data: rest });
  }),

  /** 新增员工 */
  http.post('/admin/api/employees', async ({ request }) => {
    const body = (await request.json()) as {
      employeeNo: string;
      name: string;
      gender: number;
      phone: string;
      email: string;
      positionId: number;
      password: string;
    };
    const newId = Math.max(0, ...employees.map((e) => e.id)) + 1;
    const newEmployee: Employee = {
      ...body,
      id: newId,
      positionName: positionNames[body.positionId] || '未知',
      status: 1,
    };
    employees.push(newEmployee);
    return HttpResponse.json({ resCode: '10000', resMsg: '添加成功', data: { id: newId } });
  }),

  /** 修改员工 */
  http.put('/admin/api/employees/:id', async ({ request, params }) => {
    const body = (await request.json()) as {
      employeeNo: string;
      name: string;
      gender: number;
      phone: string;
      email: string;
      positionId: number;
      password?: string;
    };
    const idx = employees.findIndex((e) => e.id === Number(params.id));
    if (idx === -1) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }
    const { password: _, ...updateData } = body;
    // 只有传了密码才更新
    const passwordUpdate = body.password ? { password: body.password } : {};
    employees[idx] = {
      ...employees[idx],
      ...updateData,
      ...passwordUpdate,
      positionName: positionNames[body.positionId] || employees[idx].positionName,
    };
    return HttpResponse.json({ resCode: '10000', resMsg: '修改成功', data: null });
  }),

  /** 删除员工 */
  http.delete('/admin/api/employees/:id', ({ params }) => {
    employees = employees.filter((e) => e.id !== Number(params.id));
    return HttpResponse.json({ resCode: '10000', resMsg: '删除成功', data: null });
  }),
];
