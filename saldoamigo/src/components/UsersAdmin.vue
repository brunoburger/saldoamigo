<template>
  <div class="flex justify-center items-center min-h-screen bg-gradient-to-r from-green-700 to-teal-700 p-8">
    <div class="w-full max-w-full overflow-x-auto">
      <h2 class="text-3xl font-semibold text-white mb-8 text-center">Lista de Usuários</h2>

      <div v-if="errorMessage" class="text-red-600 text-lg mb-6 text-center">
        {{ errorMessage }}
      </div>
      <div class="m-6 flex justify-end">
        <button 
          @click="logout" 
          class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg shadow-md focus:outline-none focus:ring-2 focus:ring-red-500 transition duration-300 ease-in-out transform hover:scale-105">
          Deslogar
        </button>
      </div>
      <table class="min-w-full bg-white rounded-lg shadow-md ">
        <thead class="bg-teal-600 text-white">
          <tr>
            <th class="px-4 py-3 text-center text-lg font-bold sm:px-6 rounded-tl-lg">ID</th>
            <th class="px-4 py-3 text-center text-lg font-bold sm:px-6">Nome</th>
            <th class="px-4 py-3 text-center text-lg font-bold sm:px-6">Email</th>
            <th class="px-4 py-3 text-center text-lg font-bold sm:px-6">Telefone</th>
            <th class="px-4 py-3 text-center text-lg font-bold sm:px-6">Role</th>
            <th class="px-4 py-3 text-center text-lg font-bold sm:px-6 rounded-tr-lg">Ações</th>
          </tr>
        </thead>
        <tbody class="text-gray-900">
          <tr v-for="user in users" :key="user.id">
            <td class="px-4 py-4 text-center sm:px-6">{{ user.id }}</td>
            <td class="px-4 py-4 text-center sm:px-6">{{ user.username }}</td>
            <td class="px-4 py-4 text-center sm:px-6">{{ user.email }}</td>
            <td class="px-4 py-4 text-center sm:px-6">{{ user.phone }}</td>
            <td class="px-4 py-4 text-center sm:px-6">{{ user.role.toUpperCase() }}</td>
            <td class="px-4 py-4 justify-center flex sm:px-6">
              <button 
                @click="editUser(user.id)" 
                class="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg shadow-md focus:outline-none focus:ring-2 focus:ring-green-500 transition duration-300 ease-in-out transform hover:scale-105">
                Editar
              </button>
              <button 
                @click="deleteUser(user.id)" 
                class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg shadow-md focus:outline-none focus:ring-2 focus:ring-red-500 transition duration-300 ease-in-out transform hover:scale-105 ml-2">
                Deletar
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Paginação -->
      <div class="mt-6 flex justify-center items-center gap-4">
        <button 
          :disabled="!pagination.prev"
          @click="goToPage(pagination.currentPage - 1)"
          class="px-4 py-2 bg-teal-600 text-white rounded-lg shadow-md hover:bg-teal-700 transition duration-300 ease-in-out transform hover:scale-100"
        >
          Anterior
        </button>
        
        <span class="text-white text-lg font-bold">{{ pagination.currentPage + 1 }} de {{ pagination.totalPages }}</span>
        
        <button 
          :disabled="!pagination.next"
          @click="goToPage(pagination.currentPage + 1)"
          class="px-4 py-2 bg-teal-600 text-white rounded-lg shadow-md hover:bg-teal-700 transition duration-300 ease-in-out transform hover:scale-100"
        >
          Próximo
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../api';

export default {
  name: 'UserList',
  data() {
    return {
      users: [],
      errorMessage: null,
      pagination: {
        currentPage: 0,
        totalPages: 0,
        prev: false,
        next: true
      }
    };
  },
  async created() {
    await this.fetchUsers();
  },
  methods: {
    async fetchUsers(page = 0) {
      try {
        const response = await api.get(`/users?page=${page}&size=10&sort=username,asc`);
        this.users = response.data._embedded.userDtoList;
        this.pagination.currentPage = page;
        this.pagination.totalPages = response.data.page.totalPages;
        this.pagination.prev = page > 0;
        this.pagination.next = page < this.pagination.totalPages - 1;
      } catch (error) {
        this.errorMessage = 'Erro ao carregar a lista de usuários.';
        console.error(error);
      }
    },
    editUser(userId) {
      this.$router.push(`/edit/user/` + userId);
    },
    deleteUser(userId) {
      if (confirm("Tem certeza que deseja excluir este usuário?")) {
        api.delete(`/users/${userId}`)
          .then(() => {
            this.fetchUsers(this.pagination.currentPage);
          })
          .catch((error) => {
            this.errorMessage = 'Erro ao excluir o usuário.';
            console.error(error);
          });
      }
    },
    goToPage(page) {
      this.fetchUsers(page);
    },

    // Método de logout
    logout() {
      // Limpar o localStorage
      localStorage.clear();
      
      // Redirecionar para a página de login
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
.bg-gradient-to-r {
  background: linear-gradient(90deg, #2F4F4F, #1B3A2B);
}
.text-teal-600 {
  color: #0d9488;
}
.text-red-600 {
  color: #dc2626;
}
.bg-teal-600 {
  background-color: #0d9488;
}
.bg-teal-700 {
  background-color: #057f77;
}
.focus:ring-teal-500 {
  ring-color: #0d9488;
}
</style>
