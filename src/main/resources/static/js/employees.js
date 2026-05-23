// Employee-specific JavaScript functions
console.log('employees.js loaded successfully');

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM Content Loaded');

    // Clear tbody before initializing DataTables
    const tbody = document.getElementById('employeesTableBody');
    if (tbody) {
        tbody.innerHTML = '';
    }

    // Initialize DataTable
    if (typeof $ !== 'undefined' && $.fn.DataTable) {
        $('#employeesTable').DataTable({
            responsive: true,
            pageLength: -1,
            lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
            ordering: false,
            info: true,
            paging: true
        });
    }

    // Fetch employees
    console.log('Fetching employees...');
    fetchEmployees();

    // Clear form when "Add Employee" button is clicked
    const addEmployeeBtn = document.querySelector('button[data-bs-target="#addEmployeeModal"]');
    if (addEmployeeBtn) {
        addEmployeeBtn.addEventListener('click', function() {
            const form = document.getElementById('addEmployeeForm');
            if (form) {
                console.log('Clearing form on Add Employee click');
                delete form.dataset.employeeId;
                form.reset();
            }
        });
    }
});

// View Toggle
function setView(view) {
    const tableView = document.getElementById('tableView');
    const cardsView = document.getElementById('cardsView');
    const viewButtons = document.querySelectorAll('.view-toggle button');
    
    if (viewButtons) {
        viewButtons.forEach(btn => btn.classList.remove('active'));
    }
    
    if (tableView && cardsView) {
        if (view === 'table') {
            tableView.style.display = 'block';
            cardsView.style.display = 'none';
            if (viewButtons && viewButtons[0]) {
                viewButtons[0].classList.add('active');
            }
        } else {
            tableView.style.display = 'none';
            cardsView.style.display = 'flex';
            if (viewButtons && viewButtons[1]) {
                viewButtons[1].classList.add('active');
            }
        }
    }
}

// CRUD Functions
function viewEmployee(id) {
    console.log('View employee:', id);
    // Implement view employee logic
}

function editEmployee(id) {
    fetch(`/api/v1/employees/${id}`)
        .then(response => response.json())
        .then(employee => {
            document.getElementById('firstName').value = employee.firstName;
            document.getElementById('lastName').value = employee.lastName;
            document.getElementById('department').value = employee.department;
            document.getElementById('jobTitle').value = employee.jobTitle;
            document.getElementById('phone').value = employee.phoneNumber;
            document.getElementById('salary').value = employee.salary;
            document.getElementById('hireDate').value = employee.hireDate;

            // Open the modal
            const modal = new bootstrap.Modal(document.getElementById('addEmployeeModal'));
            modal.show();

            // Store the employee ID for update
            document.getElementById('addEmployeeForm').dataset.employeeId = id;
        })
        .catch(error => {
            console.error('Error fetching employee:', error);
            alert('Failed to load employee data');
        });
}

function deleteEmployee(id) {
    if (confirm('Are you sure you want to delete this employee?')) {
        showLoading();
        fetch(`/api/v1/employees/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                hideLoading();
                alert('Employee deleted successfully');
                fetchEmployees();
            } else {
                hideLoading();
                response.text().then(text => {
                    console.error('Delete failed:', response.status, text);
                    alert('Failed to delete employee: ' + response.status);
                });
            }
        })
        .catch(error => {
            console.error('Delete error:', error);
            hideLoading();
            alert('Failed to delete employee');
        });
    }
}

function saveEmployee() {
    try {
        showLoading();
        const form = document.getElementById('addEmployeeForm');
        if (!form) {
            alert('Form not found!');
            hideLoading();
            return;
        }

        const employeeId = form.dataset.employeeId;
        const isUpdate = employeeId !== undefined;

        console.log('Form employeeId:', employeeId);
        console.log('isUpdate:', isUpdate);

        const firstName = document.getElementById('firstName');
        const lastName = document.getElementById('lastName');
        const phone = document.getElementById('phone');
        const department = document.getElementById('department');
        const jobTitle = document.getElementById('jobTitle');
        const salary = document.getElementById('salary');
        const hireDate = document.getElementById('hireDate');

        if (!firstName || !lastName || !department || !jobTitle || !salary || !hireDate) {
            alert('Some form fields not found!');
            hideLoading();
            return;
        }

        const employeeData = {
            firstName: firstName.value,
            lastName: lastName.value,
            phoneNumber: phone ? phone.value : '',
            department: department.value,
            jobTitle: jobTitle.value,
            salary: parseFloat(salary.value),
            hireDate: hireDate.value
        };

        // Only include username and temporaryPassword when creating a new employee
        if (!isUpdate) {
            const username = document.getElementById('username');
            const temporaryPassword = document.getElementById('temporaryPassword');

            console.log('=== CREATING NEW EMPLOYEE ===');
            console.log('isUpdate:', isUpdate);
            console.log('username element:', username);
            console.log('temporaryPassword element:', temporaryPassword);
            console.log('username value:', username ? username.value : 'N/A');
            console.log('temporaryPassword value:', temporaryPassword ? temporaryPassword.value : 'N/A');

            if (!username || !temporaryPassword) {
                console.error('Username or password field not found!');
                alert('Username or password field not found!');
                hideLoading();
                return;
            }

            if (!username.value || !temporaryPassword.value) {
                console.error('Username or temporary password is empty!');
                alert('Username and temporary password are required!');
                hideLoading();
                return;
            }

            employeeData.username = username.value;
            employeeData.temporaryPassword = temporaryPassword.value;

            // Collect selected roles
            const roles = [];
            const roleEmployee = document.getElementById('roleEmployee');
            const roleManager = document.getElementById('roleManager');
            const roleAdmin = document.getElementById('roleAdmin');

            if (roleEmployee && roleEmployee.checked) {
                roles.push('EMPLOYEE');
            }
            if (roleManager && roleManager.checked) {
                roles.push('MANAGER');
            }
            if (roleAdmin && roleAdmin.checked) {
                roles.push('ADMIN');
            }

            employeeData.roles = roles;
            console.log('Added roles to employeeData:', roles);

            // Collect selected authorities
            const authorities = [];
            const authRead = document.getElementById('authRead');
            const authWrite = document.getElementById('authWrite');
            const authDelete = document.getElementById('authDelete');
            const authManageUsers = document.getElementById('authManageUsers');
            const authManageRoles = document.getElementById('authManageRoles');

            if (authRead && authRead.checked) {
                authorities.push('READ');
            }
            if (authWrite && authWrite.checked) {
                authorities.push('WRITE');
            }
            if (authDelete && authDelete.checked) {
                authorities.push('DELETE');
            }
            if (authManageUsers && authManageUsers.checked) {
                authorities.push('MANAGE_USERS');
            }
            if (authManageRoles && authManageRoles.checked) {
                authorities.push('MANAGE_ROLES');
            }

            employeeData.authorities = authorities;
            console.log('Added authorities to employeeData:', authorities);
            console.log('Added username and password to employeeData');
        } else {
            console.log('=== UPDATING EXISTING EMPLOYEE ===');
            console.log('isUpdate:', isUpdate);
            console.log('employeeId:', employeeId);
        }

        const url = isUpdate ? `/api/v1/employees/${employeeId}` : '/api/v1/employees';
        const method = isUpdate ? 'PUT' : 'POST';

        console.log('Sending request:', method, url);
        console.log('Request body:', employeeData);

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(employeeData)
        })
        .then(response => {
            if (response.ok) {
                hideLoading();
                alert(isUpdate ? 'Employee updated successfully' : 'Employee created successfully');
                bootstrap.Modal.getInstance(document.getElementById('addEmployeeModal')).hide();
                delete form.dataset.employeeId;
                form.reset();
                fetchEmployees();
            } else {
                hideLoading();
                response.text().then(text => {
                    console.error('Save failed:', response.status, text);
                    alert('Failed to save employee: ' + response.status);
                });
            }
        })
        .catch(error => {
            console.error('Save error:', error);
            hideLoading();
            alert('Failed to save employee: ' + error.message);
        });
    } catch (error) {
        console.error('Error in saveEmployee:', error);
        hideLoading();
        alert('Error: ' + error.message);
    }
}

function applyFilters() {
    showLoading();
    // Implement filter logic
    setTimeout(() => {
        hideLoading();
        // Refresh table with filters
    }, 500);
}

function exportData() {
    showLoading();
    fetch('/api/v1/employees/export')
        .then(response => response.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'employees.csv';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
            hideLoading();
        })
        .catch(error => {
            console.error('Export error:', error);
            hideLoading();
            alert('Failed to export employees');
        });
}

// Fetch and display employees
function fetchEmployees(filters = {}) {
    console.log('Fetching employees...', filters);
    
    // Build query parameters
    const params = new URLSearchParams({
        page: filters.page || 0,
        size: filters.size || 100,
        sortBy: filters.sortBy || 'id'
    });
    
    // Add filter parameters if they exist
    if (filters.firstName) params.append('firstName', filters.firstName);
    if (filters.lastName) params.append('lastName', filters.lastName);
    if (filters.email) params.append('email', filters.email);
    if (filters.department) params.append('department', filters.department);
    if (filters.jobTitle) params.append('jobTitle', filters.jobTitle);
    if (filters.phoneNumber) params.append('phoneNumber', filters.phoneNumber);
    if (filters.minSalary) params.append('minSalary', filters.minSalary);
    if (filters.maxSalary) params.append('maxSalary', filters.maxSalary);
    if (filters.hireDateFrom) params.append('hireDateFrom', filters.hireDateFrom);
    if (filters.hireDateTo) params.append('hireDateTo', filters.hireDateTo);
    
    fetch(`/api/v1/employees?${params.toString()}`)
        .then(response => {
            console.log('Response status:', response.status);
            if (!response.ok) {
                throw new Error('HTTP error! status: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            console.log('Response data:', data);
            const tbody = document.getElementById('employeesTableBody');
            if (!tbody) return;

            const employees = data.content || data;
            
            // Clear existing DataTable instance if it exists
            if (typeof $ !== 'undefined' && $.fn.DataTable.isDataTable('#employeesTable')) {
                $('#employeesTable').DataTable().destroy();
            }
            
            // Clear tbody before adding new rows
            tbody.innerHTML = '';
            
            // Update record count
            const recordCount = document.getElementById('recordCount');
            if (recordCount) {
                recordCount.textContent = data.totalElements || employees.length;
            }

            if (Array.isArray(employees) && employees.length > 0) {
                employees.forEach(employee => {
                    const initials = employee.firstName.charAt(0) + employee.lastName.charAt(0);
                    const adminButtons = isAdmin ? `
                        <button class="btn-action btn-warning" onclick="editEmployee(${employee.id})">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn-action btn-danger" onclick="deleteEmployee(${employee.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    ` : '';
                    const row = `
                        <tr>
                            <td>${employee.id}</td>
                            <td>
                                <div class="d-flex align-items-center">
                                    <div class="employee-avatar me-3">
                                        ${initials}
                                    </div>
                                    <div>
                                        <strong>${employee.firstName} ${employee.lastName}</strong>
                                    </div>
                                </div>
                            </td>
                            <td>${employee.email}</td>
                            <td>${employee.department}</td>
                            <td>${employee.jobTitle}</td>
                            <td>${employee.hireDate}</td>
                            ${isAdmin ? `<td>
                                <div class="table-actions">
                                    <button class="btn-action btn-info" onclick="viewEmployee(${employee.id})">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                    ${adminButtons}
                                </div>
                            </td>` : ''}
                        </tr>
                    `;
                    tbody.innerHTML += row;
                });
                
                // Reinitialize DataTable after adding rows
                if (typeof $ !== 'undefined' && $.fn.DataTable) {
                    $('#employeesTable').DataTable({
                        responsive: true,
                        pageLength: -1,
                        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
                        ordering: false,
                        info: true,
                        paging: true
                    });
                }
            } else {
                const colspan = isAdmin ? '7' : '6';
                tbody.innerHTML = `<tr><td colspan="${colspan}" class="text-center">No employees found</td></tr>`;
            }
        })
        .catch(error => {
            console.error('Error fetching employees:', error);
            const tbody = document.getElementById('employeesTableBody');
            if (tbody) {
                const colspan = isAdmin ? '7' : '6';
                tbody.innerHTML = '<tr><td colspan="' + colspan + '" class="text-center text-danger">Failed to load employees: ' + error.message + '</td></tr>';
            }
        });
}

// CSV Upload Functions
let validatedEmployees = [];

function validateCSV() {
    const fileInput = document.getElementById('csvFile');
    const file = fileInput.files[0];
    if (!file) {
        alert('Please select a CSV file');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    showLoading();
    fetch('/api/v1/employees/validate', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        hideLoading();
        validatedEmployees = data.employees;
        showPreview(data.employees);
    })
    .catch(error => {
        console.error('Validation error:', error);
        hideLoading();
        alert('Failed to validate CSV file');
    });
}

function showPreview(employees) {
    const tbody = document.getElementById('previewTableBody');
    if (!tbody) return;
    tbody.innerHTML = '';

    let hasErrors = false;
    employees.forEach(emp => {
        const row = document.createElement('tr');
        row.className = emp.valid ? '' : 'table-danger';
        row.innerHTML = `
            <td>${emp.lineNumber}</td>
            <td>${emp.firstName} ${emp.lastName}</td>
            <td>${emp.email}</td>
            <td>${emp.department}</td>
            <td>${emp.jobTitle}</td>
            <td>${emp.phoneNumber}</td>
            <td>${emp.salary}</td>
            <td>${emp.hireDate}</td>
        `;
        tbody.appendChild(row);
        if (!emp.valid) hasErrors = true;
    });

    document.getElementById('uploadStep1').style.display = 'none';
    document.getElementById('uploadStep2').style.display = 'block';
    document.getElementById('validateBtn').style.display = 'none';
    document.getElementById('confirmBtn').style.display = 'inline-block';

    if (hasErrors) {
        alert('Some entries have errors. Please fix the CSV file and try again.');
        document.getElementById('confirmBtn').disabled = true;
    }
}

function confirmUpload() {
    const fileInput = document.getElementById('csvFile');
    const file = fileInput.files[0];
    if (!file) {
        alert('Please select a CSV file');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    showLoading();
    fetch('/api/v1/employees/upload', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        hideLoading();
        alert(`Successfully uploaded ${data.count} employees`);
        bootstrap.Modal.getInstance(document.getElementById('uploadModal')).hide();
        document.getElementById('csvFile').value = '';
        resetUploadModal();
        fetchEmployees();
    })
    .catch(error => {
        console.error('Upload error:', error);
        hideLoading();
        alert('Failed to upload employees');
    });
}

// Apply filters function
function applyFilters() {
    console.log('Applying filters...');
    
    // Get filter values
    const department = document.getElementById('departmentFilter').value;
    const quickSearch = document.getElementById('quickSearch').value;
    
    // Build filters object
    const filters = {};
    
    if (department) {
        filters.department = department;
    }
    
    if (quickSearch) {
        // Try to determine if it's name or email search
        if (quickSearch.includes('@')) {
            filters.email = quickSearch;
        } else {
            // Split into first and last name if space is present
            const nameParts = quickSearch.trim().split(' ');
            if (nameParts.length > 1) {
                filters.firstName = nameParts[0];
                filters.lastName = nameParts[nameParts.length - 1];
            } else {
                filters.firstName = quickSearch;
            }
        }
    }
    
    // Fetch employees with filters
    fetchEmployees(filters);
}

// Clear filters function
function clearFilters() {
    console.log('Clearing filters...');
    
    // Reset filter form
    document.getElementById('departmentFilter').value = '';
    document.getElementById('quickSearch').value = '';
    
    // Fetch all employees
    fetchEmployees();
}

// Add event listeners for filter inputs
document.addEventListener('DOMContentLoaded', function() {
    // Add enter key support for quick search
    const quickSearchInput = document.getElementById('quickSearch');
    if (quickSearchInput) {
        quickSearchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                applyFilters();
            }
        });
    }
    
    // Add change event for department filter
    const departmentFilter = document.getElementById('departmentFilter');
    if (departmentFilter) {
        departmentFilter.addEventListener('change', applyFilters);
    }
});

function resetUploadModal() {
    document.getElementById('uploadStep1').style.display = 'block';
    document.getElementById('uploadStep2').style.display = 'none';
    document.getElementById('validateBtn').style.display = 'inline-block';
    document.getElementById('confirmBtn').style.display = 'none';
    document.getElementById('confirmBtn').disabled = false;
}

// Mobile Responsive
function adjustForMobile() {
    if (window.innerWidth <= 768) {
        // Adjust DataTable for mobile
        if (typeof $ !== 'undefined' && $.fn.DataTable.isDataTable('#employeesTable')) {
            $('#employeesTable').DataTable().responsive.rebuild();
        }
    }
}

window.addEventListener('resize', adjustForMobile);
adjustForMobile();
