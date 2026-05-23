// Users-specific JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize sidebar toggle (using common function)
    initSidebarToggle();
    
    // Initialize DataTable
    if (typeof $ !== 'undefined' && $.fn.DataTable) {
        $('#usersTable').DataTable({
            pageLength: 10,
            order: [[0, 'asc']]
        });

        // Fetch employees when modal is shown
        const addUserModal = document.getElementById('addUserModal');
        if (addUserModal) {
            addUserModal.addEventListener('shown.bs.modal', function() {
                fetchEmployees();
            });
        }
    }
});

// Fetch employees for dropdown
function fetchEmployees() {
    fetch('/api/v1/employees')
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById('employee');
            if (select) {
                select.innerHTML = '<option value="">Select Employee</option>';
                if (data.content && data.content.length > 0) {
                    data.content.forEach(employee => {
                        const option = document.createElement('option');
                        option.value = employee.id;
                        option.textContent = `${employee.firstName} ${employee.lastName} (${employee.email})`;
                        select.appendChild(option);
                    });
                }
            }
        })
        .catch(error => {
            console.error('Error fetching employees:', error);
        });
}

// Save user
function saveUser() {
    const employeeId = document.getElementById('employee').value;
    const username = document.getElementById('username').value;
    const temporaryPassword = document.getElementById('temporaryPassword').value;

    if (!employeeId || !username || !temporaryPassword) {
        alert('Please fill in all fields');
        return;
    }

    const userData = {
        employeeId: parseInt(employeeId),
        username: username,
        temporaryPassword: temporaryPassword
    };

    fetch('/api/v1/users/create-with-temp-password', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
    .then(response => {
        if (response.ok) {
            alert('User created successfully');
            bootstrap.Modal.getInstance(document.getElementById('addUserModal')).hide();
            document.getElementById('addUserForm').reset();
            location.reload();
        } else {
            alert('Failed to create user: ' + response.status);
        }
    })
    .catch(error => {
        console.error('Error creating user:', error);
        alert('Failed to create user');
    });
}

function exportUsers() {
    fetch('/api/v1/users/export')
        .then(response => response.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'users.csv';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        })
        .catch(error => {
            console.error('Export error:', error);
            alert('Failed to export users');
        });
}

function uploadCSV() {
    const fileInput = document.getElementById('csvFile');
    const file = fileInput.files[0];
    if (!file) {
        alert('Please select a CSV file');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    fetch('/api/v1/users/upload', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        alert(`Successfully uploaded ${data.count} users`);
        bootstrap.Modal.getInstance(document.getElementById('uploadModal')).hide();
        location.reload();
    })
    .catch(error => {
        console.error('Upload error:', error);
        alert('Failed to upload CSV file');
    });
}

function deleteUser(id) {
    if (confirm('Are you sure you want to delete this user?')) {
        fetch(`/api/v1/users/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                alert('User deleted successfully');
                location.reload();
            } else {
                alert('Failed to delete user');
            }
        })
        .catch(error => {
            console.error('Delete error:', error);
            alert('Failed to delete user');
        });
    }
}

function editUser(id) {
    fetch(`/api/v1/users/${id}`)
        .then(response => response.json())
        .then(user => {
            document.getElementById('editUserId').value = user.id;
            document.getElementById('editUsername').value = user.userName;
            document.getElementById('editEmail').value = user.email;
            document.getElementById('editEnabled').value = user.enabled.toString();

            // Reset role checkboxes
            document.getElementById('roleAdmin').checked = false;
            document.getElementById('roleManager').checked = false;
            document.getElementById('roleEmployee').checked = false;

            // Populate role checkboxes with user's current roles
            if (user.roles && user.roles.length > 0) {
                user.roles.forEach(role => {
                    if (role === 'ADMIN') document.getElementById('roleAdmin').checked = true;
                    if (role === 'MANAGER') document.getElementById('roleManager').checked = true;
                    if (role === 'EMPLOYEE') document.getElementById('roleEmployee').checked = true;
                });
            }

            const modal = new bootstrap.Modal(document.getElementById('editUserModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error fetching user:', error);
            alert('Failed to load user data');
        });
}

function saveUserRoles() {
    const userId = document.getElementById('editUserId').value;
    const enabled = document.getElementById('editEnabled').value === 'true';

    const selectedRoles = [];
    if (document.getElementById('roleAdmin').checked) selectedRoles.push('ADMIN');
    if (document.getElementById('roleManager').checked) selectedRoles.push('MANAGER');
    if (document.getElementById('roleEmployee').checked) selectedRoles.push('EMPLOYEE');

    if (selectedRoles.length === 0) {
        alert('Please select at least one role');
        return;
    }

    const roleUpdateRequest = {
        userId: parseInt(userId),
        roleNames: selectedRoles
    };

    fetch(`/api/v1/users/${userId}/roles`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(roleUpdateRequest)
    })
    .then(response => response.json())
    .then(data => {
        alert('User roles updated successfully');
        bootstrap.Modal.getInstance(document.getElementById('editUserModal')).hide();
        location.reload();
    })
    .catch(error => {
        console.error('Error updating user roles:', error);
        alert('Failed to update user roles');
    });
}
