<template>
  <div id="app">
    <el-container>
      <el-header style="background-color: #409EFF; color: white; display: flex; align-items: center;">
        <h2>通讯录管理系统 - 832302220/832302126</h2>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  margin: 0;
  padding: 0;
}

html, body {
  margin: 0;
  padding: 0;
}
</style>