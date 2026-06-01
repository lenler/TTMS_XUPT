<template>
  <section class="grid cols-3">
    <article class="card" v-for="metric in metrics" :key="metric.label">
      <p class="muted">{{ metric.label }}</p>
      <div class="metric">{{ metric.value }}</div>
    </article>
  </section>
  <section class="grid cols-2" style="margin-top:16px">
    <article class="card">
      <h2>今日业务链路</h2>
      <table>
        <tbody>
          <tr v-for="step in flow" :key="step.name">
            <td>{{ step.name }}</td>
            <td class="muted">{{ step.owner }}</td>
            <td>{{ step.status }}</td>
          </tr>
        </tbody>
      </table>
    </article>
    <article class="card">
      <h2>待处理事项</h2>
      <table>
        <tbody>
          <tr v-for="task in tasks" :key="task">
            <td>{{ task }}</td>
            <td><button class="btn secondary">查看</button></td>
          </tr>
        </tbody>
      </table>
    </article>
  </section>
</template>

<script setup>
const metrics = [
  { label: '今日销售额', value: '¥12,680' },
  { label: '待验票观众', value: '326' },
  { label: '平均上座率', value: '78%' }
]

const flow = [
  { name: '引进剧目', owner: '运营经理', status: '已完成' },
  { name: '制定演出计划', owner: '计划员', status: '进行中' },
  { name: '售票/退票', owner: '售票员', status: '开放' },
  { name: '入场验票', owner: '场务员', status: '待开始' }
]

const tasks = ['确认今晚 19:30 一号厅验票终端', '核对售票员日结算', '检查明日演出计划冲突']
</script>
