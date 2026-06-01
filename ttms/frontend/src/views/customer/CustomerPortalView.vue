<template>
  <section class="grid cols-2">
    <article class="card">
      <h2>放映安排</h2>
      <table>
        <tbody>
          <tr v-for="show in shows" :key="show.name">
            <td>{{ show.name }}</td>
            <td>{{ show.time }}</td>
            <td><button class="btn secondary">选座</button></td>
          </tr>
        </tbody>
      </table>
    </article>
    <article class="card">
      <h2>观众端功能</h2>
      <p class="muted">首页、放映安排、选座订票、确认订单、订单结果、榜单、联系我们、登录注册。</p>
    </article>
  </section>
</template>

<script setup>
const shows = [
  { name: '长安夜话', time: '今晚 19:30 一号厅' },
  { name: '丝路回声', time: '明日 20:00 二号厅' }
]
</script>
