<template>
  <div class="min-h-screen bg-gradient-to-r from-green-700 to-teal-700 flex flex-col items-center py-8">
    <div class="bg-white p-8 rounded-xl shadow-2xl w-full sm:w-3/4 lg:w-1/2">
      <h1 class="text-3xl font-bold text-gray-900 mb-6 text-center">Lista de Grupos</h1>

      <button 
        @click="checkAccountAndRedirect"
        class="w-full bg-green-600 hover:bg-green-700 text-white font-semibold py-3 px-4 rounded-lg mb-4 transition duration-200 transform hover:scale-105">
        Adicionar Novo Grupo
      </button>

      <p v-if="errorMessage" class="text-red-600 text-center mt-4 font-medium">{{ errorMessage }}</p>

      <ul v-if="groups.length" class="space-y-4">
        <li 
          v-for="group in groups" :key="group.id" class="flex justify-between items-center bg-gray-100 p-4 rounded-lg shadow">
          <span @click="goToGroup(group.id)" class="cursor-pointer"><span class="text-lg font-semibold">{{ group.name }}</span> | <span class="text-gray-600 font-small">{{ group.description }}</span></span>
          <div class="flex space-x-4">
            <button 
              @click.stop="editGroup(group.id)" 
              class="bg-green-600 hover:bg-green-700 text-white py-2 px-4 rounded-lg transition">
              Editar
            </button>
            <button 
              @click.stop="deleteGroup(group.id)" 
              class="bg-red-600 hover:bg-red-700 text-white py-2 px-4 rounded-lg transition">
              Excluir
            </button>
          </div>
        </li>
      </ul>

      <p v-else class="text-center text-gray-600 mt-4">Nenhum grupo encontrado.</p>

      <div class="pagination flex justify-between mt-6">
        <button 
          :disabled="!pagination.prev" 
          @click="goToPage(pagination.currentPage - 1)" 
          class="bg-gray-300 hover:bg-gray-400 text-gray-900 font-semibold py-2 px-4 rounded-lg transition"
          :class="{'opacity-50 cursor-not-allowed': !pagination.prev}">
          Anterior
        </button>
        <span class="text-gray-600 font-medium">
          Página {{ pagination.currentPage + 1 }} de {{ pagination.totalPages }}
        </span>
        <button 
          :disabled="!pagination.next" 
          @click="goToPage(pagination.currentPage + 1)" 
          class="bg-gray-300 hover:bg-gray-400 text-gray-900 font-semibold py-2 px-4 rounded-lg transition"
          :class="{'opacity-50 cursor-not-allowed': !pagination.next}">
          Próxima
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../api';

export default {
  name: 'GroupList',
  data() {
    return {
      groups: [],
      errorMessage: null,
      pagination: {
        currentPage: 0,
        totalPages: 0,
        prev: false,
        next: true,
      },
    };
  },
  async created() {
    await this.fetchGroups();
  },
  methods: {
    async fetchGroups(page = 0) {
      try {
        const response = await api.get(`/groups/find/user/${localStorage.getItem('userId')}`);
        this.groups = response.data?._embedded?.groupDtoList || [];
        this.pagination.currentPage = page;
        this.pagination.totalPages = response.data.page.totalPages;
        this.pagination.prev = page > 0;
        this.pagination.next = page < this.pagination.totalPages - 1;
      } catch (error) {
        this.errorMessage = 'Erro ao carregar a lista de grupos.';
        console.error(error);
      }
    },

    async checkAccountAndRedirect() {
      try {
        const userId = localStorage.getItem('userId');
        const response = await api.get(`/accounts/find/user/${userId}`);
        if (response.data && response.data.page.totalElements > 0) {
          this.$router.push('/add/group');
        } else {
          this.errorMessage = 'Você precisa ter uma conta cadastrada para adicionar um grupo.';
        }
      } catch (error) {
        this.errorMessage = 'Erro ao verificar a conta do usuário.';
        console.error(error);
      }
    },

    editGroup(groupId) {
      this.$router.push(`/edit/group/${groupId}`);
    },
    
    deleteGroup(groupId) {
      if (confirm('Tem certeza que deseja excluir este grupo?')) {
        api.delete(`/groups/${groupId}`)
          .then(() => {
            this.fetchGroups(this.pagination.currentPage);
          })
          .catch((error) => {
            this.errorMessage = 'Erro ao excluir o grupo.';
            console.error(error);
          });
      }
    },
    
    goToPage(page) {
      this.fetchGroups(page);
    },
    
    goToGroup(groupId) {
      this.$router.push(`/group/${groupId}`);
    },
  },
};
</script>
