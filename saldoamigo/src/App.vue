<template>
  <div id="app" class="bg-secondary text-primary min-h-screen">
    <main>
      <HeaderComponent v-if="showHeader" />
      <router-view />
    </main>
  </div>
</template>

<script>
import HeaderComponent from './components/Header.vue';

export default {
  name: 'App',
  components: {
    HeaderComponent,
  },
  computed: {
    showHeader() {
      return !['/login', '/register'].includes(this.$route.path);
    },
  },
};
</script>

<style>
</style>


<style>
@import 'tailwindcss/tailwind.css';

#app {
  font-family:Arial, Helvetica, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
