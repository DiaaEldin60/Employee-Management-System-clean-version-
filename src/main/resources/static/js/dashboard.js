// Dashboard-specific JavaScript

// Sidebar Toggle (using common function)
document.addEventListener('DOMContentLoaded', function() {
    initSidebarToggle();
});

// Employee Chart
document.addEventListener('DOMContentLoaded', function() {
    const employeeCtx = document.getElementById('employeeChart');
    if (employeeCtx) {
        new Chart(employeeCtx.getContext('2d'), {
            type: 'line',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                datasets: [{
                    label: 'New Employees',
                    data: [12, 19, 15, 25, 22, 30],
                    borderColor: 'rgb(75, 192, 192)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    tension: 0.1
                }, {
                    label: 'Active Users',
                    data: [150, 160, 170, 180, 185, 189],
                    borderColor: 'rgb(255, 99, 132)',
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    tension: 0.1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top',
                    }
                }
            }
        });
    }
});

// Department Chart
document.addEventListener('DOMContentLoaded', function() {
    const departmentCtx = document.getElementById('departmentChart');
    if (departmentCtx) {
        /*<![CDATA[*/
        const departmentData = typeof departmentData !== 'undefined' ? departmentData : [];
        const departmentLabels = departmentData.map(item => item[0]);
        const departmentCounts = departmentData.map(item => item[1]);
        /*]]>*/
        
        new Chart(departmentCtx.getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: departmentLabels,
                datasets: [{
                    data: departmentCounts,
                    backgroundColor: [
                        '#FF6384',
                        '#36A2EB',
                        '#FFCE56',
                        '#4BC0C0',
                        '#9966FF',
                        '#FF9F40'
                    ]
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                    }
                }
            }
        });
    }
});

// Mobile responsiveness adjustments
function adjustForMobile() {
    if (window.innerWidth <= 768) {
        // Adjust chart sizes for mobile
        if (typeof Chart !== 'undefined') {
            Chart.defaults.font.size = 10;
        }
    } else {
        if (typeof Chart !== 'undefined') {
            Chart.defaults.font.size = 12;
        }
    }
}

window.addEventListener('resize', adjustForMobile);
adjustForMobile();

// Initialize tooltips (using common function)
document.addEventListener('DOMContentLoaded', function() {
    initTooltips();
});
