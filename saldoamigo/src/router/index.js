import { createRouter, createWebHistory } from 'vue-router';
import Login from '@/components/Login.vue';
import Register from '@/components/Register.vue';
import Users from '@/components/Users.vue';
import UsersAdmin from '@/components/UsersAdmin.vue';
import UsersForm from '@/components/UsersForm.vue';
import Groups from '@/components/Groups.vue';
import GroupsForm from '@/components/GroupsForm.vue';
import GroupsAdmin from '@/components/GroupsAdmin.vue';
import Group from '@/components/Group.vue';
import Account from '@/components/AccountsForm.vue';
import Accounts from '@/components/Accounts.vue';

const routes = [
  { path: '/login', component: Login},
  { path: '/register', component: Register},
  
  { path: '/user', component: Users, meta: { requiresAuth: true } },
  { path: '/admin/users', component: UsersAdmin, meta: { requiresAuth: true } },
  { path: '/edit/user/:id', component: UsersForm, meta: { requiresAuth: true } },
  { path: '/admin/add/user', component: UsersForm, meta: { requiresAuth: true } },

  { path: '/groups', component: Groups, meta: { requiresAuth: true } },
  { path: '/admin/groups', component: GroupsAdmin, meta: { requiresAuth: true } },
  { path: '/edit/group/:id', component: GroupsForm},
  { path: '/add/group', component: GroupsForm},
  { path: '/group/:id', component: Group},

  { path: '/account', component: Account },
  { path: '/admin/accounts', component: Accounts, meta: { requiresAuth: true } },
  { path: '/admin/add/account', component: Account, meta: { requiresAuth: true } },
  { path: '/edit/account/:id', component: Account, meta: { requiresAuth: true } },

  { path: '/', redirect: () => { return { path: '/groups' } } },
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

function isTokenExpired() {
  const expirationTime = localStorage.getItem('tokenExpiration');
  if (!expirationTime) return true;
  const now = new Date().getTime();
  return now > expirationTime;
}

router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('authToken');
  const isAdmin = localStorage.getItem('userRole') === 'ROLE_ADMIN';

  if (to.matched.some(record => record.meta.requiresAuth) && (!isLoggedIn || isTokenExpired())) {
    localStorage.removeItem('authToken');
    localStorage.removeItem('tokenExpiration');
    next('/login');
  } 
  else if (to.matched.some(record => record.meta.requiresAdmin) && !isAdmin) {
    next('/groups');
  } else {
    next();
  }
});

export default router;
