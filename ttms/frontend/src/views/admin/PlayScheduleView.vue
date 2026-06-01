<template>
  <section class="grid cols-2">
    <article class="card">
      <h2>剧目管理</h2>
      <div class="form-grid">
        <input v-model="play.name" placeholder="剧目名称" />
        <input v-model="play.type" placeholder="类型" />
        <input v-model="play.language" placeholder="语种" />
        <input v-model.number="play.basePrice" type="number" placeholder="基准票价" />
      </div>
      <button class="btn" @click="addPlay">新增剧目</button>
    </article>
    <article class="card">
      <h2>演出计划</h2>
      <div class="form-grid">
        <input v-model.number="schedule.studioId" type="number" placeholder="演出厅ID" />
        <input v-model.number="schedule.playId" type="number" placeholder="剧目ID" />
        <input v-model="schedule.showTime" type="datetime-local" />
        <input v-model.number="schedule.ticketPrice" type="number" placeholder="票价" />
      </div>
      <button class="btn" @click="addSchedule">创建排期并生成票</button>
    </article>
  </section>
  <section class="card" style="margin-top:16px">
    <h2>排期冲突检测规则</h2>
    <p class="muted">同一演出厅在剧目时长覆盖范围内不能存在另一场有效演出计划。服务端创建排期时自动检测。</p>
  </section>
</template>

<script setup>
import { reactive } from 'vue'
import { playApi, scheduleApi } from '../../api/ttms'

const play = reactive({ name: '', type: '话剧', language: '中文', durationMinutes: 120, basePrice: 100, introduction: '' })
const schedule = reactive({ studioId: 1, playId: 1, showTime: '', ticketPrice: 100 })

async function addPlay() {
  await playApi.create(play)
}

async function addSchedule() {
  await scheduleApi.create({ ...schedule })
}
</script>
