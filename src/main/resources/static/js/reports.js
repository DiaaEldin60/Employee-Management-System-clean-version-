// Reports-specific JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize sidebar toggle (using common function)
    initSidebarToggle();
});

function downloadReport() {
    // Download employees report as CSV
    fetch('/api/v1/employees/export')
        .then(response => response.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'employee_report.csv';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        })
        .catch(error => {
            console.error('Download error:', error);
            alert('Failed to download report');
        });
}
