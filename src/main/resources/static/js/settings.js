// Settings-specific JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize sidebar toggle (using common function)
    initSidebarToggle();
    
    // Settings Navigation
    document.querySelectorAll('.settings-nav .nav-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            document.querySelectorAll('.settings-nav .nav-link').forEach(l => l.classList.remove('active'));
            this.classList.add('active');
            
            const target = this.getAttribute('href');
            document.querySelector(target).scrollIntoView({ behavior: 'smooth' });
        });
    });
});
