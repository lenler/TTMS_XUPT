<template>
  <section class="grid cols-3">
    <article class="card"><p class="muted">个人日销售额</p><div class="metric">¥4,260</div></article>
    <article class="card"><p class="muted">剧院销售业绩</p><div class="metric">¥86,420</div></article>
    <article class="card"><p class="muted">综合上座率</p><div class="metric">81%</div></article>
  </section>
  <section class="card" style="margin-top:16px">
    <h2>统计口径</h2>
    <table>
      <thead><tr><th>报表</th><th>来源数据</th><th>用途</th></tr></thead>
      <tbody>
        <tr><td>日销售额</td><td>sales / sale_items</td><td>售票员日结算</td></tr>
        <tr><td>票房统计</td><td>paid sales</td><td>财务经理分析</td></tr>
        <tr><td>上座率</td><td>tickets SOLD/CHECKED</td><td>运营决策</td></tr>
      </tbody>
    </table>
  </section>
</template>
