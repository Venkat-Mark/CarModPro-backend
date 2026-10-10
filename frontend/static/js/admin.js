/**
 * CarModPro - Administrator Dashboard JavaScript
 * Sidebar toggling, responsive collapsing, auto-dismiss alerts, and table helpers
 */

document.addEventListener('DOMContentLoaded', function () {
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebar = document.getElementById('adminSidebar');
    const adminMain = document.querySelector('.admin-main');

    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function () {
            sidebar.classList.toggle('collapsed');
            if (adminMain) {
                adminMain.classList.toggle('expanded');
            }
            // Save state to localStorage
            const isCollapsed = sidebar.classList.contains('collapsed');
            localStorage.setItem('admin_sidebar_collapsed', isCollapsed ? 'true' : 'false');
        });

        // Restore sidebar state
        const savedState = localStorage.getItem('admin_sidebar_collapsed');
        if (savedState === 'true') {
            sidebar.classList.add('collapsed');
            if (adminMain) {
                adminMain.classList.add('expanded');
            }
        }
    }

    // Auto-dismiss alert alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) {
                bsAlert.close();
            }
        }, 5000);
    });
});
