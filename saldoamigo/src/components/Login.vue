<template>
  <div class="flex justify-center items-center min-h-screen bg-gradient-to-r from-green-700 to-teal-700">
    <div class="bg-white p-8 rounded-xl shadow-2xl w-full sm:w-96 lg:w-1/3">
      <h1 class="text-3xl font-bold text-gray-900 mb-8 text-center">Login</h1>
      <form @submit.prevent="login">
        <div class="mb-6">
          <label class="block text-gray-600 font-medium" for="email">Email</label>
          <input 
            v-model="email"
            type="email" 
            id="email" 
            required 
            class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent" 
          />
        </div>
        <div class="mb-6">
          <label class="block text-gray-600 font-medium" for="password">Senha</label>
          <input 
            v-model="password"
            type="password" 
            id="password" 
            required 
            class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent" 
          />
        </div>
        <button 
          type="submit" 
          :disabled="isLoading"
          class="w-full bg-green-600 hover:bg-green-700 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 transform hover:scale-105">
          <span v-if="isLoading">Carregando...</span>
          <span v-else>Entrar</span>
        </button>
      </form>
      <p v-if="errorMessage" class="text-red-600 text-center mt-4 font-medium">{{ errorMessage }}</p>
      <p class="text-gray-600 text-center mt-6">
        Não tem uma conta? <a href="/register" class="text-green-600 hover:underline font-medium">Registrar</a>
      </p>
    </div>
  </div>
</template>

<script>
import api from '../api';

export default {
  name: 'LoginForm',
  data() {
    return {
      email: '',
      password: '',
      isLoading: false,
      errorMessage: ''
    };
  },
  methods: {
    async login() {
      if (this.email && this.password) {
        this.isLoading = true;
        this.errorMessage = '';
        try {
          const response = await api.post('/auth/login', {
            email: this.email,
            password: this.password
          });

          const token = response.data.token;
          const role = response.data.role;
          const id = response.data.id;
          const expiresIn = response.data.expiresIn || 7200000;

          localStorage.setItem('authToken', token);
          localStorage.setItem('userRole', role);
          localStorage.setItem('tokenExpiration', Date.now() + expiresIn);
          localStorage.setItem('userId', id);

          this.$router.push('/');
        } catch (error) {
          if (error.response && error.response.status === 401) {
            this.errorMessage = 'Credenciais inválidas. Tente novamente.';
          } else if (error.response && error.response.status === 403) {
            this.errorMessage = 'Erro de autorização. Tente novamente.';
          } else {
            this.errorMessage = error.response?.data?.message || 'Erro de conexão. Verifique sua rede e tente novamente.';
          }
        } finally {
          this.isLoading = false;
        }
      } else {
        this.errorMessage = 'Por favor, preencha todos os campos.';
      }
    }
  }
};
</script>

<style scoped>
label, h1, button {
  font-family: 'Roboto', sans-serif;
  font-weight: bold;
}
p {
  font-family: 'Roboto', sans-serif;
}
.bg-gradient-to-r {
  background: linear-gradient(90deg, #2F4F4F, #1B3A2B); 
}
.bg-white {
  background-color: #ffffff;
}
.text-green-600 {
  color: #16a34a;
}
.text-green-700 {
  color: #15803d;
}
.focus:ring-green-500 {
  ring-color: #16a34a;
}
.focus:ring-2 {
  box-shadow: 0 0 0 2px rgba(22, 163, 74, 0.5);
}
.text-red-600 {
  color: #dc2626;
}
</style>
