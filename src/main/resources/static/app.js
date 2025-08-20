const API_URL = 'http://localhost:8080/api/users';
let editingUserId = null;

const form = document.getElementById('user-form');
const tableBody = document.querySelector('#user-table tbody');
const formTitle = document.getElementById('form-title');
const cancelBtn = document.getElementById('cancel-btn');

async function loadUsers() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('Ошибка загрузки');
        const users = await response.json();
        renderUsers(users);
    } catch (error) {
        alert(error.message);
    }
}

function renderUsers(users) {
    tableBody.innerHTML = users.map(user => `
        <tr>
            <td>${user.id}</td>
            <td>${user.name}</td>
            <td>${user.email}</td>
            <td>${user.age}</td>
            <td>
                <button onclick="editUser(${user.id})">Edit</button>
                <button onclick="deleteUser(${user.id})">Delete</button>
            </td>
        </tr>
    `).join('');
}

async function editUser(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`);
        if (!response.ok) throw new Error('Пользователь не найден');

        const user = await response.json();
        editingUserId = user.id;

        document.getElementById('user-id').value = user.id;
        document.getElementById('name').value = user.name;
        document.getElementById('email').value = user.email;
        document.getElementById('age').value = user.age;

        formTitle.textContent = 'Редактировать пользователя';
    } catch (error) {
        alert(error.message);
    }
}

async function deleteUser(id) {
    if (!confirm('Удалить пользователя?')) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Ошибка удаления');

        loadUsers();
    } catch (error) {
        alert(error.message);
    }
}

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const user = {
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        age: parseInt(document.getElementById('age').value)
    };

    try {
        const url = editingUserId ? `${API_URL}/${editingUserId}` : API_URL;
        const method = editingUserId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user)
        });

        if (!response.ok) throw new Error(editingUserId ? 'Ошибка обновления' : 'Ошибка создания');

        resetForm();
        loadUsers();
    } catch (error) {
        alert(error.message);
    }
});

function resetForm() {
    form.reset();
    editingUserId = null;
    formTitle.textContent = 'Создать пользователя';
}

cancelBtn.addEventListener('click', resetForm);

loadUsers();

window.editUser = editUser;
window.deleteUser = deleteUser;