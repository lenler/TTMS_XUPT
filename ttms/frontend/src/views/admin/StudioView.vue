<template>
  <section class="card">
    <div class="form-grid">
      <input v-model="form.name" placeholder="演出厅名称" />
      <input v-model.number="form.rowCount" type="number" placeholder="行数" />
      <input v-model.number="form.colCount" type="number" placeholder="列数" />
      <button class="btn" @click="addStudio">新增演出厅</button>
    </div>
    <div class="toolbar">
      <input v-model="keyword" placeholder="按名称查询" />
      <button class="btn secondary" @click="load">查询</button>
    </div>
    <table>
      <thead>
        <tr><th>ID</th><th>名称</th><th>行列</th><th>状态</th><th>简介</th></tr>
      </thead>
      <tbody>
        <tr v-for="studio in studios" :key="studio.id">
          <td>{{ studio.id }}</td>
          <td>{{ studio.name }}</td>
          <td>{{ studio.rowCount }} x {{ studio.colCount }}</td>
          <td>{{ studio.status }}</td>
          <td>{{ studio.introduction || '-' }}</td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { studioApi } from '../../api/ttms'

const keyword = ref('')
const studios = ref([])
const form = reactive({ name: '', rowCount: 8, colCount: 8, introduction: '' })

async function load() {
  const res = await studioApi.list(keyword.value)
  studios.value = res.data || []
}

async function addStudio() {
  await studioApi.create(form)
  await load()
}

load().catch(() => {
  studios.value = [{ id: 1, name: '1号厅', rowCount: 8, colCount: 8, status: 'ACTIVE', introduction: '默认演出厅' }]
})
</script>
